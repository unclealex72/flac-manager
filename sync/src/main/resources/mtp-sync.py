#!/usr/bin/env python
from symbol import except_clause
from pymtp import CommandFailed

import pymtp
from eyeD3 import *

# Connect to MTP
mtp = pymtp.MTP()
mtp.connect()

# Find the music directory
music_directory_id = 0 
for folder in mtp.get_parent_folders():
    if "Music" == folder.name:
        music_directory_id = int(folder.folder_id)

print "PARENT: %s" % (music_directory_id)

# Delete all files with selected ids
delete_ids = []
delete_ids.append(666117)
delete_ids.append(666436)

# Quickly delete all remaining tracks
#for track in mtp.get_tracklisting():
#    delete_ids.append(int(track.item_id))

for delete_id in delete_ids:
    try:
        mtp.delete_object(int(delete_id))
        print "DELETE: %s" % (delete_id)
    except CommandFailed:
        pass
    
# Add all new tracks. Each tuple is (ARTIST_CODE, ALBUM_CODE, TRACK_CODE, TRACK_NUMBER, absolute_file, filename)
# ID3 information is taken from the file itself so there are no problems with spaces and non-ascii characters in this file.
new_tracks = []
new_tracks.append(("METALLICA", "DEATH MAGNETIC", "THAT WAS JUST YOUR LIFE", "01", "/home/alex/queen/covered/01_that_was_just_your_life.mp3", "life.mp3"))
new_tracks.append(("MUSE", "RESISTANCE", "UPRISING", "01", "/home/alex/muse/MP3/Muse - 01 - Uprising.mp3", "uprising.mp3"))
new_tracks.append(("MUSE", "RESISTANCE", "RESISTANCE", "02", "/home/alex/muse/MP3/Muse - 02 - Resistance.mp3", "resistance.mp3"))

for new_track in new_tracks:
    source = new_track[4]
    target = new_track[5]

    audioFile = eyeD3.Mp3AudioFile(source)
    tag = audioFile.getTag()
    
    metadata = pymtp.LIBMTP_Track()
    metadata.parent_id = music_directory_id

    metadata.artist = tag.getArtist()
    metadata.title = tag.getTitle()
    metadata.album = tag.getAlbum()
    metadata.tracknumber = int(tag.getTrackNum()[0])
    metadata.duration = int(audioFile.getPlayTime()) * 1000
        
    track_id = mtp.send_track_from_file(source, target, metadata)
    print "ADD: %s,%s,%s,%s,%s" % (new_track[0], new_track[1], new_track[2], new_track[3], track_id)

## Disconnect from the device
mtp.disconnect()
