# Flac Manager 

## Overview

This project can be used to manage a repository of [FLAC](http://flac.sourceforge.net/) files. It can do the following:

* ensure FLAC files are named consistently,
* encode FLAC files into MP3 files, making sure album artwork is copied over and
* synchronise external MP3 devices for one or more [MusicBrainz](http://musicbrainz.org) users.

## Basic Usage

The FLAC manager requires that all FLAC files are stored under one directory that is made read-only. FLAC files can be _checked out_ into a staging directory where they can be changed and then _checked in_. New FLAC files can be added to the repository by ripping them into the staging directory.

## Configuration

Configuration is stored in a file called `~/.flacman.json`

    {
      "directories" : {
        "flacPath" : "The root path of your FLAC music library.",
        "devicesPath" : "The root path where devices get mounted.",
        "encodedPath" : "The root path of where encoded files are stored.",
        "stagingPath" : "The root path of the directory where FLAC paths are ripped before they are checked in to the FLAC music library."
      },
      "users" : [ {
        "name" : "The user's friendly name.",
        "musicBrainzUserName" : "The user's MusicBrainz username.",
        "musicBrainzPassword" : "The user's MusicBrainz password.",
        "devices" : [ {
          "type" : "ipod, x7 or fs",
          "uuid" : "The device's unique UUID",
          "relativePath" : "For filesystem devices, the music directory relative to the mount point."
        }, ]
      } ]
    }

## Usage

After installation, the following commands are available:

+ `flacman-checkout [directories]` Checkout all the FLAC files into the staging area so they can be retagged.
+ `flacman-checkin [directories]` Checkin all the FLAC files into the music library and also convert them to MP3.
+ `flacman-own [-d] -o users [directories]` Add (or remove if `-d` is supplied) all the FLAC files in the supplied directories to users MusicBrainz collections. `users` is a comma separated list of user names.
+ `flacman-sync` Synchronise all the connected devices with the files in the encoded music library.

## MusicBrainz

The FLAC manager requires that all files are tagged with MusicBrainz tags (e.g. by [Picard](http://musicbrainz.org/doc/MusicBrainz_Picard)). This allows FLAC files to then be named consistently and allows more than one MusicBrainz user to keep their external music devices synchronised.
