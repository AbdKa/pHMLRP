#!/usr/bin/env python3
import csv
import numpy as n
import sys
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
V = range(nv * p)
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
        if len(sys.argv) > 10:
            if str(sys.argv[10]) == "s":
                set = ",".join(sys.argv[11:])
                NH = [int(nh) for nh in set.split(",")]
                i = 0
                while i < len(NH):
                    NH[i] = NH[i]
                    i += 1

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
        dur = perf_counter() - self.start
        self.start = perf_counter()
        return round(dur * 1000000)

def loadDB(db_file):
    global D, N, MM, MM1
    # remove "encoding" or put it on function's parameters if there is a problem
    f = csv.reader(open("db/" + db_file, "r", encoding='utf-8-sig'), delimiter=",")
    D = [row for row in f]
    DD = [[float(y) for y in x] for x in D]
    D = []
    for z in N:
        list1 = []
        for j in N:
            print(str(z) + " " + str(j))
            list1.append(DD[z][j])
        D.append(list1)

    MM1 = max(n.max(D, 0))
    print(MM1)
    MM = MM1 * len(N)
    print(MM)


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
