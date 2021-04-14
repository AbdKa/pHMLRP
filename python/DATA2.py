#!/usr/bin/env python3
import csv
import numpy as n
import sys
from time import perf_counter

D = []
N = []
p = 0

nv = []
# b = 1
alpha = 0
MM = 0
# T = 0
dataset = str(sys.argv[1])
instance = int(sys.argv[2])
p = int(sys.argv[3])
nv = int(sys.argv[4])  # 4
# alpha = float(sys.argv[5])  # 5
H = []

if len(sys.argv) > 5:
    if str(sys.argv[5]) == "h":
        hubs = ",".join(sys.argv[6:])
        H = [int(h) for h in hubs.split(",")]
        if len(H) != p:
            print("Error: p != entered hubs length")
            print("    >> p = " + str(p) + "\thubs length = " + str(len(H)))
            exit(0)
    else:
        print("Error: Enter a hub")
        exit(0)


# perf_counter() function always returns the float value of time in seconds.
# https://www.geeksforgeeks.org/time-perf_counter-function-in-python/
class Timer:
    def __init__(self):
        self.start = perf_counter()

    def stop(self):
        dur = perf_counter() - self.start
        self.start = perf_counter()
        return dur


def loadDB(file_name):
    global D, N, MM
    # remove "encoding" or put it on function's parameters if there is a problem
    f = csv.reader(open("db/" + file_name, "r", encoding='utf-8-sig'), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)


def truncateData(n):
    global D, N
    N = range(n)
    D = [D[i][:n] for i in N]


def loadScriptArgs():
    global dataset, instance
    if dataset in ["AP10"]:
        loadDB("APNetworkDist10.csv")
    elif dataset in ["AP15"]:
        loadDB("APNetworkDist15.csv")
    elif dataset in ["AP100"]:
        loadDB("APNetworkDist100.csv")
    elif dataset in ["AP200"]:
        loadDB("APNetworkDist200.csv")
    elif dataset in ["TR"]:
        loadDB("TurkishNetworkDist.csv")
    elif dataset in ["TR16"]:
        loadDB("TurkishNetworkDist16.csv")
    else:
        loadDB("CABNetworkDist.csv")

    truncateData(instance)
