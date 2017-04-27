# Flac Manager 

## Overview

This project can be used to manage a repository of [FLAC](http://flac.sourceforge.net/) files. 
It can do the following:

* ensure FLAC files are named consistently,
* encode FLAC files into MP3 files, making sure album artwork is copied over and
* synchronise external MP3 devices for one or more users.

## Basic Usage

The FLAC manager requires that all FLAC files are stored under one directory that is made read-only. 
FLAC files can be _checked out_ into a staging directory where they can be changed and 
then _checked in_. New FLAC files can be added to the repository by ripping them into the 
staging directory. Each FLAC file is encoded into an MP3 file when it is checked in to an encoded
directory and is also symbolically linked from the devices directory for each user who owns the
file.

## Server Setup

The server is distributed as a a [Docker](https://www.docker.com/) image at 
https://hub.docker.com/r/unclealex72/flac-manager. A [PostgreSQL](https://www.postgresql.org/) database is required 
to store which user owns which albums.

The following environment variables are required:

| Name        | Value                         | 
| ----------- | ----------------------------- |
| DB_HOST     | The database host.            |
| DB_USER     | The database user.            |
| DB_PASSWORD | The database user's password. |

Your music files need to be attached as a volume at `/music` and must be writeable by a user with UID 1000. Your
music directory must have the following subdirectories:

| Subdirectory     | Contents| 
| ---------------- | ---------------------------- |
| `flac/`          | A repository of FLAC files.  |
| `staging/`       | A directory where new FLAC files are stored before checking in.|
| `encoded/`       | Files that are encoded versions of FLAC files. |
| `devices/<user>` | Symbolic links to the encoded files owned by a user. |

## Client Setup

The client can be installed as a Debian `dpkg` file.

## Usage

After installation, the following commands are available:

+ `flacman-checkout [directories]` Checkout all the FLAC files into the staging area so they can be retagged.
+ `flacman-checkin [directories]` Checkin all the FLAC files into the music library and also convert them to MP3.
+ `flacman-own --users users [directories]` Add all the FLAC files in the supplied directories to users collections. 
  `users` is a comma separated list of user names.
+ `flacman-unown --users users [directories]` Add all the FLAC files in the supplied directories to users collections. 
  `users` is a comma separated list of user names.

## Synchronising to external devices

The Device Synchroniser project at https://github.com/unclealex72/device-synchroniser contains both Android and
desktop applications that can be used to synchronise Android phones and USB sticks respectively.

## MusicBrainz

The FLAC manager requires that all files are tagged with MusicBrainz tags 
(e.g. by [Picard](http://musicbrainz.org/doc/MusicBrainz_Picard)). This allows FLAC files to then be named consistently 
and allows more than one user to keep their external music devices synchronised.
