#!/usr/bin/env python3
# Solve combined hub center & routing problems with python
### python3 NVEG.py pHC_MTSP_10_1.json TR 10 1 2 1 1 h 1 s 2,3,4,5,6,7,8,9,10 ###

##################################################
##  for nv > 1 formulation  ###
##################################################

from time import clock

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
NH = [*range(len(N))]   # non-hub nodes
HH = H
H = []
for hh in HH:
    hh = list(dictionary.keys())[list(dictionary.values()).index(hh)]
    H.append(hh)
    NH.remove(hh)
alpha = DATA3.alpha

print(H)
print(dictionary)
print(NH)
print(N)
print(V)

L = len(N) - len(H) * nv + 1  # max tour length

# Start to model
m = Model()
tmr = Timer()
m.setParam(GRB.Param.TimeLimit, 1000.0)
threads = -1  # number of threads to use
startTotal = clock()


def tPrint(msg):
    print("%5.1f: %s" % (clock() - startTotal, msg))


# Create optimization model
m = Model('FixedpHubCenterRouting')
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
#*kisit101(v,i)..sum(j$(ord(j)<>ord(i)),x(i,j,v))-sum(j$(ord(j)<>ord(i)),x(j,i,v))=e=0;
#kisit2(i,v)..sum(j$(ord(j)<>ord(i)),x(i,j,v))-sum(j$(ord(j)<>ord(i)),x(j,i,v))=e=0;
#kisit2(i,v)..sum(j$(ord(j)<>ord(i)),x(i,j,v))-sum(j$(ord(j)<>ord(i)),x(j,i,v))=e=0;
for v in V:
    for i in N:
        m.addConstr(
            quicksum(x[i][j][v] for j in N if j != i) - quicksum(x[j][i][v] for j in N if j != i) == 0, name="ctr1")

#kisit8(i,j)$(ord(j)<> ord(i) and s2(i) and s2(j))..u(i)-u(j)+(10*sum(v,x(i,j,v)))=l=9;
#kisit8(i,j)$(ord(j)<> ord(i) and s2(i) and s2(j))..u(i)-u(j)+(10*sum(v,x(i,j,v)))=l=9;
for i in N:
    for j in NH:
        m.addConstr(u[j] >= u[i] + 1 + L*(quicksum(x[i][j][v] for v in V)-1 ))

#kisit9(i,j,v)$(ord(j)<> ord(i) and s2(j))..sum(k$s1(k),collect(k,i,j,v))=e=x(i,j,v);
#kisit9(i,j,v)$(ord(j)<> ord(i) and s2(j))..sum(k$s1(k),collect(k,i,j,v))=e=x(i,j,v);
for i in N:
    for j in NH:
        if i != j:
            for v in V:
                m.addConstr(quicksum(collect[k, i, j, v] for k in H) == x[i][j][v], name="ctr36_%i%j%v" )

#kisit10(i,j,k,v)  $(s2(j) and s1(k))..collect(k,i,j,v)=l=x(i,j,v);
# kisit10(i,j,k,v)$(s2(j) and s1(k))..collect(k,i,j,v)=l=x(i,j,v);
for k in H:
    for i in N:
        for j in NH:
            for v in V:
                m.addConstr(collect[k, i, j, v] <= x[i][j][v], name="ctr7_%i%j%k%v")

#kisit21(i,j,k,w)$(s2(i) and s1(k) and ord(i)<>ord(j))..distribute(i,j,k,w)=l=x(i,j,w);
# kisit21(i,j,k,w)$(s2(i) and s1(k) and ord(i)<>ord(j))..distribute(i,j,k,w)=l=x(i,j,w);
for i in NH:
    for j in N:
        if i != j:
            for k in H:
                for w in V:
                    m.addConstr(distribute[i, j, k, w] <= x[i][j][w], name="ctr9_%i%j%k%w")

#kisit24(i,j,w)$(ord(j)<> ord(i) and s2(i))..sum(k$s1(k),distribute(i,j,k,w))=e=x(i,j,w);
# kisit24(i,j,w)$(ord(j)<> ord(i) and s2(i))..sum(k$s1(k),distribute(i,j,k,w))=e=x(i,j,w);
for i in NH:
    for j in N:
        if i != j:
            for w in V:
                m.addConstr(quicksum(distribute[i, j, k, w] for k in H) == x[i][j][w], name="ctr11_%i%j%w")

#kisit5(v)..sum((i,j)$(s1(i) and ord(j)<>ord(i)),x(i,j,v))=e=1;
for v in V:
    m.addConstr(
        quicksum(x[i][j][v] for j in N for i in H if j != i) == 1 )

#kisit80(i)$(s1(i))..sum((v,j)$(ord(j)<>ord(i)),x(i,j,v))=e=(n(i));
for i in H:
    m.addConstr(
        quicksum(x[i][j][v] for j in N for v in V if j != i) == nv, name="ctr12_%i" )

#kisit90(i)$(s1(i))..sum((v,j)$(ord(j)<>ord(i)),x(j,i,v))=e=(n(i));
for i in H:
    m.addConstr(
        quicksum(x[j][i][v] for j in N for v in V if j != i) == nv, name="ctr32_%i" )

#kisit11(i)$(s2(i))..sum((v,j)$(ord(j)<>ord(i)),x(i,j,v))=e=1;
for i in NH:
    m.addConstr(
        quicksum(x[i][j][v] for j in N for v in V if j != i) == 1, name="ctr33_%i" )

#kisit12(i)$(s2(i))..sum((v,j)$(ord(j)<>ord(i)),x(j,i,v))=e=1;
for i in NH:
    m.addConstr(
        quicksum(x[j][i][v] for j in N for v in V if j != i) == 1, name="ctr34_%i" )

#kisit23(k,w)$(s1(k))..cost2(k,w)=g= sum((i,j),d(i,j)*distribute(i,j,k,w));
# kisit23(k,w)$(s1(k))..cost2(k,w)=g= sum((i,j),d(i,j)*distribute(i,j,k,w));
for k in H:
    for w in V:
        m.addConstr(cost2[k, w] >= quicksum(float(D[i][j]) * distribute[i, j, k, w] for i in NH for j in N if i != j), name="ctr15_%k%w")

#kisit100(i,k,v,w)$(ord(w)<>ord(v) and ord(i)= ord(k))..cost3=g=cost(i,v)+cost2(k,w);
# kisit100(i,k,v,w)$(ord(w)<>ord(v) and ord(i)= ord(k))..cost3=g=cost(i,v)+cost2(k,w);
for k in H:
    for v in V:
        for w in V:
            if v != w:
                m.addConstr(z >= cost1[k, v] + cost2[k, w], name="ctr16_%k%v%w")

#kisit18(k,v)..cost(k,v)=g= sum((i,j),d(i,j)*collect(k,i,j,v));
#kisit18(k,v)..cost(k,v)=g= sum((i,j),d(i,j)*collect(k,i,j,v));
for k in H:
    for v in V:
        m.addConstr(cost1[k, v] >= (quicksum(float(D[i][j]) * collect[k, i, j, v] for i in N for j in NH if i != j)), name="ctr17_%k%v" )


m.optimize()
m.write("out.lp")
CPU = tmr.stop()
m.setObjective(z)

# get routes
links = [dict() for v in V]
routes = ["" for v in V]
# loop to find first moves (H[0], node) in each route and add to links and routes
for i in range(len(N)):
    for k in range(len(N)):
        for v in V:
            if x[i][k][v].x > 0.5:
                if i != k and dictionary.get(N[i]) == dictionary.get(H[0]):
                    routes[v] = str(dictionary.get(N[i])) + "," + str(dictionary.get(N[k]))
                    links[v][dictionary.get(N[i])] = dictionary.get(N[k])
                    print('x(', dictionary.get(N[i]), ',', dictionary.get(N[k]), ',', v, ') ')

# loop on the other edges and add them to links
for i in range(len(N)):
    for k in range(len(N)):
        for v in V:
            if x[i][k][v].x > 0.5:
                if i != k and dictionary.get(N[i]) != dictionary.get(H[0]):
                    links[v][dictionary.get(N[i])] = dictionary.get(N[k])
                    print('x(', dictionary.get(N[i]), ',', dictionary.get(N[k]), ',', v, ') ')

print(links)

for k in H:
    for v in V:
        print("cost1[%d, %d] = %1.1f" % (dictionary.get(k), v, cost1[k, v].x))

for k in H:
    for v in V:
        print("cost2[%d, %d] = %1.1f" % (dictionary.get(k), v, cost2[k, v].x))


# get next edge in links e.g. 1:2, 2:5, 5:4
def get_next(second, v):
    for first, last in links[v].items():
        if second == first:
            return "," + str(last)


# fill in the routes
for v in V:
    for i in range(len(links[v]) - 2):
        routes[v] += str(get_next(int(routes[v].split(',')[-1]), v))

print(routes)

print('***********************************************************************************')

print("dataset instance p nv alpha obj CPU/elapsed Nodes routes")
print("Problem:", DATA3.dataset, DATA3.instance, DATA3.p, "alpha:", DATA3.alpha, "Obj:", z.x, "CPU: ", str(CPU), )
print('***********************************************************************************')

import json

with open(DATA3.file_name, 'r+') as f:
    data = json.load(f)
    data['CPU'] += CPU
    for route in routes:
        data['routes'].append(route)
    f.seek(0)  # <--- should reset fpHC_MTSPile position to the beginning.
    json.dump(data, f, indent=4)
    f.truncate()  # remove remaining part
    f.close()
