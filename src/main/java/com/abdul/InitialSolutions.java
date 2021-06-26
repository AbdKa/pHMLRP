package com.abdul;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

class InitialSolutions {

    private PHCRP pHCRP;
    private DS dataset;
    private int numNodes;
    private int numHubs;
    private int numVehiclesPerHub;

    InitialSolutions(PHCRP pHCRP, Params params, boolean doInit) {
        init(pHCRP, params);

        if (doInit)
            doInitSol(params.getInitSol());
    }

    private void init(PHCRP pHCRP, Params params) {
        this.pHCRP = pHCRP;
        this.dataset = params.getDataset();
        this.numNodes = pHCRP.getNumNodes();
        this.numHubs = pHCRP.getNumHubs();
        this.numVehiclesPerHub = pHCRP.getNumVehiclesPerHub();
    }

    private void doInitSol(IS initSol) {
        switch (initSol) {
            case RND:
                randomSolution();
                break;
            case GREEDY:
                greedySolution();
                break;
            case GREEDY_RND:
                greedyRandomSolution();
                break;
            case RND_GREEDY:
                randomGreedySolution();
                break;
            case PROB:
                probabilisticInitSol();
                break;
            case GREEDY_GRB:
            case GRB:
                gurobiSolution(initSol);
                break;
        }
    }

    //    #1 RND
    void randomSolution() {
        long startTime = System.nanoTime();
        // 1- pick hubs randomly
        randomlyPickHubs();
        // 2- assign non-hub nodes to hubs randomly
        // 3- distribute non-hubs on the vehicles
        randomlyAssignNonHubsToVehicles();
        double cpu = (System.nanoTime() - startTime) / 1e9;
        pHCRP.setInitCPU(cpu);
    }

    //    #2 GREEDY
    void greedySolution() {
        long startTime = System.nanoTime();
        // 1- greedily pick hubs, after calculating the average distances for each node
        greedyPickHubs();
        // 2- greedily assign non-hub nodes to hubs
        // 3- distribute non-hubs on the vehicles
        greedilyAssignNonHubsToVehicles();
        double cpu = (System.nanoTime() - startTime) / 1e9;
        pHCRP.setInitCPU(cpu);
    }

    //    #3 GREEDY_RND
    void greedyRandomSolution() {
        long startTime = System.nanoTime();
        // 1- greedily pick hubs, after calculating the average distances for each node
        greedyPickHubs();
        // 2- greedily assign non-hub nodes to hubs
        // 3- distribute non-hubs on the vehicles
        randomlyAssignNonHubsToVehicles();
        double cpu = (System.nanoTime() - startTime) / 1e9;
        pHCRP.setInitCPU(cpu);
    }

    //    #4 RND_GREEDY
    void randomGreedySolution() {
        long startTime = System.nanoTime();
        // 1- greedily pick hubs, after calculating the average distances for each node
        randomlyPickHubs();
        // 2- greedily assign non-hub nodes to hubs
        // 3- distribute non-hubs on the vehicles
        greedilyAssignNonHubsToVehicles();
        double cpu = (System.nanoTime() - startTime) / 1e9;
        pHCRP.setInitCPU(cpu);
    }

    /**
     * methods
     **/
    private void randomlyPickHubs() {
        for (int i = 0; i < numHubs; i++) {
            Random rand = new Random();
            int randomNode = rand.nextInt(numNodes); // random node index

            if (pHCRP.getIsVisitedCity()[randomNode]) {
                i--;
                continue;
            }

            pHCRP.getHubsArr()[i] = randomNode;
            pHCRP.getIsVisitedCity()[randomNode] = true;
        }
    }

    void greedyPickHubs() {
        LinkedHashMap<Integer, Integer> nodesDistanceAvg = new LinkedHashMap<>();
        for (int i = 0; i < numNodes; i++) {
            int sum = 0;
            for (int j = 0; j < numNodes; j++) {
                sum += pHCRP.getDistance(i, j);
            }
            nodesDistanceAvg.put(i, sum / numNodes);
        }

        nodesDistanceAvg = Utils.sortByValue(nodesDistanceAvg);

        int h = 0;
        for (Map.Entry<Integer, Integer> node : nodesDistanceAvg.entrySet()) {
            if (h >= numHubs) break;
            pHCRP.getHubsArr()[h] = node.getKey();
            pHCRP.getIsVisitedCity()[pHCRP.getHubsArr()[h]] = true;
            h++;
        }
    }

    private void randomlyAssignNonHubsToVehicles() {
        /* Assigning non-hub nodes to vehicles */
        // min and max number of nodes in a vehicle
        int minNumOfNodesInVehicle = 1;
        int maxNumOfNodesInVehicle = numNodes - (numHubs * (numVehiclesPerHub + 1)) + 1;
        int remainingNodes = numNodes - numHubs;

        // loop through vehicles lists
        for (int i = 0; i < pHCRP.getVehiclesList().size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = pHCRP.getVehiclesList().size() - i;

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
                    if (pHCRP.getIsVisitedCity()[randomNode]) {
                        j--;
                        continue;
                    }

                    pHCRP.getVehiclesList().get(i).add(j, randomNode);
                    pHCRP.getIsVisitedCity()[randomNode] = true;
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
        LinkedHashMap<Integer, Double> nodesToHubDistance = new LinkedHashMap<>();
        // loop through vehicles lists
        for (int i = 0; i < pHCRP.getVehiclesList().size(); i++) {
            // if it's a new hub
            if (i % numVehiclesPerHub == 0) {
                // create a hash map of the hub-to-node distances
                // current hub
                int currentHub = i / numVehiclesPerHub;
                for (int node = 0; node < numNodes; node++) {
                    // loop through node and get distances to the current hub
                    // only if the node is non-hub
                    if (!pHCRP.getIsVisitedCity()[node])
                        nodesToHubDistance.put(node, pHCRP.getDistance(pHCRP.getHubsArr()[currentHub], node));
                }
                nodesToHubDistance = Utils.sortByValue(nodesToHubDistance);
            }

            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = pHCRP.getVehiclesList().size() - i;

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
                    if (pHCRP.getIsVisitedCity()[closestNode]) {
                        j--;
                        continue;
                    }

                    pHCRP.getVehiclesList().get(i).add(j, closestNode);
                    pHCRP.getIsVisitedCity()[closestNode] = true;
                    // remove the visited node, because already added to a hub
                    nodesToHubDistance.remove(closestNode);
                }

            } else {
                i--;
            }
        }
    }

//    #5 PROP

    /**
     * generate a probabilistic initial solution
     * pick hubs using roulette wheel
     **/
    private void probabilisticInitSol() {
        long startTime = System.nanoTime();
        ArrayList<Integer> nodesDistancesSum = new ArrayList<>(numNodes);
        int totalSum = 0;
        // timeSum nodes distances and the total of all the summations
        for (int i = 0; i < numNodes; i++) {
            int sum = 0;
            for (int j = 0; j < numNodes; j++) {
                sum += pHCRP.getDistance(i, j);
            }
            nodesDistancesSum.add(sum);
            totalSum += sum;
        }

        LinkedHashMap<Integer, Double> normalizedSums = new LinkedHashMap<>();
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

        pHCRP.setHubsArr(hubsArr);
        pHCRP.setIsVisitedCity(isVisitedCity);
        assignNodesRouletteWheel(isVisitedCity);
        double cpu = (System.nanoTime() - startTime) / 1e9;
        pHCRP.setInitCPU(cpu);
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
        LinkedHashMap<Integer, Double> normalizedDistances = new LinkedHashMap<>();
        // loop through vehicles lists
        for (int i = 0; i < pHCRP.getVehiclesList().size(); i++) {
            Random random = new Random();
            int numOfNodesForVehicle = random.nextInt(maxNumOfNodesInVehicle) + minNumOfNodesInVehicle;
            int remainingVehicles = pHCRP.getVehiclesList().size() - i;

            // current hub
            int currentHub = i / numVehiclesPerHub;

            int distancesSum = 0;
            for (int node = 0; node < numNodes; node++) {
                // distancesSum hub to node distances
                distancesSum += pHCRP.getDistance(currentHub, node);
            }

            // if it's a new hub
            if (i % numVehiclesPerHub == 0) {
                // create a hash map of the hub-to-node distances
                for (int node = 0; node < numNodes; node++) {
                    // loop through node and get distances to the current hub
                    // only if the node is non-hub
                    if (!isVisitedCity[node]) {
                        double prob = (double) pHCRP.getDistance(pHCRP.getHubsArr()[currentHub], node) / distancesSum;
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
                        if (x == normalizedDistances.size() - 2 && n.getValue() == -1.0) {
//                            System.out.println("randomProb " + randomProb + " nodeProb " + n.getValue() +
//                                    " node " + n.getKey() + " totalSum " + distancesSum);
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

                            pHCRP.getVehiclesList().get(i).add(j, node);
                            isVisitedCity[node] = true;
                            int oldDistancesSum = distancesSum;
                            distancesSum -= (distancesSum * n.getValue());
                            n.setValue(-1.0);
                            // remove the visited node, because already added to a hub
                            normalizedDistances.remove(node);

                            // reset probabilities after changing the totalSum => distancesSum/new totalSum
                            for (Map.Entry<Integer, Double> nn : normalizedDistances.entrySet()) {
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

    //    #6 GRB & #7 GREEDY_GRB
    void gurobiSolution(IS initSol) {
        final String jsonPath = "results" + File.separator;

        int[] hubsArr = new int[numHubs];
        JSONParser parser = new JSONParser();
        String jsonPrefix = initSol == IS.GRB ? "GRB" : "HUBS_GRB";


        try {
            String json = Files.readString(Paths.get(jsonPath + jsonPrefix +
                    "_" + dataset.toString() + "_" + numNodes + "_" + numHubs + "_" + numVehiclesPerHub + ".json"));
            Reader reader = new StringReader(json);
            JSONObject a = (JSONObject) parser.parse(reader);
            double cpu = (Double) a.get("CPU");
            pHCRP.setInitCPU(cpu);
            JSONArray routesJson = (JSONArray) a.get("routes");
            int i = 0;
            int h = 0;
            for (Object routeObj : routesJson) {
                int j = 0;
                String[] route = routeObj.toString().split(",");
                ArrayList<Integer> r = new ArrayList<>();
                for (String nodeObj : route) {
                    if (j < route.length - 1) {
                        int node = Math.toIntExact((long) Long.valueOf(nodeObj));
                        if (j == 0 && i % numVehiclesPerHub == 0) {
                            hubsArr[h] = node;
                            h++;
                        } else if (j != 0) {
                            r.add(node);
                        }
                    }
                    j++;
                }
                pHCRP.setRouteInVehiclesList(i, r);
                i++;
            }
            pHCRP.setHubsArr(hubsArr);
            reader.close();
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        System.out.println("hubs:");
//        System.out.print(Arrays.toString(pHCRP.getHubsArr()));

//        for (List<Integer> route :
//                pHCRP.getVehiclesList()) {
//            System.out.print(route);
//            System.out.println();
//        }
    }
}
