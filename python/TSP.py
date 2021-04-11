#!/usr/bin/env python3
# Solve combined hub center & routing problems with python
# TSP: if there is just one vehicle (TSP.py TR 10 1 1 1 1 h 2)
### python TSP.py pHC_MTSP_10_1.json TR 10 1 1 1 1 h 1 s 2,3,4,5,6,7,8,9,10 ###

from time import perf_counter, clock

from gurobipy import *

import DATA3
from DATA3 import Timer

# BURASI DA AYNI HUB LARI VERDIGIMIZDE KULLANABILDIGIMIZ HALE GETIRMEMIZ GEREKLI


DATA3.loadScriptArgs()
D = DATA3.D
p = DATA3.p
H = DATA3.H
nv = DATA3.nv
V = range(nv)  # one vehicle type per hub
N = DATA3.N
dictionary = dict(zip([*range(len(N))], N))
N = [*range(len(N))]
NH = [*range(len(N))]  # non-hub nodes
HH = H
H = []
for hh in HH:
    hh = list(dictionary.keys())[list(dictionary.values()).index(hh)]
    H.append(hh)
    NH.remove(hh)
print(H)
print(dictionary)
print(NH)
print(N)
print(V)

alpha = DATA3.alpha
L = len(N) - len(H) * nv + 1  # max tour length

# Start to model
tmr = Timer()
threads = -1  # number of threads to use
startTotal = perf_counter()


def tPrint(msg):
    print("%5.1f: %s" % (clock() - startTotal, msg))


# Create optimization model
m = Model('FixedpHubCenterRouting')
m.setParam('OutputFlag', False)
m.setParam('LogToConsole', False)
m.setParam(GRB.Param.TimeLimit, 1000.0)
# Create variables

h = []
for i in N:
    h.append(m.addVar(vtype=GRB.BINARY, name="h"))

for i in H:
    h[i].ub = 1
    h[i].lb = 1

u = []
for i in N:
    u.append(m.addVar(ub=L, vtype=GRB.INTEGER, name="u"))

for i in H:
    u[i].ub = 1
    u[i].lb = 1

hz = []
for i in N:
    hz.append([])
    for v in V:
        hz[i].append(m.addVar(vtype=GRB.BINARY, name="hz"))

x = []
for i in N:
    x.append([])
    for j in N:
        x[i].append([])
        for v in V:
            x[i][j].append(m.addVar(lb=0, vtype=GRB.BINARY, name="x"))

collect = m.addVars(H, N, NH, V, lb=0, vtype=GRB.BINARY, name="collect")

distribute = m.addVars(NH, N, H, V, lb=0, vtype=GRB.BINARY, name="distribute")

cost2 = m.addVars(H, V, lb=0, vtype=GRB.CONTINUOUS, name="cost2")

cost1 = m.addVars(H, V, lb=0, vtype=GRB.CONTINUOUS, name="cost1")

z = m.addVar(obj=1, vtype=GRB.CONTINUOUS, name="z")
# Update model to integrate new variables
m.update()
for indxs in range(len(N)):
    h[indxs].BranchPriority = 100
# Create constraints
# Degree constraint

# kisit2(i,v)..sum(j$(ord(j)<>ord(i)),x(i,j,v))-sum(j$(ord(j)<>ord(i)),x(j,i,v))=e=0;
for v in V:
    for i in N:
        m.addConstr(
            quicksum(x[i][j][v] for j in N if j != i) - quicksum(x[j][i][v] for j in N if j != i) == 0, name="ctr1")

# kisit8(i,j)$(ord(j)<> ord(i) and s2(i) and s2(j))..u(i)-u(j)+(10*sum(v,x(i,j,v)))=l=9;
for i in N:
    for j in NH:
        m.addConstr(u[j] >= u[i] + 1 + L * (quicksum(x[i][j][v] for v in V) - 1))

# kisit5(v)..sum((i,j)$(s1(i) and ord(j)<>ord(i)),x(i,j,v))=e=1;
for v in V:
    m.addConstr(
        quicksum(x[i][j][v] for j in N for i in H if j != i) == 1)

# kisit80(i)$(s1(i))..sum((v,j)$(ord(j)<>ord(i)),x(i,j,v))=e=(n(i));
for i in H:
    m.addConstr(
        quicksum(x[i][j][v] for j in N for v in V if j != i) == nv, name="ctr12_%i")

# kisit90(i)$(s1(i))..sum((v,j)$(ord(j)<>ord(i)),x(j,i,v))=e=(n(i));
for i in H:
    m.addConstr(
        quicksum(x[j][i][v] for j in N for v in V if j != i) == nv, name="ctr32_%i")

# kisit11(i)$(s2(i))..sum((v,j)$(ord(j)<>ord(i)),x(i,j,v))=e=1;
for i in NH:
    m.addConstr(
        quicksum(x[i][j][v] for j in N for v in V if j != i) == 1, name="ctr33_%i")

# kisit12(i)$(s2(i))..sum((v,j)$(ord(j)<>ord(i)),x(j,i,v))=e=1;
for i in NH:
    m.addConstr(
        quicksum(x[j][i][v] for j in N for v in V if j != i) == 1, name="ctr34_%i")

# m.addConstr(z == (quicksum(float(D[i][j])*x[i][j][v]) for i in N for j in N if i != j for v in V) ,name="ctr16")
m.addConstr(
    z == quicksum(float(D[i][j]) * x[i][j][v] for i in N for j in N for v in V if i != j), name="ctr16")

m.optimize()
CPU = tmr.stop()
m.setObjective(z)

print('***********************************************************************************')
links = {}
route = ""
# loop to find first moves (H[0], node) in each route and add to links and routes
for i in range(len(N)):
    for k in range(len(N)):
        for v in V:
            if x[i][k][v].x > 0.5:
                if i != k and dictionary.get(N[i]) == dictionary.get(H[0]):
                    route = str(dictionary.get(N[i])) + "," + str(dictionary.get(N[k]))
                    links[dictionary.get(N[i])] = dictionary.get(N[k])
                    print('x(', dictionary.get(N[i]), ',', dictionary.get(N[k]), ',', v, ') ')

# loop on the other edges and add them to links
for i in range(len(N)):
    for k in range(len(N)):
        for v in V:
            if x[i][k][v].x > 0.5:
                if i != k and dictionary.get(N[i]) != dictionary.get(H[0]):
                    links[dictionary.get(N[i])] = dictionary.get(N[k])
                    print('x(', dictionary.get(N[i]), ',', dictionary.get(N[k]), ',', v, ') ')

print(links)

for k in H:
    for v in V:
        print("cost1[%d, %d] = %1.1f" % (dictionary.get(k), v, cost1[k, v].x))

for k in H:
    for v in V:
        print("cost2[%d, %d] = %1.1f" % (dictionary.get(k), v, cost2[k, v].x))


def get_next(second):
    for first, last in links.items():
        if second == first:
            return "," + str(last)


for i in range(len(links) - 2):
    route += str(get_next(int(route.split(',')[-1])))

print(route)

print('***********************************************************************************')

print("dataset instance p nv alpha obj CPU/elapsed Nodes routes")
print("Problem:", DATA3.dataset, DATA3.instance, DATA3.p, "alpha:", DATA3.alpha, "Obj:", z.x, "CPU:", CPU, )
print('***********************************************************************************')

import json

with open(DATA3.file_name, 'r+') as f:
    data = json.load(f)
    data['CPU'] += CPU
    data['routes'].append(route)
    # data['routes'].sort()
    f.seek(0)  # <--- should reset fpHC_MTSPile position to the beginning.
    json.dump(data, f, indent=4)
    f.truncate()  # remove remaining part
    f.close()
