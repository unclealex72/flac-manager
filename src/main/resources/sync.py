#!/usr/bin/python
import sys
import string
import gpod
import datetime
import os
import eyeD3
import tempfile
import gtk.gdk
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
        elif cmd == "REMOVEPLAYLISTS":
          self.remove_all_playlists()
          self.okay()
        elif cmd == "ADDPLAYLIST":
          playlist_name = parts[1]
          track_ids = parts[2:]
          self.add_playlist(playlist_name, track_ids)
          self.okay()
          
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
  
  def clone(self, list):
    my_clone = []
    for item in list:
      my_clone.append(item)
    return my_clone
  
class ipod_synchroniser(synchroniser):
  def __init__(self, mount_point):
    self.mount_point = mount_point
    self.db = gpod.Database(mount_point)
    self.added_file_count = 0;
    
  def list_device_files(self):
    device_files = []
    self.tracks = {}
    self.reset_db()
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
    # Check for any album art and albumartist information and add if found.
    tag = eyeD3.Tag()
    tag.link(audio_file)
    if (len(tag.getImages()) != 0):
      img = tag.getImages()[0]
      img_file = tempfile.NamedTemporaryFile(suffix=".jpg")
      img_filename = img_file.name
      img.writeFile(os.path.dirname(img_filename), os.path.basename(img_filename))
      pixbuf = gtk.gdk.pixbuf_new_from_file(img_filename)
      img_file.close()
      track.set_coverart(pixbuf)
      track.copy_to_ipod()
    track['artist'] = tag.getArtist(eyeD3.frames.BAND_FID)
    track['sort_artist'] = tag.getArtist("TSOP")
    self.added_file_count += 1
    if (self.added_file_count % 50 == 0):
      self.reset_db()

  def reset_db(self):
      self.db.copy_delayed_files()
      self.db.close()
      self.db = gpod.Database(self.mount_point)
      self.out("** Database reset **")
      
  def remove(self, id):
    self.db.remove(self.tracks[id], ipod=True)
  
  def remove_all_playlists(self):
    for playlist in self.clone(self.db.get_playlists()):
      if (not (playlist.get_smart() or playlist.get_master() or playlist.get_podcast())):
        for track in self.clone(playlist):
          playlist.remove(track)
        self.db.remove(playlist, ipod=True)

  def add_playlist(self, playlist_name, track_ids):
    playlist = self.db.new_Playlist(title = playlist_name)
    for id in track_ids:
      playlist.add(self.tracks[id])
    
  def quit(self):
    self.db.copy_delayed_files()
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