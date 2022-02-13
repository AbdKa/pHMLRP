package com.abdul;

import java.util.ArrayList;
import java.util.List;

public class PHCRP {
    private double initCPU;
    private double maxCost, maxNonMinEdgeCost, maxCostAfterOperation = 0;
    private final DS dataset;
    private final int numNodes, numHubs, numVehiclesPerHub;
    private int[] hubsArr;
    private final float collectionCostCFactor, distributionCostCFactor, hubToHubCFactor, removalPercentage;
    private ArrayList<List<Integer>> vehiclesList;
    private boolean[] isVisitedCity;
    private double saOperationCost;
    private double[] collectionCostArr;
    private double[] distributionCostArr;

    public enum CostType {
        NORMAL, OPERATION
    }

    private boolean silent = false;

    void setSilent(boolean silent) {
        this.silent = silent;
    }

    public double getInitCPU() {
        return initCPU;
    }

    public void setInitCPU(double initCPU) {
        this.initCPU = initCPU;
    }

    /**
     * Constructor
     */
    PHCRP(DS dataset, int numNodes, int numHubs, int numVehicles,
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
        vehiclesList = new ArrayList<>();
        isVisitedCity = new boolean[numNodes];
        for (int i = 0; i < numHubs * numVehicles; i++) {
            vehiclesList.add(new ArrayList<>());
        }
    }

    /**
     * Copy a PHCRP object
     */
    public PHCRP(PHCRP pHCRP) {
        this.dataset = pHCRP.getDataset();
        this.numNodes = pHCRP.getNumNodes();
        this.numHubs = pHCRP.getNumHubs();
        this.numVehiclesPerHub = pHCRP.getNumVehiclesPerHub();
        this.collectionCostCFactor = pHCRP.getCollectionCostCFactor();
        this.distributionCostCFactor = pHCRP.getDistributionCostCFactor();
        this.hubToHubCFactor = pHCRP.getHubToHubCFactor();
        this.removalPercentage = pHCRP.getRemovalPercentage();
        hubsArr = pHCRP.getHubsArr().clone();
        setVehiclesList(pHCRP.getVehiclesList());
        this.isVisitedCity = pHCRP.isVisitedCity.clone();
        this.maxCost = pHCRP.getMaxCost();
        this.saOperationCost = pHCRP.getSaOperationCost();
    }

    DS getDataset() {
        return dataset;
    }

    public int getNumNodes() {
        return numNodes;
    }

    int getNumHubs() {
        return numHubs;
    }

    public double getMaxCost() {
        return maxCost;
    }

    void setMaxCost(double originalMaxCost) {
        this.maxCost = originalMaxCost;
        saOperationCost = originalMaxCost;
    }

    public double getSaOperationCost() {
        return saOperationCost;
    }

    public void setSaOperationCost(double cost) {
        this.saOperationCost = cost;
    }

    int getNumVehiclesPerHub() {
        return numVehiclesPerHub;
    }

    int[] getHubsArr() {
        return hubsArr;
    }

    String getHubsString() {
        StringBuilder hubs = new StringBuilder();
        for (int hub : hubsArr) {
            hubs.append(hub + 1).append(";");
        }

        return hubs.substring(0, hubs.length() - 1);
    }

    ArrayList<List<Integer>> getVehiclesList() {
        return vehiclesList;
    }

    void setHubsArr(int[] hubsArr) {
        this.hubsArr = hubsArr;
    }

    void setRouteInVehiclesList(int index, List<Integer> route) {
        vehiclesList.set(index, route);
    }

    void setVehiclesList(ArrayList<List<Integer>> vehiclesList) {
        this.vehiclesList = new ArrayList<>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<>(list);
            this.vehiclesList.add(innerList);
        }
    }

    String getVehiclesListString() {
        StringBuilder routes = new StringBuilder();
        for (int i = 0; i < vehiclesList.size(); i++) {
            List<Integer> route = vehiclesList.get(i);
            int hub = hubsArr[i / numVehiclesPerHub];
            routes.append(hub + 1).append("-");

            for (int node : route) {
                routes.append(node + 1).append("-");
            }
            routes.append(hub + 1).append("; ");
        }

        return routes.toString();
    }

    boolean[] getIsVisitedCity() {
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

    public float getRemovalPercentage() {
        return removalPercentage;
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
        return Dataset.getDistance(dataset, node1, node2);
    }

    public double calculateCost(CostType costType) {
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

    boolean move(int operationNumber) {
//        if (operationNumber == 7) {
//            // called NodesRemoveAndGreedyInsert operation
//            return calledNodesRemoveAndGreedyInsert();
//        }

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
            case 7:
                return operations.swapHubWithNode(false, -1, -1, -1);
        }

        return false;
    }

    private boolean calledNodesRemoveAndGreedyInsert() {
        PHCRP copyPHCRP = new PHCRP(dataset, numNodes, numHubs, numVehiclesPerHub,
                collectionCostCFactor, distributionCostCFactor, hubToHubCFactor,
                removalPercentage);
        copyPHCRP.setMaxCost(maxCost);
        copyPHCRP.setHubsArr(hubsArr);
        copyPHCRP.setIsVisitedCity(isVisitedCity);
        copyPHCRP.setVehiclesList(vehiclesList);

        Operations operations = new Operations(copyPHCRP);
        operations.nodesRemoveAndGreedyInsert(removalPercentage);

        if (copyPHCRP.getMaxCost() >= maxCost) {
            return false;
        }

        setMaxCost(copyPHCRP.getMaxCost());
        setHubsArr(copyPHCRP.getHubsArr());
        setVehiclesList(copyPHCRP.getVehiclesList());
        return true;
    }

    /**
     * Prints the resulted hubs with their vehicles' routes
     */
    void print() {
        if (silent) return;
        // loop on hubs
        for (int i = 0; i < hubsArr.length; i++) {
            System.out.printf("Hub %d: %d%n", i, hubsArr[i]);
            // loop on vehicles in a hub
            for (int j = 0; j < numVehiclesPerHub; j++) {
                System.out.printf("\tVehicle%d: ", j);
                // loop on the vehicle's nodes
                for (int node : vehiclesList.get(numVehiclesPerHub * i + j)) {
                    System.out.printf("%d,", node);
                }
                System.out.println();
            }
            System.out.println();
        }

        System.out.println("**Total maxCost is " + this.maxCost);
//        System.out.println("**Total maxNonMinEdgeCost is " + this.maxNonMinEdgeCost);
//        System.out.println("**The bound is " + bound);
        System.out.println();
    }

    public String getRoutes() {
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

    /**
     * Checks solution feasibility
     */
    boolean isFeasible() {
        for (int n = 0; n < numNodes; n++) {
            int count = 0;
            // loop on hubs
            for (int h : hubsArr) {
                if (n == h) count++;
            }
            // loop on vehicle's nodes
            for (int j = 0; j < numVehiclesPerHub * hubsArr.length; j++) {
                for (int node : vehiclesList.get(j)) {
                    if (n == node) count++;
                }
            }

            if (count != 1)
                return false;
        }

        return true;
    }
}
