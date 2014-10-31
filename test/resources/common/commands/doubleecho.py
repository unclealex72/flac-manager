#!/usr/bin/python
import sys


def okay():
    out("OK")


def out(message):
    print message
    sys.stdout.flush()


keep_running = True
while keep_running:
    line = sys.stdin.readline()
    line = line.strip()
    if line == "" or line == "QUIT":
        okay()
        keep_running = False
    else:
        out(line + "A")
        out(line + "B")
        okay()