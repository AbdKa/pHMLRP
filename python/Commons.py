from gurobipy import *

# https://support.gurobi.com/hc/en-us/community/posts/360047967872-Gurobi-status-code
def status_code(i):
    sc = gurobipy.StatusConstClass
    d = {sc.__dict__[k]: k for k in sc.__dict__.keys() if k[0] >= 'A' and k[0] <= 'Z'}
    return d[i]
