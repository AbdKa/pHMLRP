#!/usr/bin/env bash

declare -a instances

instances=(
TR.10.2.1 TR.10.2.2 TR.10.3.1  TR.15.2.1 TR.15.2.2
TR.25.2.1 TR.25.2.5 TR.25.5.1 TR.25.5.2
TR.50.2.1 TR.50.2.5 TR.50.5.1 TR.50.5.2
TR.81.2.1 TR.81.2.5 TR.81.5.1 TR.81.5.2
TR.81.9.1 TR.81.9.2 TR.81.9.3 TR.81.9.4 TR.81.9.5
AP100.100.5.1 AP100.100.5.2 AP100.100.5.5
AP200.200.10.1 AP200.200.10.2 AP200.200.10.5
CAB.10.2.1 CAB.10.2.2 CAB.10.3.1
CAB.15.2.1 CAB.15.2.2 CAB.25.2.1
CAB.25.2.5 CAB.25.5.1 CAB.25.5.2)

for i in "${instances[@]}"
do
 echo "python3 python/pHubCenter.py ${i//'.'/ }"
 #python3 python/pHubCenter.py ${i//'.'/ }
done

