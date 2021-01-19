package com.abdul;

import com.abdul.dbs.TurkishNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class PHMLRP {
    private double maxCost, maxNonMinEdgeCost, maxCostAfterOperation = 0;
    private final DS dataset;
    private final int numNodes, numHubs, numVehiclesPerHub;
    private int[] hubsArr;
    private final float collectionCostCFactor, distributionCostCFactor, hubToHubCFactor, removalPercentage;
    private ArrayList<List<Integer>> vehiclesList;
    private boolean[] isVisitedCity;
    private boolean isSimulatedAnnealing = false;
    private double saOperationCost;

    private double[] collectionCostArr;
    private double[] distributionCostArr;

    enum CostType {
        NORMAL, OPERATION
    }

    /**
     * Constructor
     */
    PHMLRP(DS dataset, int numNodes, int numHubs, int numVehicles,
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
        this.vehiclesList = new ArrayList<>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<>(list);
            this.vehiclesList.add(innerList);
        }
    }

    public boolean[] getIsVisitedCity() {
        return isVisitedCity;
    }

    void setIsVisitedCity(boolean[] isVisitedCity) {
        this.isVisitedCity = isVisitedCity;
    }

    public float getCollectionCostCFactor() {
        return collectionCostCFactor;
    }

    public float getDistributionCostCFactor() {
        return distributionCostCFactor;
    }

    public float getHubToHubCFactor() {
        return hubToHubCFactor;
    }

    public double[] getCollectionCostArr() {
        return collectionCostArr;
    }

    public double[] getDistributionCostArr() {
        return distributionCostArr;
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

    double calculateCost(CostType costType) {
        collectionCostArr = new double[numHubs * numVehiclesPerHub];
        distributionCostArr = new double[numHubs * numVehiclesPerHub];
        fillCollectionDistributionCostsArr(collectionCostArr, distributionCostArr);

        ArrayList<String> tempStrArr = new ArrayList<>();
        // loop on the hubs
        for (int h = 0; h < numHubs; h++) {
            double cost;
            // loop through vehicles in a hub
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
                double collectionCost = collectionCostArr[i];

                // loop on other vehicles in the same hub
                for (int ii = h * numVehiclesPerHub; ii < ((h + 1) * numVehiclesPerHub); ii++) {
                    // skipping current vehicle
                    if (i == ii) continue;

                    double distributionCost = distributionCostArr[ii];
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

                        double distributionCost = distributionCostArr[ii];
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

    private void fillCollectionDistributionCostsArr(double[] collectionCostArr, double[] distributionCostArr) {
        // calculate collection and distribution costs for vehicles and print them
        for (int h = 0; h < numHubs; h++) {
//            System.out.printf("Hub %d:\n", h);
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
                collectionCostArr[i] = calculateCollectionCost(h, i) * collectionCostCFactor;
                distributionCostArr[i] = calculateDistributionCost(h, i) * distributionCostCFactor;
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
        int randOpr = random.nextInt(12);
//        int randOpr = 7;

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
                operations.edgeOptWithinRoutes(false, -1, -1, -1, -1);
                break;
            case 5:
                operations.edgeOptInRoute(false, -1, -1, -1);
                break;
            case 6:
                operations.swapHubWithNode(false, -1, -1, -1);
                break;
            case 7:
                operations.twoOptAlgorithm();
                break;
            case 8:
                operations.localSearchInsertion();
                break;
            case 9:
                operations.localSearchSwap();
                break;
            case 10:
                operations.insertTwoNodes(false);
                break;
            case 11:
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
                return operations.edgeOptWithinRoutes(false, -1, -1, -1, -1);
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

    String getRoutes() {
        StringBuilder routes = new StringBuilder();
        // loop on hubs
        for (int i = 0; i < hubsArr.length; i++) {
            // loop on vehicles in a hub
            for (int j = 0; j < numVehiclesPerHub; j++) {
                routes.append(hubsArr[i] + 1).append("-");
                // loop on the vehicle's nodes
                for (int node : vehiclesList.get(numVehiclesPerHub * i + j)) {
                    routes.append(node + 1).append("-");
                }
                routes.append(hubsArr[i] + 1).append("; ");
            }
        }

        return routes.toString();
    }
}
