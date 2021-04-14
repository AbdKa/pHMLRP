#!/usr/bin/env python
# Solve combined hub center & routing problems with python
### python pHubCenter_H.py TR 10 2 2 h 4,5 ###
import json
from sys import platform
from time import perf_counter

from gurobipy import *

import DATA2
from DATA2 import Timer

DATA2.loadScriptArgs()
D = DATA2.D
N = DATA2.N
p = DATA2.p
nv = DATA2.nv
H = DATA2.H
alpha = DATA2.alpha
m = Model()
tmr = Timer()
m.setParam(GRB.Param.TimeLimit, 7200.0)
startTotal = perf_counter()


def tPrint(msg):
    print("%5.1f: %s" % (perf_counter() - startTotal, msg))


# Create optimization model
m = Model('pHubCenter')
# Create variables

r = []
for k in N:
    r.append(m.addVar(lb=0, vtype=GRB.CONTINUOUS, name="rk%s"))

h = []
for k in N:
    h.append(m.addVar(lb=0, vtype=GRB.BINARY, name="h%s"))

x = []
for i in N:
    x.append([])
    for j in N:
        x[i].append(m.addVar(vtype=GRB.BINARY, name="x%s"))

for k in H:
    h[k].ub = 1
    h[k].lb = 1

z = m.addVar(obj=1, vtype=GRB.CONTINUOUS, name="z")

m.update()
# Update model to integrate new variables
# Create constraints
for k in N:
    for i in N:
        m.addConstr(
            z >= r[k] + r[i] + alpha * float(D[k][i]))

m.addConstr(
    quicksum(h[k] for k in N) == p)

m.addConstr(
    quicksum(x[k][k] for k in N) == p)

m.addConstr(
    quicksum(x[k][k] for k in N) == quicksum(h[k] for k in N))

for i in N:
    m.addConstr(quicksum(x[i][k] for k in N) == 1)

for i in N:
    for j in N:
        m.addConstr(
            x[i][j] <= x[j][j])

for i in N:
    for j in N:
        m.addConstr(
            x[i][j] <= h[j])

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
        if x[i][k].x > 0.5:
            if i == k:
                print('x(', i, ',', k, ') => hub')
            else:
                if k not in sets:
                    sets[k] = ""
                sets[k] += str(i) + ","
                print('x(', i, ',', k, ')')

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
    p_command = "python3"
if platform == "darwin":
    p_command = "python3"

# for nv > 1 set py_file name
py_file = "NVEG.py"
if nv == 1:
    py_file = "TSP.py"

for h, s in sets.items():
    command = p_command + " python/" + py_file + " " + json_file + " " + DATA2.dataset + " " + \
              str(int(len(s) / 2) + 1) + " 1 " + str(nv) + " 1 1 h " + str(h) + " s " + s[:-1]
    print(command)
    os.system(command)

print('***********************************************************************************')
