#!/usr/bin/env python3
import sys
import csv, time, numpy as n
from time import perf_counter

file_name = str(sys.argv[1])
D = []
N = []
W = []
p = 0
nv = 0
b = 1
alpha = 0
MM = 0
MM1 = 0
q = 0
V = range(nv*p)
dataset = str(sys.argv[2])
instance = int(sys.argv[3])
p = int(sys.argv[4])
nv = int(sys.argv[5])
alpha = float(sys.argv[6])
q = float(sys.argv[7])

H = []
NH = []

route = []
if len(sys.argv) > 8:
    if str(sys.argv[8]) == "h":
        hub = sys.argv[9]
        H = [int(hub)]
        print(H)
        # if len(H) != p:
        #     print("Error: p != entered hubs length")
        #     print("    >> p = " + str(p) + "\thubs length = " + str(len(H)))
        #     exit(0)
        if len(sys.argv) > 10:
            if str(sys.argv[10]) == "s":
                set = ",".join(sys.argv[11:])
                NH = [int(nh) for nh in set.split(",")]
                i=0
                while i < len(NH):
                    NH[i]=NH[i]
                    i+=1

                N = H + NH
                N.sort()

                if len(NH) < 1:
                    print("Error: did not enter non-hub length")
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
        dur = perf_counter()-self.start
        self.start = perf_counter()
        return round(dur * 1000000)

def loadTR():
    global D, N, MM, W, MM1

    f = csv.reader(open("DB/TurkishNetworkDist.csv", "r"), delimiter=";")
    D = [row for row in f]
    DD = [[float(y) for y in x] for x in D]
    D = []
    for z in N:
        list1 = []
        for j in N:
            # print(str(z) + " " + str(j))
            list1.append(DD[z][j])
        D.append(list1)

    MM1 = max(n.max(D, 0))
    print(MM1)
    MM = MM1 * len(N)
    print(MM)
    f = csv.reader(open("DB/TurkishNetworkFlow.csv", "r"), delimiter=",")
    W = [row for row in f]
    W = [[float(y) for y in x] for x in D]

def loadCAB():
    global D, N, MM, W, MM1
    f = csv.reader(open("DB/CABNetworkDist.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = max(n.max(D, 0))
    MM = MM1 * len(N)
    f = csv.reader(open("DB/CABNetworkFlow.csv", "r"), delimiter=",")
    W = [row for row in f]
    W = [[float(y) for y in x] for x in D]

def loadAP():
    global D, N, MM, W
    f = csv.reader(open("DB/APNetworkDist10.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)
    f = csv.reader(open("DB/APNetworkFlow10.csv", "r"), delimiter=",")
    W = [row for row in f]
    W = [[float(y) for y in x] for x in D]

def loadAP2():
    global D, N, MM, W
    f = csv.reader(open("DB/APNetworkDist15.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)
    f = csv.reader(open("DB/APNetworkFlow15.csv", "r"), delimiter=",")
    W = [row for row in f]
    W = [[float(y) for y in x] for x in D]


def truncateData(n):
    global D,N,W
    if len(route) > 1:
        N = route
        D = [[D[i][j] for i in N] for j in N]
        W = [[W[i][j] for i in N] for j in N]
    else:
        N = range(n)
        D = [D[i][:n] for i in N]
        W = [W[i][:n] for i in N]


def loadScriptArgs():
    global dataset, instance
    if dataset in ["AP"]:
        loadAP()
        truncateData(instance)
    elif dataset in ["TR"]:
        loadTR()
        # truncateData(instance)
    else:
        loadCAB()
        truncateData(instance)
