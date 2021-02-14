#!/usr/bin/env python
import sys
import csv, time, numpy as n

D = []
N = []
p = 0

nv = []
#b = 1
alpha = 0
MM = 0
#T = 0
dataset = str(sys.argv[1])
instance = int(sys.argv[2])
p = int(sys.argv[3])
# for i in N:
nv = int(sys.argv[4]) #4
alpha=float(sys.argv[5]) #5

class Timer:
    def __init__(self):
        self.start = time.clock()
    def stop(self):
        dur = time.clock()-self.start
        self.start = time.clock()
        return (dur * 1000000.0)

def loadTR():
    global D, N, MM, W
    f = csv.reader(open("DB/TurkishNetworkDist.csv", "r", encoding='utf-8-sig'), delimiter=";")
    D = [row for row in f]
    print(D)
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)

def loadTR16():
    global D, N, MM, W
    f = csv.reader(open("DB/TR16.csv", "r", encoding='utf-8-sig'), delimiter=",")
    D = [row for row in f]
    print(D)
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)

def loadCAB():
    global D, N, MM, W, nv
    f = csv.reader(open("DB/CABNetworkDist.csv", "r"), delimiter=",")
    D = [row for row in f]
    D = [[float(y) for y in x] for x in D]
    N = range(len(D))
    MM1 = n.max(D)
    MM = MM1 * len(N)
    f = csv.reader(open("DB/CABNetworkFlow.csv", "r"), delimiter=",")
    W = [row for row in f]
    W = [[float(y) for y in x] for x in D]
    # f = open("/Users/mac/PycharmProjects/Hubs/Vehicles/Vehicles1.txt", "r")
    # nv = [int(x) for x in f.readline().strip().split()]
    # f.readline()

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
    N = range(n)
    D = [D[i][:n] for i in N]
    # W = [W[i][:n] for i in N]


def loadScriptArgs():
    global dataset, instance
    if dataset in ["AP"]:
        loadAP()
        truncateData(instance)
    elif dataset in ["TR"]:
        loadTR()
        truncateData(instance)
    elif dataset in ["TR16"]:
        loadTR16()
        truncateData(instance)
    else:
        loadCAB()
        truncateData(instance)
