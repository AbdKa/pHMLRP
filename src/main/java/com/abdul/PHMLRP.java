package com.abdul;

import com.abdul.dbs.*;

import java.util.*;

class PHMLRP {
    private double maxCost, maxNonMinEdgeCost, maxCostAfterOperation = 0;
    private String dataset;
    private final int numNodes, numHubs, numVehiclesPerHub;
    private int[] hubsArr;
    private final float collectionCostCFactor, distributionCostCFactor, hubToHubCFactor, removalPercentage;
    private ArrayList<List<Integer>> vehiclesList;
    private boolean[] isVisitedCity;
    private boolean isSimulatedAnnealing = false;
    private double saOperationCost;

    enum CostType {
        NORMAL, OPERATION
    }

    /**
     * Constructor
     * */
    PHMLRP(String dataset, int numNodes, int numHubs, int numVehicles,
           float collectionCostCFactor, float distributionCostCFactor, float hubToHubCFactor,
           float removalPercentage) {
        this.dataset = dataset;
        this.numNodes = numNodes;
        this.numHubs = numHubs;
        this.numVehiclesPerHub = numVehicles;
        this.collectionCostCFactor = collectionCostCFactor;
        this.distributionCostCFactor = distributionCostCFactor;
        this.hubToHubCFactor = hubToHubCFactor;
        this.removalPercentage = removalPercentage;
        hubsArr = new int[numHubs];
        vehiclesList = new ArrayList<List<Integer>>();
        isVisitedCity = new boolean[numNodes];
        for (int i = 0; i < numHubs * numVehicles; i++) {
            vehiclesList.add(new ArrayList<Integer>());
        }
    }

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
     * **/
    void greedySolution() {
        // 1- greedily pick hubs, after calculating the average distances for each node
        greedyPickHubs();
        // 2- greedily assign non-hub nodes to hubs
        // 3- distribute non-hubs on the vehicles
        greedilyAssignNonHubsToVehicles();
    }

    void setSimulatedAnnealing(boolean simulatedAnnealing) {
        isSimulatedAnnealing = simulatedAnnealing;
    }

    int getNumNodes() {
        return numNodes;
    }

    int getNumHubs() {
        return numHubs;
    }

    double getMaxCost() {
        return maxCost;
    }

    void setMaxCost(double originalMaxCost) {
        this.maxCost = originalMaxCost;
        saOperationCost = originalMaxCost;
    }

    double getSaOperationCost() {
        return saOperationCost;
    }

    int getNumVehiclesPerHub() {
        return numVehiclesPerHub;
    }

    int[] getHubsArr() {
        return hubsArr;
    }

    ArrayList<List<Integer>> getVehiclesList() {
        return vehiclesList;
    }

    void setHubsArr(int[] hubsArr) {
        this.hubsArr = hubsArr;
    }

    void addRouteToVehiclesList(List<Integer> route) {
        vehiclesList.add(route);
    }

    void resetVehiclesList(ArrayList<List<Integer>> vehiclesList) {
        this.vehiclesList = new ArrayList<List<Integer>>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<Integer>(list);
            this.vehiclesList.add(innerList);
        }
    }

    void setIsVisitedCity(boolean[] isVisitedCity) {
        this.isVisitedCity = isVisitedCity;
    }

    /**
     * Getting the distance
     *
     * @param node1 from node1
     * @param node2 to node2
     * @return the distance as maxCost
     */
    double getDistance(int node1, int node2) {
        return new Dataset().getDistance(dataset, node1, node2);
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

    private void greedyPickHubs() {
        Map<Integer, Integer> nodesDistanceAvg = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < numNodes; i++) {
            int sum = 0;
            for (int j = 0; j < numNodes; j++) {
                sum += getDistance(i, j);
            }
            nodesDistanceAvg.put(i, sum / numNodes);
        }

        nodesDistanceAvg = Utils.sortByValue(nodesDistanceAvg);

        int h = 0;
        for (Map.Entry node : nodesDistanceAvg.entrySet()) {
            if (h >= numHubs) break;
            hubsArr[h] = (int) node.getKey();
            isVisitedCity[hubsArr[h]] = true;
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
        for (int i = 0; i < vehiclesList.size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = vehiclesList.size() - i;

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
                    if (isVisitedCity[randomNode]) {
                        j--;
                        continue;
                    }

                    vehiclesList.get(i).add(j, randomNode);
                    isVisitedCity[randomNode] = true;
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
        for (int i = 0; i < vehiclesList.size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = vehiclesList.size() - i;

            // if it's a new hub
            if (i % numVehiclesPerHub == 0) {
                // create a hash map of the hub-to-node distances
                // current hub
                int currentHub = i / numVehiclesPerHub;
                for (int node = 0; node < numNodes; node++) {
                    // loop through node and get distances to the current hub
                    // only if the node is non-hub
                    if (!isVisitedCity[node])
                        nodesToHubDistance.put(node, getDistance(hubsArr[currentHub], node));
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
                    if (isVisitedCity[closestNode]) {
                        j--;
                        continue;
                    }

                    vehiclesList.get(i).add(j, closestNode);
                    isVisitedCity[closestNode] = true;
                    // remove the visited node, because already added to a hub
                    nodesToHubDistance.remove(closestNode);
                }

            } else {
                i--;
            }
        }
    }

    double calculateCost(CostType costType) {
        int[] collectionCostArr = new int[numHubs * numVehiclesPerHub];
        int[] distributionCostArr = new int[numHubs * numVehiclesPerHub];
        fillInCollectionAndDistributionCostArrAndPrint(collectionCostArr, distributionCostArr);

        ArrayList<String> tempStrArr = new ArrayList<String>();
        // loop on the hubs
        for (int h = 0; h < numHubs; h++) {
            double cost;
            // loop through vehicles in a hub
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
                int collectionCost = collectionCostArr[i];

                // loop on other vehicles in the same hub
                for (int ii = h * numVehiclesPerHub; ii < ((h + 1) * numVehiclesPerHub); ii++) {
                    // skipping current vehicle
                    if (i == ii) continue;

                    int distributionCost = distributionCostArr[ii];
                    cost = collectionCost + distributionCost;

                    setMaxCostForEachType(costType, cost);
                }

                // loop through other hubs
                for (int hh = 0; hh < numHubs; hh++) {
                    // skipping current hub
                    if (h == hh) continue;

                    // calculateCost between hubs
                    double betweenHubs = Math.round(getDistance(hubsArr[h], hubsArr[hh]) * hubToHubCFactor);

                    if (h < hh && !tempStrArr.contains(h + "" + hh)) {
                        tempStrArr.add(h + "" + hh);
//                        System.out.println("betweenHubs " + h + " and " + hh + ": " + betweenHubs);
                    }

                    // loop through other hub's vehicles
                    for (int ii = hh * numVehiclesPerHub; ii < ((hh + 1) * numVehiclesPerHub); ii++) {

                        int distributionCost = distributionCostArr[ii];
                        cost = collectionCost + betweenHubs + distributionCost;

                        setMaxCostForEachType(costType, cost);
                    }
                }
            }
        }

//        System.out.println("maxCostAfterOperation: " + maxCostAfterOperation);
        if (costType == CostType.OPERATION && maxCost > maxCostAfterOperation) {
            maxCost = maxCostAfterOperation;
        }

        saOperationCost = maxCostAfterOperation;
        if (maxCostAfterOperation <= 0) saOperationCost = maxCost;

        maxCostAfterOperation = 0;

        return maxCost;
    }

    private void setMaxCostForEachType(CostType costType, double cost) {
        if (costType == CostType.NORMAL) {
            if (cost > maxCost) {
                maxCost = cost;
            }
        } else if (costType == CostType.OPERATION) {
            if (cost > maxCostAfterOperation) {
                maxCostAfterOperation = cost;
            }
        }
    }

    double costWithoutMinEdge() {
        int cost;

        // loop on the hubs
        for (int h = 0; h < numHubs; h++) {
            // loop through vehicles in a hub
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
                int minEdgeFirstNode = getMinEdgeFirstNode(h, i);
//                System.out.println(minEdgeFirstNode);
                cost = getCollectionAndDistributionCostNoMinEdge(h, i, minEdgeFirstNode);
//                System.out.println("Vehicle " + i + " costNoMin: " + cost);
                if (cost > maxNonMinEdgeCost) {
                    maxNonMinEdgeCost = cost;
                }
            }
        }

//        System.out.println();
//        System.out.println("Max Cost without the minimum edge is: " + maxNonMinEdgeCost);
        return maxNonMinEdgeCost;
    }

    private int getMinEdgeFirstNode(int h, int v) {
        // between a hub and first node in a vehicle list
        int minEdgeFirstNode = hubsArr[h];
        double minEdge = getDistance(hubsArr[h], vehiclesList.get(v).get(0));
        // loop on a vehicle's list and calculating the distribution calculateCost
        for (int j = 0; j < vehiclesList.get(v).size() - 1; j++) {
            double edgeCost = getDistance(vehiclesList.get(v).get(j), vehiclesList.get(v).get(j + 1));
            if (edgeCost < minEdge) {
                minEdge = edgeCost;
                minEdgeFirstNode = vehiclesList.get(v).get(j);
            }
        }

        int lastCityWithinVehicle = vehiclesList.get(v).get(vehiclesList.get(v).size() - 1);
        // the collection calculateCost between the last city in a route (vehicle's list) and its hub
        double lastEdge = getDistance(lastCityWithinVehicle, hubsArr[h]);
        if (lastEdge < minEdge) {
            minEdgeFirstNode = lastCityWithinVehicle;
        }

        return minEdgeFirstNode;
    }

    private void fillInCollectionAndDistributionCostArrAndPrint(int[] collectionCostArr, int[] distributionCostArr) {
        // calculate collection and distribution costs for vehicles and print them
        for (int h = 0; h < numHubs; h++) {
//            System.out.printf("Hub %d:\n", h);
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
                collectionCostArr[i] = Math.round(calculateCollectionCost(h, i) * collectionCostCFactor);
                distributionCostArr[i] = Math.round(calculateDistributionCost(h, i) * distributionCostCFactor);
//                System.out.printf("\tVehicle %d:\tCollection Cost: %d, Distribution Cost: %d\n",
//                        i, collectionCostArr[i], distributionCostArr[i]);
            }
        }

//        System.out.println();
    }

    private int calculateDistributionCost(int h, int v) {
        int distributionCost = 0;
        // between a hub and first node in a vehicle list
        distributionCost += getDistance(hubsArr[h], vehiclesList.get(v).get(0));
        // loop on a vehicle's list and calculating the distribution calculateCost
        for (int j = 0; j < vehiclesList.get(v).size() - 1; j++) {
            distributionCost += getDistance(vehiclesList.get(v).get(j), vehiclesList.get(v).get(j + 1));
        }
        return distributionCost;
    }

    private int calculateCollectionCost(int h, int v) {
        int collectionCost = 0;
        // loop on a vehicle's list and calculating the collection calculateCost
        for (int j = 0; j < vehiclesList.get(v).size() - 1; j++) {
            collectionCost += getDistance(vehiclesList.get(v).get(j), vehiclesList.get(v).get(j + 1));
        }
        // the collection calculateCost between the last city in a route (vehicle's list) and its hub
        int lastCityWithinVehicle = vehiclesList.get(v).get(vehiclesList.get(v).size() - 1);
        collectionCost += getDistance(lastCityWithinVehicle, hubsArr[h]);
        return collectionCost;
    }

    private int getCollectionAndDistributionCostNoMinEdge(int h, int v, int minEdgeFirstNode) {
        int collectionCost = 0;
        int distributionCost = 0;

        int distributionStartIndex = 0;

        if (hubsArr[h] != minEdgeFirstNode) {
            // if first edge is not the minimum
            // between a hub and first node in a vehicle list
            collectionCost += getDistance(hubsArr[h], vehiclesList.get(v).get(0));
            // loop on a vehicle's list and calculating the collection calculateCost
            for (int j = 0; j < vehiclesList.get(v).size() - 1; j++) {
                distributionStartIndex = j + 1;
                if (vehiclesList.get(v).get(j) == minEdgeFirstNode) {
                    break;
                }
                collectionCost += getDistance(vehiclesList.get(v).get(j), vehiclesList.get(v).get(j + 1));
            }
        }


        // loop on a vehicle's list and calculating the distribution calculateCost
        for (int j = distributionStartIndex; j < vehiclesList.get(v).size() - 1; j++) {
            distributionCost += getDistance(vehiclesList.get(v).get(j), vehiclesList.get(v).get(j + 1));
        }
        int lastCityWithinVehicle = vehiclesList.get(v).get(vehiclesList.get(v).size() - 1);
        if (lastCityWithinVehicle != minEdgeFirstNode) {
            // if last edge is not the minimum
            // the calculateCost between the last node in a route (vehicle's list) and its hub
            distributionCost += getDistance(lastCityWithinVehicle, hubsArr[h]);
        }

        collectionCost = Math.round(collectionCost * collectionCostCFactor);
        distributionCost = Math.round(distributionCost * distributionCostCFactor);

        return collectionCost + distributionCost;
    }

    void randomOperation() {
        // TODO: Ask?? In a specific operation, if the randomly selected route does not satisfy our criteria,
        //  shall we reselect another one or just abort the operation then randomly select a new operation.
        Random random = new Random();
//        int randOpr = random.nextInt(7);
        int randOpr = 7;

        Operations operations = new Operations(this);

        switch (randOpr) {
            case 0:
                operations.insertNodeInRoute(false, -1, -1, -1);
                break;
            case 1:
                operations.insertNodeBetweenRoutes(false, -1, -1, -1, -1);
                break;
            case 2:
                operations.swapNodeInRoute(false, -1, -1, -1);
                break;
            case 3:
                operations.swapNodeWithinRoutes(false, -1, -1, -1, -1);
                break;
            case 4:
                operations.edgeOpt(false);
                break;
            case 5:
                operations.swapHubWithNode(false, -1, -1, -1);
                break;
            case 6:
                operations.twoOptAlgorithm();
                break;
            case 7:
                operations.localSearchInsertion();
                break;
            case 8:
                operations.localSearchSwap();
                break;
            case 9:
                operations.insertTwoNodes(false);
                break;
            case 10:
                operations.nodesRemoveAndGreedyInsert(removalPercentage);
                break;
        }

        print(false);
    }

    boolean callOperation(int operationNumber) {
        if (operationNumber == 7) {
            // called NodesRemoveAndGreedyInsert operation
            return calledNodesRemoveAndGreedyInsert();
        }

        Operations operations = new Operations(this);
        switch (operationNumber) {
            case 0:
                return operations.insertNodeBetweenRoutes(false, -1, -1, -1, -1);
            case 1:
                return operations.edgeOpt(false);
            case 2:
                return operations.insertTwoNodes(false);
            case 3:
                return operations.twoOptAlgorithm();
            case 4:
                return operations.insertNodeInRoute(false, -1, -1, -1);
            case 5:
                return operations.swapNodeInRoute(false, -1, -1, -1);
            case 6:
                return operations.swapNodeWithinRoutes(false, -1, -1, -1, -1);
            case 8:
                return operations.swapHubWithNode(false, -1, -1, -1);
        }

        return false;
    }

    private boolean calledNodesRemoveAndGreedyInsert() {
        PHMLRP copyPHMLRP = new PHMLRP(dataset, numNodes, numHubs, numVehiclesPerHub,
                collectionCostCFactor, distributionCostCFactor, hubToHubCFactor,
                removalPercentage);
        copyPHMLRP.setMaxCost(maxCost);
        copyPHMLRP.setHubsArr(hubsArr);
        copyPHMLRP.setIsVisitedCity(isVisitedCity);
        copyPHMLRP.resetVehiclesList(vehiclesList);

        Operations operations = new Operations(copyPHMLRP);
        operations.nodesRemoveAndGreedyInsert(removalPercentage);

        if (copyPHMLRP.getMaxCost() >= maxCost) {
            return false;
        }

        setMaxCost(copyPHMLRP.getMaxCost());
        setHubsArr(copyPHMLRP.getHubsArr());
        resetVehiclesList(copyPHMLRP.getVehiclesList());
        return true;
    }

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
                System.out.printf("\tVehicle%d: ", j);
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
//        Bounds bounds = new Bounds();
//        int bound = bounds.getBound(numNodes + "." + numHubs + "." + numVehiclesPerHub);
        System.out.println("**Total maxCost is " + this.maxCost);
//        System.out.println("**Total maxNonMinEdgeCost is " + this.maxNonMinEdgeCost);
//        System.out.println("**The bound is " + bound);
        System.out.println();
    }
}
