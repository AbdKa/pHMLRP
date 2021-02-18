#!/usr/bin/env python3
# Solve combined hub center & routing problems with python
### python phubcenter.py TR 10 2 2 1 1 ###

from sys import platform
from time import perf_counter

from gurobipy import *

import DATA2
import json
from DATA2 import Timer

DATA2.loadScriptArgs()
D = DATA2.D
N = DATA2.N
p = DATA2.p
nv = DATA2.nv
alpha = DATA2.alpha
m = Model()
tmr = Timer()
# m.setParam(GRB.Param.TimeLimit, 1000.0)
threads = -1  # number of threads to use
startTotal = perf_counter()


def tPrint(msg):
    print("%5.1f: %s" % (perf_counter() - startTotal, msg))


# Create optimization model
m = Model('pHubCenter')
# Create variables

r = []
for k in N:
    r.append(m.addVar(lb=0, vtype=GRB.CONTINUOUS, name="rk%s"))

x = []
for i in N:
    x.append([])
    for j in N:
        x[i].append(m.addVar(vtype=GRB.BINARY, name="x%s"))

# TODO: set hubs properly
for h in DATA2.H:
    x[h][h].lb = 1
    x[h][h].ub = 1

z = m.addVar(obj=1, vtype=GRB.CONTINUOUS, name="z")

m.update()
m.params.TimeLimit = 1000

for k in N:
    for i in N:
        m.addConstr(
            z >= r[k] + r[i] + alpha * float(D[k][i]))

m.addConstr(
    quicksum(x[k][k] for k in N) == p)

for i in N:
    m.addConstr(quicksum(x[i][k] for k in N) == 1)

for i in N:
    for j in N:
        m.addConstr(
            x[i][j] <= x[j][j])

for k in N:
    m.addConstr(
        r[k] >= quicksum(D[i][k] * x[i][k] for i in N))

for j in N:
    m.addConstr(quicksum(x[i][j] for i in N) >= (nv + 1) * x[j][j])

m.optimize()
CPU = tmr.stop()
m.setObjective(z)
print("obj:", z.x)
successor = [[j for j in N if x[i][j].x > 0.5] for i in N]

sets = {}
for i in N:
    for k in N:
        if (x[i][k].x > 0.5):
            if i == k:
                print('x(', i + 1, ',', k + 1, ') => hub')
            else:
                if (k + 1) not in sets:
                    sets[k + 1] = ""
                sets[k + 1] += str(i + 1) + ","
                print('x(', i + 1, ',', k + 1, ')')

print("pHubCenter CPU: " + str(CPU))
data = {'routes': [], 'CPU': CPU, 'dataset': DATA2.dataset, "N": DATA2.instance}

from pathlib import Path
Path("results").mkdir(parents=True, exist_ok=True)
json_file = "results/pHC_MTSP_" + str(len(N)) + "_" + str(p) + "_" + str(nv) + ".json"

try:
    os.remove(json_file)
except OSError:
    pass

# convert into JSON:
with open(json_file, "w") as write_file:
    y = json.dump(data, write_file, indent=4)

write_file.close()

p_command = "python"
# change python to python3 for mac and linux
if platform == "linux" or platform == "linux2":
    p_command = "python"
if platform == "darwin":
    p_command = "python3"

# for nv > 1 set py_file name
py_file = "NVEG.py"
if nv == 1:
    py_file = "TSP.py"

for h, s in sets.items():
    command = p_command + " " + py_file + " " + json_file + " TR " + \
              str(int(len(s) / 2) + 1) + " 1 " + str(nv) + " 1 1 h " + str(h) + " s " + s[:-1]
    print(command)
    os.system(command)

print('***********************************************************************************')
#
# for i in N:
#     for k in N:
#         if (x[i][k].x > 0.5):
#             if i == k:
#                 print('x(',i,',', k,') => hub')
#
#             else:
#                 print('x(',i,',', k,')')
