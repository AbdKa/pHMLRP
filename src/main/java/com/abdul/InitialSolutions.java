package com.abdul;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

class InitialSolutions {

    private PHMLRP phmlrp;
    private int numNodes, numHubs, numVehiclesPerHub;

    InitialSolutions(PHMLRP phmlrp) {
        this.phmlrp = phmlrp;
        this.numNodes = phmlrp.getNumNodes();
        this.numHubs = phmlrp.getNumHubs();
        this.numVehiclesPerHub = phmlrp.getNumVehiclesPerHub();
    }

    /*void testSameDistancesSum() {
        int[] nodesDistancesSum = new int[numNodes];
        int totalSum = 0;
        for (int i = 0; i < numNodes; i++) {
            int timeSum = 0;
            for (int j = 0; j < numNodes; j++) {
                timeSum += phmlrp.getDistance(i, j);
            }
            nodesDistancesSum[i] = timeSum;
            totalSum += timeSum;

            for (int j = 0; j < numNodes; j++) {
                if (i != j && nodesDistancesSum[i] == nodesDistancesSum[j]) {
                    System.out.println(i + " " + j + " = " + nodesDistancesSum[i]);
                }
            }
        }
    }*/

    void randomSolution() {
        // 1- pick hubs randomly
        pickHubs();
        // 2- assign non-hub nodes to hubs randomly
        // 3- distribute non-hubs on the vehicles
        assignNonHubsToVehicles();
    }

    void semiGreedySolution() {
        // 1- greedily pick hubs, after calculating the average distances for each node
        greedyPickHubs();
        // 2- greedily assign non-hub nodes to hubs
        // 3- distribute non-hubs on the vehicles
        assignNonHubsToVehicles();
    }

    void semiGreedySolution2() {
        // 1- greedily pick hubs, after calculating the average distances for each node
        pickHubs();
        // 2- greedily assign non-hub nodes to hubs
        // 3- distribute non-hubs on the vehicles
        greedilyAssignNonHubsToVehicles();
    }

    /**
     * Getters and setters
     **/
    void greedySolution() {
        // 1- greedily pick hubs, after calculating the average distances for each node
        greedyPickHubs();
        // 2- greedily assign non-hub nodes to hubs
        // 3- distribute non-hubs on the vehicles
        greedilyAssignNonHubsToVehicles();
    }

    private void pickHubs() {
        for (int i = 0; i < numHubs; i++) {
            Random rand = new Random();
            int randomNode = rand.nextInt(numNodes); // random node index

            if (phmlrp.getIsVisitedCity()[randomNode]) {
                i--;
                continue;
            }

            phmlrp.getHubsArr()[i] = randomNode;
            phmlrp.getIsVisitedCity()[randomNode] = true;
        }
    }

    private void greedyPickHubs() {
        Map<Integer, Integer> nodesDistanceAvg = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < numNodes; i++) {
            int sum = 0;
            for (int j = 0; j < numNodes; j++) {
                sum += phmlrp.getDistance(i, j);
            }
            nodesDistanceAvg.put(i, sum / numNodes);
        }

        nodesDistanceAvg = Utils.sortByValue(nodesDistanceAvg);

        int h = 0;
        for (Map.Entry node : nodesDistanceAvg.entrySet()) {
            if (h >= numHubs) break;
            phmlrp.getHubsArr()[h] = (int) node.getKey();
            phmlrp.getIsVisitedCity()[phmlrp.getHubsArr()[h]] = true;
            h++;
        }
    }

    private void assignNonHubsToVehicles() {
        /* Assigning non-hub nodes to vehicles */
        // min and max number of nodes in a vehicle
        int minNumOfNodesInVehicle = 1;
        int maxNumOfNodesInVehicle = numNodes - (numHubs * (numVehiclesPerHub + 1)) + 1;
        int remainingNodes = numNodes - numHubs;

        // loop through vehicles lists
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = phmlrp.getVehiclesList().size() - i;

            // this condition ensures that we do not run out of nodes
            if (remainingNodes - numOfNodesForVehicle >= remainingVehicles - 1) {
                // if one vehicle left, fill it with the remaining nodes
                if (remainingVehicles == 1) {
                    numOfNodesForVehicle = remainingNodes;
                }

                // subtract the number of nodes that will be added from the remaining nodes
                remainingNodes -= numOfNodesForVehicle;

                // filling in a vehicle's list with nodes
                for (int j = 0; j < numOfNodesForVehicle; j++) {
                    int randomNode = random.nextInt(numNodes);
                    if (phmlrp.getIsVisitedCity()[randomNode]) {
                        j--;
                        continue;
                    }

                    phmlrp.getVehiclesList().get(i).add(j, randomNode);
                    phmlrp.getIsVisitedCity()[randomNode] = true;
                }
            } else {
                i--;
            }
        }
    }

    private void greedilyAssignNonHubsToVehicles() {
        /* Assigning non-hub nodes to vehicles */
        // min and max number of nodes in a vehicle
        int minNumOfNodesInVehicle = 1;
        int maxNumOfNodesInVehicle = numNodes - (numHubs * (numVehiclesPerHub + 1)) + 1;
        int remainingNodes = numNodes - numHubs;

        // a hash map of the hub-to-node distances
        Map<Integer, Double> nodesToHubDistance = new LinkedHashMap<Integer, Double>();
        // loop through vehicles lists
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = phmlrp.getVehiclesList().size() - i;

            // if it's a new hub
            if (i % numVehiclesPerHub == 0) {
                // create a hash map of the hub-to-node distances
                // current hub
                int currentHub = i / numVehiclesPerHub;
                for (int node = 0; node < numNodes; node++) {
                    // loop through node and get distances to the current hub
                    // only if the node is non-hub
                    if (!phmlrp.getIsVisitedCity()[node])
                        nodesToHubDistance.put(node, phmlrp.getDistance(phmlrp.getHubsArr()[currentHub], node));
                }
                nodesToHubDistance = Utils.sortByValue(nodesToHubDistance);
            }

            // this condition ensures that we do not run out of nodes
            if (remainingNodes - numOfNodesForVehicle >= remainingVehicles - 1) {
                // if one vehicle left, fill it with the remaining nodes
                if (remainingVehicles == 1) {
                    numOfNodesForVehicle = remainingNodes;
                }

                // subtract the number of nodes that will be added from the remaining nodes
                remainingNodes -= numOfNodesForVehicle;

                // filling in a vehicle's list with nodes
                for (int j = 0; j < numOfNodesForVehicle; j++) {
                    Map.Entry<Integer, Double> entry = nodesToHubDistance.entrySet().iterator().next();
                    int closestNode = entry.getKey();
                    if (phmlrp.getIsVisitedCity()[closestNode]) {
                        j--;
                        continue;
                    }

                    phmlrp.getVehiclesList().get(i).add(j, closestNode);
                    phmlrp.getIsVisitedCity()[closestNode] = true;
                    // remove the visited node, because already added to a hub
                    nodesToHubDistance.remove(closestNode);
                }

            } else {
                i--;
            }
        }
    }

    /**
     * generate a probabilistic initial solution
     * pick hubs using roulette wheel
     **/
    void probabilisticInitSol() {
        ArrayList<Integer> nodesDistancesSum = new ArrayList<Integer>(numNodes);
        int totalSum = 0;
        // timeSum nodes distances and the total of all the summations
        for (int i = 0; i < numNodes; i++) {
            int sum = 0;
            for (int j = 0; j < numNodes; j++) {
                sum += phmlrp.getDistance(i, j);
            }
            nodesDistancesSum.add(sum);
            totalSum += sum;
        }

        Map<Integer, Double> normalizedSums = new LinkedHashMap<Integer, Double>();
        // normalize the summations to => timeSum/totalSum
        for (int i = 0; i < numNodes; i++) {
            double normalizedSum = (double) nodesDistancesSum.get(i) / totalSum;
            normalizedSums.put(i, normalizedSum);
//            System.out.println(i + "\t" + normalizedSum);
        }
        normalizedSums = Utils.sortByValue(normalizedSums);


        int[] hubsArr = new int[numHubs];
        boolean[] isVisitedCity = new boolean[numNodes];
        Random random = new Random();
        // loop through hubsArr to fill it
        for (int h = 0; h < hubsArr.length; h++) {
            // random probability
            double randomProb = random.nextDouble();
            int i = 0;  // i to check for the last entry in loop
            // loop through probabilities HashMap
            for (Map.Entry<Integer, Double> n : normalizedSums.entrySet()) {
                // if this is the last node but already visited
                if (i == normalizedSums.size() - 1 && n.getValue() == -1.0) {
                    h--;
                    break;
                }
                // if the randomProb is less than the current entry,
                // or if this is the last (highest probability) node
                if (randomProb < n.getValue() || i == normalizedSums.size() - 1) {
                    int hub = n.getKey();
                    // if an already added hub
                    if (isVisitedCity[hub]) {
                        h--;
                        continue;
                    }
//                    System.out.println("randomProb " + randomProb + " nodeProb " + n.getValue() +
//                            " hub " + hub + " distanceSum " + nodesDistancesSum.get(hub) +
//                            " totalSum " + totalSum);
                    hubsArr[h] = hub;
                    isVisitedCity[hub] = true;
                    totalSum -= nodesDistancesSum.get(hub);
                    n.setValue(-1.0);
                    // reset probabilities after changing the totalSum => timeSum/new totalSum
                    int ii = -1;
                    for (Map.Entry<Integer, Double> nn : normalizedSums.entrySet()) {
                        ii++;
                        // if the node is already selected as a hub, skip to the next
                        if (nn.getValue() == -1.0) continue;
                        // change the normalized sums. After the total change
                        double normalizedSum = (double) nodesDistancesSum.get(ii) / totalSum;
                        nn.setValue(normalizedSum);
//                        System.out.println(nn.getKey() + "\t" + normalizedSum);
                    }
                    break;
                }
                i++;
            }
        }

        phmlrp.setHubsArr(hubsArr);
        phmlrp.setIsVisitedCity(isVisitedCity);
        assignNodesRouletteWheel(isVisitedCity);
    }

    /**
     * Assign nodes to hubs using roulette wheel
     **/
    private void assignNodesRouletteWheel(boolean[] isVisitedCity) {
        /* Assigning non-hub nodes to vehicles */
        // min and max number of nodes in a vehicle
        int minNumOfNodesInVehicle = 1;
        int maxNumOfNodesInVehicle = numNodes - (numHubs * (numVehiclesPerHub + 1)) + 1;
        int remainingNodes = numNodes - numHubs;

        // a hash map of the hub-to-node distances
        Map<Integer, Double> normalizedDistances = new LinkedHashMap<Integer, Double>();
        // loop through vehicles lists
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = phmlrp.getVehiclesList().size() - i;

            // current hub
            int currentHub = i / numVehiclesPerHub;

            int distancesSum = 0;
            for (int node = 0; node < numNodes; node++) {
                // distancesSum hub to node distances
                distancesSum += phmlrp.getDistance(currentHub, node);
            }

            // if it's a new hub
            if (i % numVehiclesPerHub == 0) {
                // create a hash map of the hub-to-node distances
                for (int node = 0; node < numNodes; node++) {
                    // loop through node and get distances to the current hub
                    // only if the node is non-hub
                    if (!isVisitedCity[node]) {
                        double prob = (double) phmlrp.getDistance(phmlrp.getHubsArr()[currentHub], node) / distancesSum;
                        normalizedDistances.put(node, prob);
                    }
                }
                normalizedDistances = Utils.sortByValue(normalizedDistances);
            }

            // this condition ensures that we do not run out of nodes
            if (remainingNodes - numOfNodesForVehicle >= remainingVehicles - 1) {
                // if one vehicle left, fill it with the remaining nodes
                if (remainingVehicles == 1) {
                    numOfNodesForVehicle = remainingNodes;
                }

                // subtract the number of nodes that will be added from the remaining nodes
                remainingNodes -= numOfNodesForVehicle;

                // filling in a vehicle's list with nodes
                for (int j = 0; j < numOfNodesForVehicle; j++) {
                    // random probability
                    double randomProb = random.nextDouble();
                    int x = 0;  // x to check for the last entry in loop
                    // loop through probabilities HashMap
                    for (Map.Entry<Integer, Double> n : normalizedDistances.entrySet()) {
                        // if this is the last node but already visited
                        if (x == normalizedDistances.size() - 1 && n.getValue() == -1.0) {
                            j--;
                            break;
                        }
                        // if the randomProb is less than the current entry,
                        // or if this is the last (highest probability) node
                        if (randomProb < n.getValue() || x == normalizedDistances.size() - 1) {
                            int node = n.getKey();
                            // if an already assigned node
                            if (isVisitedCity[node]) {
                                j--;
                                continue;
                            }
//                            System.out.println("randomProb " + randomProb + " nodeProb " + n.getValue() +
//                                    " node " + node + " totalSum " + distancesSum);

                            phmlrp.getVehiclesList().get(i).add(j, node);
                            isVisitedCity[node] = true;
                            int oldDistancesSum = distancesSum;
                            distancesSum -= (distancesSum * n.getValue());
                            n.setValue(-1.0);
                            // remove the visited node, because already added to a hub
                            normalizedDistances.remove(node);

                            // reset probabilities after changing the totalSum => distancesSum/new totalSum
                            int ii = -1;
                            for (Map.Entry<Integer, Double> nn : normalizedDistances.entrySet()) {
                                ii++;
                                // if the node is already selected as a node, skip to the next
                                if (nn.getValue() == -1.0) continue;
                                // change the normalized sums. After the total change
                                double normalizedSum = (nn.getValue() * oldDistancesSum) / distancesSum;
                                nn.setValue(normalizedSum);
//                                System.out.println(nn.getKey() + "\t" + normalizedSum);
                            }
                            break;
                        }
                        x++;
                    }
                }

            } else {
                i--;
            }
        }
    }
}
