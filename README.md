# Music

## Overview

This project can be used to manage a repository of [FLAC](http://flac.sourceforge.net/) files. It can do the following:

* ensure FLAC files are named consistently,
* encode FLAC files into MP3 files, making sure album artwork is copied over and
* synchronise external MP3 devices for one or more [MusicBrainz](http://musicbrainz.org) users.

## Basic Usage

The FLAC manager requires that all FLAC files are stored under one directory that is made read-only. FLAC files can be _checked out_ into a staging directory where they can be changed and then _checked in_. New FLAC files can be added to the repository by ripping them into the staging directory.

## MusicBrainz

The FLAC manager requires that all files are tagged with MusicBrainz tags (e.g. by [Picard](http://musicbrainz.org/doc/MusicBrainz_Picard)). This allows FLAC files to then be named consistently and allows more than one MusicBrainz user to keep their external music devices synchronised.