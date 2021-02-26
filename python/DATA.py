#!/usr/bin/env python
import csv
import sys
import time

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


class Timer:
    def __init__(self):
        self.start = time.clock()

    def stop(self):
        dur = time.clock() - self.start
        self.start = time.clock()
        return dur


def loadTR16():
    global D, N, MM
    f = csv.reader(open("python/DB/TR16.csv", "r", encoding='utf-8-sig'), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)


def loadTR():
    global D, N, MM
    f = csv.reader(open("python/DB/TurkishNetworkDist.csv", "r"), delimiter=";")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)


def loadCAB():
    global D, N, MM
    f = csv.reader(open("python/DB/CABNetworkDist.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)


def loadAP10():
    global D, N, MM
    f = csv.reader(open("python/DB/APNetworkDist10.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)


def loadAP15():
    global D, N, MM
    f = csv.reader(open("python/DB/APNetworkDist15.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)


def loadAP100():
    global D, N, MM
    f = csv.reader(open("python/DB/APNetworkDist100.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)


def loadAP200():
    global D, N, MM
    f = csv.reader(open("python/DB/APNetworkDist200.csv", "r"), delimiter=",")
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
        loadAP10()
        truncateData(instance)
    elif dataset in ["AP15"]:
        loadAP15()
        truncateData(instance)
    elif dataset in ["AP100"]:
        loadAP100()
        truncateData(instance)
    elif dataset in ["AP200"]:
        loadAP200()
        truncateData(instance)
    elif dataset in ["TR"]:
        loadTR()
        truncateData(instance)
    else:
        loadCAB()
        truncateData(instance)
