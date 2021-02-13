# pHMLRP
pHMLRP (p-hub median location and routing problem)

## How to build and run
`mvn clean package` and `java -server -Xmx4g -jar target/pHMLRP.jar` will run the application with default values.




# Gurobi / python stuff
The top-level directory contains python scripts that call/use the Gurobi Optimizer.

###
TSP: if there is just one vehicle call `TSP.py TR 10 1 1 1 1 h 2`