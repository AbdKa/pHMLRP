#!/usr/bin/env python
import csv
import sys
from time import perf_counter

import numpy as n

D = []
N = []
p = 0

nv = 0
b = 1
alpha = 0
MM = 0
q = 0
V = range(nv * p)
dataset = str(sys.argv[1])
instance = int(sys.argv[2])
p = int(sys.argv[3])
nv = int(sys.argv[4])
alpha = float(sys.argv[5])
q = float(sys.argv[6])

H = []
route = []
if len(sys.argv) > 9:
    if len(sys.argv) > 10:
        if str(sys.argv[9]) == "h":
            hubs = ",".join(sys.argv[10:])
            H = [int(h) for h in hubs.split(",")]
            if len(H) != p:
                print("Error: p != entered hubs length")
                print("    >> p = " + str(p) + "\thubs length = " + str(len(H)))
                exit(0)
        elif str(sys.argv[9]) == "r":
            routeStr = ",".join(sys.argv[10:])
            route = [int(node) for node in routeStr.split(",")]
            if len(route) != instance:
                print("Error: instance != entered route length")
                print("    >> instance = " + str(instance) + "\troute length = " + str(len(route)))
                exit(0)
    else:
        print("Error: Enter hubs or route (separated by ,)")
        exit(0)


# perf_counter() function always returns the float value of time in seconds.
# https://www.geeksforgeeks.org/time-perf_counter-function-in-python/
class Timer:
    def __init__(self):
        self.start = perf_counter()

    def stop(self):
        dur = perf_counter() - self.start
        self.start = perf_counter()
        return round(dur * 1000000)


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
    if len(route) > 1:
        N = route
        print(N)
        D = [[D[i][j] for i in N] for j in N]
        print(D)
    else:
        N = range(n)
        print(N)
        D = [D[i][:n] for i in N]
        print(D)


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
