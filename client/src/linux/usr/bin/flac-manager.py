#!/usr/bin/python
import os
import argparse
import sys
import pycurl
import urllib
import urllib2
import httplib
import StringIO

class CurlHTTPStream(object):
    def __init__(self, url, parameters):
        self.url = url
        self.received_buffer = StringIO.StringIO()

        self.curl = pycurl.Curl()
        self.curl.setopt(pycurl.URL, url)
        self.curl.setopt(pycurl.HTTPHEADER, ['Cache-Control: no-cache'])
        self.curl.setopt(pycurl.ENCODING, 'gzip')
        self.curl.setopt(pycurl.CONNECTTIMEOUT, 5)
        self.curl.setopt(pycurl.WRITEFUNCTION, self.received_buffer.write)
        self.curl.setopt(pycurl.POSTFIELDS, urllib.urlencode(parameters))
        self.curlmulti = pycurl.CurlMulti()
        self.curlmulti.add_handle(self.curl)

        self.status_code = 0

    SELECT_TIMEOUT = 10

    def _any_data_received(self):
        return self.received_buffer.tell() != 0

    def _get_received_data(self):
        result = self.received_buffer.getvalue()
        self.received_buffer.truncate(0)
        self.received_buffer.seek(0)
        return result

    def _check_status_code(self):
        if self.status_code == 0:
            self.status_code = self.curl.getinfo(pycurl.HTTP_CODE)
        if self.status_code != 0 and self.status_code != httplib.OK and self.status_code != httplib.BAD_REQUEST:
            raise urllib2.HTTPError(self.url, self.status_code, None, None, None)

    def _perform_on_curl(self):
        while True:
            ret, num_handles = self.curlmulti.perform()
            if ret != pycurl.E_CALL_MULTI_PERFORM:
                break
        return num_handles

    def _iter_chunks(self):
        while True:
            remaining = self._perform_on_curl()
            if self._any_data_received():
                self._check_status_code()
                yield self._get_received_data()
            if remaining == 0:
                break
            self.curlmulti.select(self.SELECT_TIMEOUT)

        self._check_status_code()
        self._check_curl_errors()

    def _check_curl_errors(self):
        for f in self.curlmulti.info_read()[2]:
            raise pycurl.error(*f[1:])

    def iter_lines(self):
        chunks = self._iter_chunks()
        return self._split_lines_from_chunks(chunks)

    @staticmethod
    def _split_lines_from_chunks(chunks):
        #same behaviour as requests' Response.iter_lines(...)

        pending = None
        for chunk in chunks:

            if pending is not None:
                chunk = pending + chunk
            lines = chunk.splitlines()

            if lines and lines[-1] and chunk and lines[-1][-1] == chunk[-1]:
                pending = lines.pop()
            else:
                pending = None

            for line in lines:
                yield line

        if pending is not None:
            yield pending

class Command:

    parameters = {}

    def execute(self):
        host = os.environ.get("FLAC_HOST", "localhost")
        port = os.environ.get("FLAC_PORT", "9999")
        parser = self.generateParser()
        args = parser.parse_args()
        with open ("/etc/mtab", "r") as mtab:
            self.addParameter("mtab", mtab.read())
        if hasattr(args, "directories"):
            self.addParameters("directories", map(lambda p: os.path.abspath(p), args.directories))
        if hasattr(args, "users"):
            self.addParameters("users", args.users.split(","))
        if hasattr(args, "unown") and args.unown:
            self.addParameter("unown", "true")
        url = "http://%s:%s/commands/%s" % (host, port, self.cmd)
        for line in CurlHTTPStream(url, self.parameters).iter_lines():
            print line

    def addParameter(self, key, value):
        self.parameters[key] = value

    def addParameters(self, key, values):
        for i in range(len(values)):
            self.parameters['%s[%d]' % (key, i)] = values[i]

class Checkin(Command):
    cmd = "checkin"

    def generateParser(self):
        parser = argparse.ArgumentParser(description='Check-in FLAC files to the FLAC repository.')
        parser.add_argument('directories', metavar='directory', type=str, nargs='+', help='a staging directory to check-in')
        return parser

class Checkout(Command):
    cmd = "checkout"

    def generateParser(self):
        parser = argparse.ArgumentParser(description='Check-out FLAC files from the FLAC repository.')
        parser.add_argument('directories', metavar='directory', type=str, nargs='+', help='a FLAC directory to check-out')
        parser.add_argument('--unown', action='store_true', help='also unown any files that are checked out')
        return parser

class Own(Command):
    cmd = "own"

    def generateParser(self):
        parser = argparse.ArgumentParser(description='Add owners to directories in the staging repository.')
        parser.add_argument('directories', metavar='directory', type=str, nargs='+', help='a staging directory to which owners will be added')
        parser.add_argument('--users', required=True, help='a comma separated list of users to add')
        return parser

class Unown(Command):
    cmd = "unown"

    def generateParser(self):
        parser = argparse.ArgumentParser(description='Remove owners from directories in the staging repository.')
        parser.add_argument('directories', metavar='directory', type=str, nargs='+', help='a staging directory from which owners will be removed')
        parser.add_argument('--users', required=True, help='a comma separated list of users to remove')
        return parser

class Sync(Command):
    cmd = "sync"

    def generateParser(self):
        return argparse.ArgumentParser(description='Synchronise any connected devices.')


class Initialise(Command):
    cmd = "initialise"

    def generateParser(self):
        return argparse.ArgumentParser(description='Initialise the backend database.')

commands = {
    'flacman-checkin': Checkin(),
    'flacman-checkout': Checkout(),
    'flacman-own': Own(),
    'flacman-unown': Unown(),
    'flacman-sync': Sync(),
    'flacman-initialise': Initialise()
}

cmd = os.path.basename(__file__)
if cmd in commands.keys():
    commands[cmd].execute()
else:
    print "Please call this file using one of the following symlinks: " + ", ".join(commands.keys())
    sys.exit(1)
