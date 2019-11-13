package com.abdul;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PHMLRP {
    private int maxCost = 0;
    private final int numNodes, numHubs, numVehiclesPerHub;
    private int[] hubsArr;
    private ArrayList<List<Integer>> vehiclesList;
    private boolean[] isVisitedCity;

    public PHMLRP(int numNodes, int numHubs, int numVehicles) {
        this.numNodes = numNodes;
        this.numHubs = numHubs;
        this.numVehiclesPerHub = numVehicles;
        hubsArr = new int[numHubs];
        vehiclesList = new ArrayList<List<Integer>>();
        isVisitedCity = new boolean[numNodes];
        for (int i = 0; i < numHubs * numVehicles; i++) {
            vehiclesList.add(new ArrayList<Integer>());
        }
    }

    public void randomSolution() {
        // 1- pick hubs randomly
        pickHubs();
        // 2- assign non-hub nodes to hubs randomly
        // 3- distribute non-hubs on the vehicles
        assignNonHubsToVehicles();
    }

    /**
     * @return maxCost of the current vehicles list
     */
    /*public int maxCost() {
        int maxCost = 0;
        // loop on the vehicles routes
        for (int i = 0; i < numHubs * numVehiclesPerHub; i++) {
            // maxCost between a hub and first node in a vehicle's route
            maxCost += getCost(hubsArr[i / numVehiclesPerHub], vehiclesList.get(i).get(0));
            // loop on a vehicle's list and calculating the maxCost within the route
            for (int j = 0; j < vehiclesList.get(i).size() - 1; j++) {
                maxCost += getCost(vehiclesList.get(i).get(j), vehiclesList.get(i).get(j + 1));
            }
            // the maxCost between the last city in a route (vehicle's list) and its hub
            int lastCity = vehiclesList.get(i).get(vehiclesList.get(i).size() - 1);
            maxCost += getCost(lastCity, hubsArr[i / numVehiclesPerHub]);
        }
        this.maxCost = maxCost;
        return maxCost;
    }*/

    /**
     * Getting the distance
     *
     * @param node1 from node1
     * @param node2 to node2
     * @return the distance as maxCost
     */
    private int getCost(int node1, int node2) {
        return TurkishNetwork.distance[node1][node2];
    }

    private void pickHubs() {
        for (int i = 0; i < numHubs; i++) {
            Random rand = new Random();
            int randomNode = rand.nextInt(numNodes); // random node index

            if (isVisitedCity[randomNode]) {
                i--;
                continue;
            }

            hubsArr[i] = randomNode;
            isVisitedCity[randomNode] = true;
        }
    }

    private void assignNonHubsToVehicles() {
        /* Assigning non-hub nodes to vehicles */
        // min and max number of nodes in a vehicle
        int minNumOfNodesInVehicle = 1;
        int maxNumOfNodesInVehicle = numNodes - (numHubs * (numVehiclesPerHub + 1)) + 1;
        int remainingNodes = numNodes - numHubs;

        // loop through vehicles lists
        for (int i = 0; i < vehiclesList.size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = vehiclesList.size() - i;

            // this condition ensures that we do not run out of nodes
            if (remainingNodes - numOfNodesForVehicle >= remainingVehicles - 1) {
                if (remainingVehicles == 1) {
                    numOfNodesForVehicle = remainingNodes;
                }

                remainingNodes -= numOfNodesForVehicle;

                // filling in a vehicle's list with nodes
                for (int j = 0; j < numOfNodesForVehicle; j++) {
                    int randomNode = random.nextInt(numNodes);
                    if (isVisitedCity[randomNode]) {
                        j--;
                        continue;
                    }

                    vehiclesList.get(i).add(j, randomNode);
                    isVisitedCity[randomNode] = true;
                }

                System.out.println();
            } else {
                i--;
            }
        }
    }

    int cost() {
        int[] distributionCostArr = new int[numHubs * numVehiclesPerHub];
        // calculate distribution costs for vehicles
        for (int h = 0; h < numHubs; h++) {
            System.out.printf("Hub %d:\n", h);
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
                int collectionCost = calculateCollectionCost(h, i);
                distributionCostArr[i] = calculateDistributionCost(h, i);
                System.out.printf("\tVehicle %d:\tCollection Cost: %d, Distribution Cost: %d\n",
                        i, collectionCost, distributionCostArr[i]);
            }
        }

        System.out.println();

        ArrayList<String> tempStrArr = new ArrayList<String>();
        // loop on the hubs
        for (int h = 0; h < numHubs; h++) {

//            System.out.println();
//            System.out.println("Hub " + h + ": ");

            int cost;
            // loop on vehicles in a hub
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {

//                System.out.print("Vehicle " + i + ": ");

                int collectionCost = calculateCollectionCost(h, i);

//                System.out.println("collectionCost: " + collectionCost);

                // loop on other vehicles in the same hub
                for (int ii = h * numVehiclesPerHub; ii < ((h + 1) * numVehiclesPerHub); ii++) {
                    // skipping current vehicle
                    if (i == ii) continue;

                    int distributionCost = distributionCostArr[ii];

//                    System.out.println("distributionCost of vehicle " + ii + ": " + distributionCost);

                    cost = collectionCost + distributionCost;

//                    System.out.println("cost to vehicle " + ii + ": " + cost);

                    if (cost > maxCost) {
                        maxCost = cost;
                    }
                }

                // loop through other hubs
                for (int hh = 0; hh < numHubs; hh++) {
                    // skipping current hub
                    if (h == hh) continue;

                    // cost between hubs
                    int betweenHubs = getCost(hubsArr[h], hubsArr[hh]);

                    if (h < hh && !tempStrArr.contains(h + "" + hh)) {
                        tempStrArr.add(h + "" + hh);
                        System.out.println("betweenHubs " + h + " and " + hh + ": " + betweenHubs);
                    }

                    // loop through other hub's vehicles
                    for (int ii = hh * numVehiclesPerHub; ii < ((hh + 1) * numVehiclesPerHub); ii++) {

                        int distributionCost = distributionCostArr[ii];

//                        System.out.println("distributionCost to vehicle " + ii + ": " + distributionCost);

                        cost = collectionCost + betweenHubs + distributionCost;

//                        System.out.println("cost to vehicle " + ii + ": " + cost);

                        if (cost > maxCost) {
                            maxCost = cost;
                        }
                    }
                }
            }
        }

        System.out.println();
        System.out.println("maxCost is: " + maxCost);
        return maxCost;
    }

    private int calculateDistributionCost(int h, int v) {
        int distributionCost = 0;
        // between a hub and first node in a vehicle list
        distributionCost += getCost(hubsArr[h], vehiclesList.get(v).get(0));
        // loop on a vehicle's list and calculating the distribution cost
        for (int j = 0; j < vehiclesList.get(v).size() - 1; j++) {
            distributionCost += getCost(vehiclesList.get(v).get(j), vehiclesList.get(v).get(j + 1));
        }
        return distributionCost;
    }

    private int calculateCollectionCost(int h, int i) {
        int collectionCost = 0;
        // loop on a vehicle's list and calculating the collection cost
        for (int j = 0; j < vehiclesList.get(i).size() - 1; j++) {
            collectionCost += getCost(vehiclesList.get(i).get(j), vehiclesList.get(i).get(j + 1));
        }
        int lastCityWithinVehicle = vehiclesList.get(i).get(vehiclesList.get(i).size() - 1);
        // the collection cost between the last city in a route (vehicle's list) and its hub
        collectionCost += getCost(lastCityWithinVehicle, hubsArr[h]);
        return collectionCost;
    }

    /*private void assignNonHubsToVehicles() {
      Dividing nodes on the hubs and vehicles

        // Per hub calculations
        int nodesPerHubCount = (numNodes - numHubs) / numHubs;
        int remainingNodesPerHub = (numNodes - numHubs) % numHubs;
        int firstHubNodesCount = nodesPerHubCount + remainingNodesPerHub;

        // Per vehicle calculations
        int nodesPerVehicleCount = nodesPerHubCount / numVehiclesPerHub;
        int remainingNodesPerVehicle = nodesPerHubCount % numVehiclesPerHub;
        int firstVehicleNodesCount = nodesPerVehicleCount + remainingNodesPerVehicle;

        // Calculations for the first hub
        int firstHubNodesPerVehicleCount = firstHubNodesCount / numVehiclesPerHub;
        int remainingFirstHubNodePerVehicle = firstHubNodesCount % numVehiclesPerHub;
        int firstHubFirstVehicleNodesCount = firstHubNodesPerVehicleCount + remainingFirstHubNodePerVehicle;

        // Assigning non-hub nodes to vehicles
        assignNodesToVehicles(nodesPerVehicleCount, firstVehicleNodesCount,
                firstHubNodesPerVehicleCount, firstHubFirstVehicleNodesCount);
    } */

    /*private void assignNodesToVehicles(int nodesPerVehicleCount, int firstVehicleNodesCount,
                                       int firstHubNodesPerVehicleCount, int firstHubFirstVehicleNodesCount) {
        // Filling in the vehicles lists by nodes randomly
         First Hub
        // The first vehicle in the first hub
        fillVehiclesLists(vehiclesList.get(0), firstHubFirstVehicleNodesCount);
        // The rest of vehicles in the first hub
        for (int i = 1; i < numVehiclesPerHub; i++) {
            fillVehiclesLists(vehiclesList.get(i), firstHubNodesPerVehicleCount);
        }

         Rest of hubs
        // The first vehicle in each hub
        for (int i = 1; i < numHubs; i++) {
            int firstVehicleIdx = i * numVehiclesPerHub;
            fillVehiclesLists(vehiclesList.get(firstVehicleIdx), firstVehicleNodesCount);
        }
        // The rest of vehicles in each hub
        for (int i = 1; i < numHubs; i++) {
            for (int j = i * numVehiclesPerHub + 1; j < numVehiclesPerHub * (i + 1); j++) {
                fillVehiclesLists(vehiclesList.get(j), nodesPerVehicleCount);
            }
        }
    }*/

    /*private void fillVehiclesLists(List<Integer> arr, int length) {
        for (int i = 0; i < length; i++) {
            Random random = new Random();
            int nodeNum = random.nextInt(numNodes);
            if (isVisitedCity[nodeNum]) {
                i--;
                continue;
            }
            arr.add(i, nodeNum);
            isVisitedCity[nodeNum] = true;
        }
    }*/

    /**
     * Prints the resulted hubs with their vehicles' routes
     *
     * @param verbose defines either nodes will be printed in words or numbers
     */
    void print(boolean verbose) {
        // loop on hubs
        for (int i = 0; i < hubsArr.length; i++) {
            if (verbose) {
                System.out.printf("Hub %d: %s%n", i, TurkishNetwork.nodes[hubsArr[i]]);
            } else {
                System.out.printf("Hub %d: %d%n", i, hubsArr[i]);
            }
            // loop on vehicles in a hub
            for (int j = 0; j < numVehiclesPerHub; j++) {
                System.out.printf("\tVehicle%d: ", j + 1);
                // loop on the vehicle's nodes
                for (int node : vehiclesList.get(numVehiclesPerHub * i + j)) {
                    if (verbose) {
                        System.out.printf("%s,", TurkishNetwork.nodes[node]);
                    } else {
                        System.out.printf("%d,", node);
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
        Bounds bounds = new Bounds();
        int bound = bounds.getBound(numNodes + "." + numHubs + "." + numVehiclesPerHub);
        System.out.println("**Total maxCost is " + this.maxCost);
        System.out.println("**The bound is " + bound);
        System.out.println();
    }
}
