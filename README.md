# IpHCVRP

We introduce the *incomplete* p-hub center and routing network design problem (IpHCVRP).
For [pHCVRP](https://link.springer.com/article/10.1007/s00291-018-0526-2), see [Kartal et al](https://link.springer.com/article/10.1007/s00291-018-0526-2).

## How to build and run
`mvn clean package` and `java -server -Xmx4g -jar target/pHMLRP.jar` will run the application with default values.


# Gurobi / python stuff
The top-level directory contains python scripts that call/use the [Gurobi Optimizer](https://www.gurobi.com).

### pHubCenter.py
After gurobi optimization, it calls TSP.py or NVEG.py for each hub and passes a json file name that has been created (pHC_MTSP_p_h_nv.json) as an argument
to fill in the routes array in json.
command `python pHubCenter.py TR 10 2 2 1 1 1`

### pHubCenter_H.py
Like pHubCenter.py but accepts hubs list as an argument.
command `python pHubCenter_H.py TR 10 2 2 1 1 1 h 5,7`

### TSP.py
If there is just one vehicle (called by pHubCenter.py and pHubCenter_H.py):
`python TSP.py json_file TR 10 1 1 1 1 h 1 s 2,3,4,5,6,7,8,9,10`

### NVEG.py
If there are more than one vehicle (called by pHubCenter.py and pHubCenter_H.py):
`python NVEG.py json_file TR 10 1 2 1 1 h 1 s 2,3,4,5,6,7,8,9,10`


## How to get all runs on a server machine
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

## Author
Please feel free to contact Abdul Kader Kassoumeh at `abood.kassoumeh@gmail.com` if you have any questions, comments or contributions.

## Citation Policy
If you use this library for research purposes, please use the following citation:

``` tex
@article{
  author = "Kassoumeh, Abdul Kader and Kartal, Z{\"u}hal and Arslan, Ahmet",
  title = "The Effect of Different Initial Solutions on the Metaheuristic Algorithms for the Single Allocation \textit{p}-Hub Center and Routing Problem",
  journal={PeerJ Computer Science},
  pages={to appear},
  year={2025},
  note= {[Manuscript submitted for publication]}
}
```
