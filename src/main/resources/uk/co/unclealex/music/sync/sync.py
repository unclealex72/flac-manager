#!/usr/bin/python
import sys
import string
import gpod
import datetime
import os
#import pymtp

class device_file:
  def __init__(self, id, path, last_modified):
    self.id = id
    self.path = path
    self.last_modified = last_modified
    
class synchroniser:
  def run(self):
    self.out("Waiting for input", False)
    keep_running = True
    while keep_running:
      line = sys.stdin.readline()
      line = line.strip()
      if line == "" or line == "QUIT":
        self.quit()
        self.okay();
        keep_running = False
      else:
        parts = string.split(line, '|')
        cmd = parts[0]
        if cmd == "LIST":
          self.list()
          self.okay();
        elif cmd == "ADD":
          self.add(self.flatten_relative_path(parts[1]), parts[2])
          self.okay();
        elif cmd == "REMOVE":
          self.remove(parts[1])
          self.okay();
      
  def okay(self):
    self.out("OK")
    
  def list(self):
    for device_file in self.list_device_files():
      self.out("%s|%s|%s" % (device_file.id, self.unflatten_relative_path(device_file.path), device_file.last_modified))
      
  def out(self, message, stdout = True):
    if stdout:
      print message
      sys.stdout.flush()
    print >> sys.stderr, message

  def flatten_relative_path(self, path):
    return string.replace(path, "/", "_")
  
  def unflatten_relative_path(self, path):
    return string.replace(path, "_", "/")
  
class ipod_synchroniser(synchroniser):
  def __init__(self, mount_point):
    self.db = gpod.Database(mount_point)
    self.tracks = {}
    
  def list_device_files(self):
    device_files = []
    self.tracks = {}
    for track in self.db:
      id = str(track['id'])
      this_device_file = device_file(id, track['comment'], track['time_modified'].isoformat())
      self.tracks[id] = track
      device_files.append(this_device_file)
    return device_files
  
  def add(self, path, audio_file):
    track = self.db.new_Track(filename=audio_file)
    track['comment'] = path
    track['time_modified'] = os.path.getmtime(audio_file)
    self.db.copy_delayed_files()
  
  def remove(self, id):
    self.db.remove(self.tracks[id], ipod=True)
  
  def quit(self):
    self.db.close()

#Note that this doesn't work due to pymtp not working on Ubuntu 10.04
#class mtp_synchroniser(synchroniser):
#  def __init__(self):
#    self.mtp = pymtp.MTP()
#    self.mtp.connect()
#    folders = self.mtp.get_folder_list()
#    for key in folders:
#      folder = folders[key]
#      if "Music" == folder.name:
#        self.music_folder_id = folder.folder_id
#    
#  def list_device_files(self):
#    pass
#
#  def add(self, path, audio_file):
#    pass
#  
#  def remove(self, id):
#    pass
#  
#  def quit(self):
#    self.mtp.disconnect()

def main(args):
  if args[0] == "ipod":
    synchroniser = ipod_synchroniser(args[1])
  else:
    synchroniser = mtp_synchroniser()
  synchroniser.run()
  
if __name__ == "__main__":
  main(sys.argv[1:])