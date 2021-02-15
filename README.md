# IpHCVRP

We introduce the *incomplete* p-hub center and routing network design problem (IpHCVRP).
For [pHCVRP](https://link.springer.com/article/10.1007/s00291-018-0526-2), see [Kartal et al](https://link.springer.com/article/10.1007/s00291-018-0526-2).

## How to build and run
`mvn clean package` and `java -server -Xmx4g -jar target/pHMLRP.jar` will run the application with default values.




# Gurobi / python stuff
The top-level directory contains python scripts that call/use the [Gurobi Optimizer](https://www.gurobi.com).

### TSP
If there is just one vehicle call `python3 python/TSP.py TR 10 1 1 1 1 h 2`

# How to get all runs on a server machine
Run the command: `java -server -Xmx3g -cp target/pHMLRP.jar com.abdul.Benchmark 2>runs.log` 
Algorithms like SA and VNS prints the desired output (e.g. objective function, running time etc) to the standard error.
The command captures the desired output by saving the standard error to a file (runs.log).
Later of you can open this dumpfile with excel and apply text to columns (split by dash) transformation.
You can create pivot tables, analyze the results (min, max, average) and compare algorithms.


## Dependencies

* JDK 9 or above
* Apache Maven 3.6.3 or above
* Python 3.9.1 or above
* Numpy 1.20.1 or above
* Gurobi 9.1.1 or above
* python3 /Library/gurobi911/mac64/setup.py `build` and then `install`
* python3.9 -m pip install pulp --user
* pip3 install dlib