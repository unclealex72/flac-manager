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
# Delete all obsolete tracks. Each tuple is (ARTIST_CODE, ALBUM_CODE, TRACK_NUMBER, TRACK_CODE, id)
obsolete_tracks = []
<#list obsoleteTracks as obsoleteTrack>
obsolete_tracks.append(("${track.artistCode}", "${track.albumCode}", "${track.trackNumber}", "${track.trackCode}", "${track.id}"))
</#list>

# Quickly delete all remaining tracks
#for track in mtp.get_tracklisting():
#    delete_ids.append(int(track.item_id))

for obsolete_track in obsolete_tracks:
    try:
        mtp.delete_object(int(obsolete_track[4]))
        print "DELETE: %s,%s,%s,%s,%s" % (obsolete_track[0], obsolete_track[1], obsolete_track[2], obsolete_track[3], obsolete_track[4])
    except CommandFailed:
        pass
    
# Add all new tracks. Each tuple is (ARTIST_CODE, ALBUM_CODE, TRACK_NUMBER, TRACK_CODE, absolute_file, filename)
# ID3 information is taken from the file itself so there are no problems with spaces and non-ascii characters in this file.
new_tracks = []
<#list newTracks as track>
new_tracks.append(("${track.artistCode}", "${track.albumCode}", "${track.trackNumber}", "${track.trackCode}", "${track.path}", "${track.filename}"))
</#list>

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
