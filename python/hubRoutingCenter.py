#!/usr/bin/env python
# Solve combined hub center & routing problems with python
from time import process_time

from gurobipy import *

import DATA
from DATA import Timer

DATA.loadScriptArgs()
if len(DATA.route) > 0:
    N = range(DATA.instance)
else:
    N = DATA.N
D = DATA.D
p = DATA.p
nv = DATA.nv
alpha = DATA.alpha
V = range(p * nv)
MM = DATA.MM
# Start to model
m = Model()
tmr = Timer()
threads = -1  # number of threads to use
startTotal = process_time()


def tPrint(msg):
    print("%5.1f: %s" % (process_time() - startTotal, msg))


# Create optimization model
m = Model('HubCenterRouting')
m.setParam(GRB.Param.TimeLimit, 10 * 60.0)
# Create variables
ab = []
for i in N:
    ab.append([])
    for v in V:
        ab[i].append(m.addVar(vtype=GRB.CONTINUOUS, name="ab%s_%s"))

ac = []
for i in N:
    ac.append([])
    for v in V:
        ac[i].append(m.addVar(vtype=GRB.CONTINUOUS, name="ac%s_%s"))

fc = []
for i in N:
    fc.append([])
    for v in V:
        fc[i].append(m.addVar(vtype=GRB.CONTINUOUS, name="fc%s_%s"))
fd = []
for i in N:
    fd.append([])
    for v in V:
        fd[i].append(m.addVar(vtype=GRB.CONTINUOUS, name="fd%s_%s"))

x = []
for i in N:
    x.append([])
    for j in N:
        x[i].append(m.addVar(vtype=GRB.BINARY, name="x%s_%s"))

h = []
for i in N:
    h.append(m.addVar(vtype=GRB.BINARY, name="h%s"))

# set hubs if passed
if len(DATA.H) > 0:
    for i in DATA.H:
        h[i].ub = 1
        h[i].lb = 1

if len(DATA.route) > 0:
    h[0].ub = 1
    h[0].lb = 1
    # del DATA.route[0]

xz = []
for i in N:
    xz.append([])
    for v in V:
        xz[i].append(m.addVar(vtype=GRB.BINARY, name="xz%s"))

hz = []
for i in N:
    hz.append([])
    for v in V:
        hz[i].append(m.addVar(vtype=GRB.CONTINUOUS, name="hz%s"))

z = m.addVar(obj=1, vtype=GRB.CONTINUOUS, name="z")
# Update model to integrate new variables
m.update()
for indxs in range(len(N)):
    h[indxs].BranchPriority = 100

# Create constraints
# Degree constraint
for i in N:
    m.addConstr(
        quicksum(x[i][j] for j in N if i != j) == quicksum(x[j][i] for j in N if i != j))
## Route related constraints1
for i in N:
    for j in N:
        if i != j:
            for v in V:
                m.addConstr(
                    ab[i][v] >= ab[j][v] + float(D[j][i]) - MM * (1.0 - x[j][i]) - MM * h[j] - MM * (1.0 - xz[i][v]))
## Route related constraints2
for i in N:
    for j in N:
        if i != j:
            for v in V:
                m.addConstr(
                    ac[i][v] >= ac[j][v] + float(D[i][j]) - MM * (1.0 - x[i][j]) - MM * h[j] - MM * (1 - xz[i][v]))
##Constraints for obj function
for i in N:
    for j in N:
        for v in V:
            for y in V:
                if v != y:
                    m.addConstr(z >= ab[i][v] + alpha * float(D[i][j]) + ac[j][y] - MM * (1 - h[i]) - MM * (1 - h[j]))
##Constraints for routes
for i in N:
    m.addConstr(
        quicksum(x[i][j] for j in N if i != j) == 1 + (nv - 1) * h[i])

for i in N:
    m.addConstr(
        quicksum(x[j][i] for j in N if i != j) == 1 + (nv - 1) * h[i])
##Constraints for routes3
for i in N:
    m.addConstr(
        quicksum(xz[i][v] for v in V) == 1 + (nv - 1) * h[i])
# Constraint for hubs to be opened
m.addConstr(
    quicksum(h[i] for i in N) == p)

# Constraints for routes4
for v in V:
    m.addConstr(
        quicksum(xz[i][v] for i in N) >= 2)
# Constraints for routes5
for v in V:
    m.addConstr(
        quicksum(hz[i][v] for i in N) == 1)
# Constraints for routes6
for i in N:
    for v in V:
        m.addConstr(hz[i][v] <= h[i])
# Constraints for routes7
for i in N:
    for v in V:
        m.addConstr(hz[i][v] <= xz[i][v])
# Constraints for routes8
for i in N:
    for v in V:
        m.addConstr(hz[i][v] >= h[i] + xz[i][v] - 1)
# Constraints for routes9
for i in N:
    for j in N:
        for v in V:
            if i != j:
                m.addConstr(xz[i][v] >= xz[j][v] + x[j][i] - 1 - h[j])
for i in N:
    for j in N:
        for v in V:
            if i != j:
                m.addConstr(xz[i][v] >= xz[j][v] + x[i][j] - 1 - h[j])
# Constraints for routes11
for i in N:
    for j in N:
        for v in V:
            if i != j:
                m.addConstr(
                    fc[i][v] >= fc[j][v] + float(D[i][j]) - MM * (1 - x[i][j]) - MM * h[i] - MM * (1 - xz[i][v]))
                # Constraints for routes12
for i in N:
    for j in N:
        for v in V:
            if i != j:
                m.addConstr(
                    fd[i][v] >= fd[j][v] + float(D[i][j]) - MM * (1 - x[i][j]) - MM * h[j] - MM * (1 - xz[i][v]))

# Constraints for routes13
for i in N:
    for j in N:
        for v in V:
            m.addConstr(z >= fc[i][v] + fd[j][v] - MM * (1 - x[i][j]))

m.optimize()
CPU = tmr.stop()
m.setObjective(z)
hubs = set([i for i in N if h[i].x > 0.5])
print("hubs:", DATA.route[0] if len(DATA.route) > 0 else sorted(hubs))
print("obj:", z.x)
successor = [[j for j in N if x[i][j].x > 0.5] for i in N]
routes = [[i, j] for i in hubs for j in N if x[i][j].x > 0.5]
print(routes)
for r in routes:
    while r[-1] != r[0]:
        r.append(successor[r[-1]][0])
        print("routes:")
        if len(DATA.route) > 0:
            print("\n".join("\t" + " ".join("%d" % DATA.route[i] for i in r) for r in routes))
        else:
            print("\n".join("\t" + " ".join("%d" % i for i in r) for r in routes))

if len(DATA.route) > 0:
    routes[0] = [DATA.route[i] for i in routes[0]]
    # 0 index is used because always one route TR 5 1 1 1 1 2 7 r 3,1,2,0,4
data = {'routes': routes}
import json

# convert into JSON:
with open("python/results/gorubi-routes.json", "w") as write_file:
    y = json.dump(data, write_file)
print("dataset instance p nv alpha obj CPU/elapsed Nodes routes")
print("Problem:", DATA.dataset, DATA.instance, DATA.p, DATA.nv, "alpha:", DATA.alpha, "Obj:", z.x, "CPU:", CPU,
      "routes:", routes)
# # # # # # # # # # solve and report the solution # # # # #
