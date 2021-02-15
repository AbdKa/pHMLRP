#!/usr/bin/env python3
# Solve combined hub center & routing problems with python
### python3 phubcenter.py TR 10 2 2 1 1 ###

import DATA2, sys
from gurobipy import *
from time import perf_counter
from DATA2 import Timer
from sys import platform

DATA2.loadScriptArgs()
D = DATA2.D
N = DATA2.N
p = DATA2.p
nv = DATA2.nv
alpha = DATA2.alpha
m = Model()
tmr = Timer()
#m.setParam(GRB.Param.TimeLimit, 1000.0)
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

#x[5][5].lb = 1
#x[2][2].lb = 1
#x[5][5].ub = 1
#x[2][2].ub = 1

z = m.addVar(obj=1, vtype=GRB.CONTINUOUS, name="z")

m.update()
# m.params.varBranch = 3
m.params.TimeLimit = 1000
#for indxs in range(0,6):
#    x[indxs].BranchPriority = 100
# for indx in range(0,9):
#     for ind in range(0, 1):
#         x[indx][ind].BranchPriority = 0
# Update model to integrate new variables
# Create constraints


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
    m.addConstr(quicksum(x[i][j] for i in N) >= (nv+1) * x[j][j])


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
                print('x(',i+1,',', k+1,') => hub')
            else:
                if (k+1) not in sets:
                    sets[k+1] = ""
                sets[k+1] += str(i+1) + ","
                print('x(',i+1,',', k+1,')')

print("pHubCenter CPU: " + str(CPU))
data = {'routes': [], 'CPU': CPU, 'dataset': DATA2.dataset, "N": DATA2.instance}

import os
try:
    os.remove("pHC_MTSP_" + str(len(N)) + "_" + str(p) + ".json")
except OSError:
    pass

import os
try:
    os.remove("pHC_MTSP_" + str(len(N)) + "_" + str(p) + "_" + str(nv) + ".json")
except OSError:
    pass

import json
# convert into JSON:
with open("pHC_MTSP_" + str(len(N)) + "_" + str(p) + "_" + str(nv) + ".json", "w") as write_file:
    y = json.dump(data, write_file, indent=4)

write_file.close()


p_command = "python";
if platform == "linux" or platform == "linux2":
    p_command = "python"
elif platform == "darwin":
    p_command = "python3"
elif platform == "win32":
    p_command = "python"

for h, s in sets.items():

    command = p_command + " NVEG.py pHC_MTSP_" + str(len(N)) + "_" + str(p) + "_" + str(nv) + ".json TR " + \
              str(int(len(s)/2)+1) + " 1 " + str(nv) + " 1 1 h " + str(h) + " s " + s[:-1]
    print(command)
    os.system(command)


print ('***********************************************************************************')
#
# for i in N:
#     for k in N:
#         if (x[i][k].x > 0.5):
#             if i == k:
#                 print('x(',i,',', k,') => hub')
#
#             else:
#                 print('x(',i,',', k,')')
