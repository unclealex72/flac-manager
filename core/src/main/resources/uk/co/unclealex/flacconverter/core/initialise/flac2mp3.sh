#!/bin/sh

[ -r "$1" ] || exit 1

# Before converting, check for a valid genre

GENRE=`metaflac --show-tag=GENRE $1 | sed s/GENRE=//`
[ -z `id3v2 -L | egrep "$GENRE" | wc -l` ] || GENRE="Other"

flac -dcs "$1" \
    | lame --resample 44100 -h -b 192 --add-id3v2 \
        --tt "`metaflac --show-tag=TITLE $1 | sed s/TITLE=//`" \
        --ta "`metaflac --show-tag=ARTIST $1 | sed s/ARTIST=//`" \
        --tl "`metaflac --show-tag=ALBUM $1 | sed s/ALBUM=//`" \
        --ty "`metaflac --show-tag=DATE $1 | sed s/DATE=//`" \
        --tn "`metaflac --show-tag=TRACKNUMBER $1 | sed s/TRACKNUMBER=//`" \
        --tg "$GENRE" - "$2"
