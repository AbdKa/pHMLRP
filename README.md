# pHMLRP

We introduce the incomplete p-hub center and routing network design problem (IpHCVRP).
For [pHCVRP](https://link.springer.com/article/10.1007/s00291-018-0526-2), see [Kartal et al](https://link.springer.com/article/10.1007/s00291-018-0526-2).

## How to build and run
`mvn clean package` and `java -server -Xmx4g -jar target/pHMLRP.jar` will run the application with default values.




# Gurobi / python stuff
The top-level directory contains python scripts that call/use the [Gurobi Optimizer](https://www.gurobi.com).

### TSP
If there is just one vehicle call `python\TSP.py TR 10 1 1 1 1 h 2`