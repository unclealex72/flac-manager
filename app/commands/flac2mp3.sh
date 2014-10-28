#!/bin/sh

flac -dcs "$1" | lame --resample 44100 -h -b 320 - "$2"
