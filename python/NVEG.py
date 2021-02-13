#!/usr/bin/env python
# Solve combined hub center & routing problems with python
#amac..z=e=cost3;

##################################################
##  nv is equal to greater than 2 formulation  ###
##################################################

import DATA3, sys
from gurobipy import *
from time import clock
from DATA3 import Timer

# BURASI DA AYNI HUB LARI VERDIGIMIZDE KULLANABILDIGIMIZ HALE GETIRMEMIZ GEREKLI

DATA3.loadScriptArgs()
N = DATA3.N
D = DATA3.D
p = DATA3.p
# D = [[0, 490, 558, 837, 523, 809, 206, 69, 939, 901],
#      [490, 0, 544, 382, 912, 876, 673, 483, 453, 579],
 #     [558, 544, 0, 537, 1081, 1249, 764, 489, 724, 446],
#      [837, 382, 537, 0, 1283, 1235, 1043, 829, 243, 322],
#      [523, 912, 1081, 1283, 0, 324, 317, 592, 1365, 1424],
#      [809, 876, 1249, 1235, 324, 0, 641, 878, 1224, 1455],
#      [206, 673, 764, 1043, 317, 641, 0, 275, 1126, 1107],
 #     [69, 483, 489, 829, 592, 878, 275, 0, 932, 893],
#      [939, 453, 724, 243, 1365, 1224, 1126, 932, 0, 565],
#      [901, 579, 446, 322, 1424, 1455, 1107, 893, 565, 0]]
H = DATA3.H
nv = DATA3.nv
# H = [h - 1 for h in H]  # index from zero
# hub = sorted(H)  # sorted list of hubs
# H = set(H)
V = range(nv)  # one vehicle type per hub
NH = [i for i in N if i not in H]  # non-hub nodes
#H = [3]
#N = [3,7,9,10]
#NH = [7,9,10]
#p = 1
#DATA = [ [ 0 for i in range(len(N)+1) ] for j in range(len(N)+1)]

#r = 0;
#c = 0;
#for i in N:
#        c = 0;
#        for j in N:
#            DATA[r][c] = DATA3.D[i-1][j-1];
 #           c=c + 1
#        r=r+1

#H = [0]
#N = [0,1,2,3]
#NH = [1,2,3]
#p = 1

alpha = DATA3.alpha
L = len(N) - len(H) * nv + 1 # max tour length
print p
print H
print V
print nv
print NH
print L
# Start to model
m = Model()
tmr = Timer()
m.setParam(GRB.Param.TimeLimit, 1000.0)
threads = -1  # number of threads to use
startTotal = clock()


def tPrint(msg):
    print "%5.1f: %s" % (clock() - startTotal, msg)

# Create optimization model
m = Model('FixedpHubCenterRouting')
# Create variables

h = []
for i in N:
    h.append(m.addVar(vtype=GRB.BINARY, name="h"))

for i in H:
    h[i].ub = 1
    h[i].lb = 1

#h[1].ub = 1
#h[1].lb = 1
#h[2].ub = 1
#h[2].lb = 1

u = []
for i in N:
    u.append(m.addVar(ub=L, vtype=GRB.INTEGER, name="u"))

for i in H:
    u[i].ub = 1
    u[i].lb = 1

#u[1].ub = 1
#u[1].lb = 1
#u[2].ub = 1
#u[2].lb = 1

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

#xz = []
#for i in N:
#    xz.append([])
#    for j in N:
#        xz[i].append(m.addVar(vtype=GRB.BINARY, name="xz"))



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

#kisit88(i)..sum((j,v)$(ord(j)<>ord(i)),x(i,j,v))=e=1+(n(i)-1)*H(i);
# kisit88(i)..sum((j,v)$(ord(j)<>ord(i)),x(i,j,v))=e=1+(n(i)-1)*H(i);
#for i in N:
#    m.addConstr(
#        quicksum(x[i][j][v] for j in N for v in V if j != i) == 1 + (nv - 1) * h[i], name="ctr12_%i" )

#for i in N:
#    m.addConstr(
 #       quicksum(x[j][i][v] for j in N for v in V if j != i) == 1 + (nv - 1) * h[i], name="ctr12_%i" )


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

#kisit200(v,k,l)..cost3=g=sum((i,j)$(ord(j)<>ord(i)), d(i,j)*x(i,j,v))-d(k,l)*x(k,l,v)-M*(1-x(k,l,v));
#kisit200(v,k,l)..cost3=g=sum((i,j)$(ord(j)<>ord(i)), d(i,j)*x(i,j,v))-d(k,l)*x(k,l,v)-M*(1-x(k,l,v));

#for k in N:
#    for l in N:
#        for v in V:
##            m.addConstr(
 #               z >= quicksum(float(D[i][j]) * x[i][j][v] for i in N for j in N if j != i) - (
 #               float(D[k][l]) * x[k][l][v]) - 100000 * (1 - x[k][l][v]),name="ctr20_%k%l%v")

m.optimize()
m.write("out.lp")
CPU = tmr.stop()
m.setObjective(z)


print ('***********************************************************************************')

for k in H:
    for v in V:
        print("cost1[%d, %d] = %1.1f" % (k, v, cost1[k, v].x))


for k in H:
    for v in V:
        print("cost2[%d, %d] = %1.1f" % (k, v, cost2[k, v].x))



for i in N:
    for k in N:
       for v in V:
            if (x[i][k][v].x > 0.5):
                print('x(', i+1, ',', k+1, ',', v, ') ')



print "dataset instance p nv alpha obj CPU/elapsed Nodes routes"
print ("Problem:", DATA3.dataset, DATA3.instance, DATA3.p, "alpha:", DATA3.alpha, "Obj:", z.x, "CPU:", CPU,)
print ('***********************************************************************************')

#print('The model is infeasible; computing IIS')
#m.computeIIS()
#print('\nThe following constraint(s) cannot be satisfied:')
#for c in m.getConstrs():
 #   if c.IISConstr:
#        print('%s' % c.constrName)

# print cost1
# for k in H:
#     for v in V:
#         print cost1[k][v]
# print cost1.select(5, 0)
# print cost1.select(5, 0).getAttr('X')
# hubs = set([i for i in N if h[i].x > 0.5])
# print "hubs:", sorted(hubs)
# print "obj:", z.x
# RIGHT NOW WE HAVE X WITH THREE INDEX- NOT JUST I AND J : WE HAVE V INDEX AS WELL.
# successor = [[j for j in N if x[i][j][v].x > 0.5] for i in N]
# routes = [[i, j] for i in hubs for j in N if x[i][j].x > 0.5]
# for r in routes:
#     while r[-1] != r[0]:
#         r.append(successor[r[-1]][0])
#         print "routes:"
#        print "\n".join("\t" + " ".join("%d" % i for i in r) for r in routes)
# print "dataset instance p nv alpha obj CPU/elapsed Nodes routes"
# print "Problem:", DataAE.dataset, DataAE.instance, DataAE.p, DataAE.nv, "alpha:", DataAE.alpha, "Obj:", z.x, "CPU:", CPU, "routes:", routes
# # # # # # # # # # solve and report the solution # # # # #