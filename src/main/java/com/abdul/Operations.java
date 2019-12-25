package com.abdul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Operations {

    private PHMLRP phmlrp;

    Operations(PHMLRP phmlrp) {
        this.phmlrp = phmlrp;
    }

    boolean insertNodeInRoute(boolean isSimulatedAnnealing, int routeIdx, int nodeIdx, int newIdx) {
        boolean thereIsValidRoute = false;
        for (List<Integer> route :
                phmlrp.getVehiclesList()) {
            if (route.size() > 2) {
                thereIsValidRoute = true;
                break;
            }
        }
        if (!thereIsValidRoute) return false;

        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking a route randomly if the operation not called from insertion local search
        if (routeIdx == -1) {
            routeIdx = random.nextInt(phmlrp.getVehiclesList().size());
            // if number of nodes in this random route is less than 3, re-pick.
            while (phmlrp.getVehiclesList().get(routeIdx).size() < 3) {
                routeIdx = random.nextInt(phmlrp.getVehiclesList().size());
            }
        }

        // the two random indices from the random route if the operation not called from insertion local search
        if (nodeIdx == -1) {
            nodeIdx = random.nextInt(phmlrp.getVehiclesList().get(routeIdx).size());
            newIdx = random.nextInt(phmlrp.getVehiclesList().get(routeIdx).size());
            while (nodeIdx == newIdx) {
                // while the two randomly selected indices are the same, regenerate another one
                newIdx = random.nextInt(phmlrp.getVehiclesList().get(routeIdx).size());
            }
        }

        // removing the node from its index then add it at the new one
        int removedNode = phmlrp.getVehiclesList().get(routeIdx).remove(nodeIdx);
        phmlrp.getVehiclesList().get(routeIdx).add(newIdx, removedNode);

//        System.out.println("randomRouteIdx: " + randomRouteIdx +
//                " randomNodeIdx: " + randomNodeIdx +
//                " randomNewIdx: " + randomNewIdx);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the node from new index then to add it into its original index
            removedNode = phmlrp.getVehiclesList().get(routeIdx).remove(newIdx);
            phmlrp.getVehiclesList().get(routeIdx).add(nodeIdx, removedNode);

            return false;
        }

        return true;
    }

    boolean insertNodeBetweenRoutes(boolean isSimulatedAnnealing, int routeIdx1, int routeIdx2, int nodeIdx, int newIdx) {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return false;

        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        if (routeIdx1 == -1) {
            // picking two routes randomly if the operation is not called from insertion local search
            routeIdx1 = random.nextInt(phmlrp.getVehiclesList().size());
            routeIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
            while (routeIdx1 == routeIdx2) {
                // while the two randomly selected routes are the same, re-pick another one
                routeIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
            }
        }

        // if number of nodes in the first route is less than 2, we cannot remove any node.
        if (phmlrp.getVehiclesList().get(routeIdx1).size() < 2) return false;

        if (nodeIdx == -1) {
            // if the operation is not called from insertion local search,
            // pick two random indices, one from each route
            nodeIdx = random.nextInt(phmlrp.getVehiclesList().get(routeIdx1).size());
            newIdx = random.nextInt(phmlrp.getVehiclesList().get(routeIdx2).size());
        }

        // removing the node from the first route then add it to the new one
        int removedNode = phmlrp.getVehiclesList().get(routeIdx1).remove(nodeIdx);
        phmlrp.getVehiclesList().get(routeIdx2).add(newIdx, removedNode);

//        System.out.println(" vehicle1: " + randomRouteIdx1 + " nodeIndex: " + randomNodeIdx +
//                " vehicle2: " + randomRouteIdx2 + " newIndex: " + randomNewIdx);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the node from new index then to add it into its original index
            removedNode = phmlrp.getVehiclesList().get(routeIdx2).remove(newIdx);
            phmlrp.getVehiclesList().get(routeIdx1).add(nodeIdx, removedNode);
            return false;
        }

        return true;
    }

    boolean swapNodeInRoute(boolean isSimulatedAnnealing, int randomRouteIdx, int randomNodeIdx1, int randomNodeIdx2) {
        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        if (randomRouteIdx == -1) {
            // picking a route randomly, if not called from swap local search operation
            randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
        }
        // if number of nodes in the route is less than 3, return.
        if (phmlrp.getVehiclesList().get(randomRouteIdx).size() < 3) return false;
        if (randomNodeIdx1 == -1) {
            // the two random nodes indices from the random route, if not called from swap local search operation
            randomNodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());
            randomNodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());

            while (randomNodeIdx1 == randomNodeIdx2) {
                // while the two randomly selected indices are the same, regenerate another one
                randomNodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());
            }
        }
        // swapping the two nodes
        int temp = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1);
        phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1,
                phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2));
        phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2, temp);

//        System.out.println("randomRouteIdx: " + randomRouteIdx +
//                " randomNodeIdx1: " + randomNodeIdx1 +
//                " randomNodeIdx2: " + randomNodeIdx2);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the two nodes
            temp = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1);
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1,
                    phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2));
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2, temp);
            return false;
        }

        return true;
    }

    boolean swapNodeWithinRoutes(boolean isSimulatedAnnealing, int randomRouteIdx1, int randomRouteIdx2, int randomNodeIdx1, int randomNodeIdx2) {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return false;

        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        if (randomRouteIdx1 == -1) {
            // picking two route randomly, if not called from swap local search operation
            randomRouteIdx1 = random.nextInt(phmlrp.getVehiclesList().size());
            randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
            while (randomRouteIdx1 == randomRouteIdx2) {
                // while the two randomly selected routes are the same, re-pick another one
                randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
            }

            // the two random indices, one from each route, if not called from swap local search operation
            randomNodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx1).size());
            randomNodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx2).size());
        }
        // swapping the two nodes
        int temp = phmlrp.getVehiclesList().get(randomRouteIdx1).get(randomNodeIdx1);
        phmlrp.getVehiclesList().get(randomRouteIdx1).set(randomNodeIdx1,
                phmlrp.getVehiclesList().get(randomRouteIdx2).get(randomNodeIdx2));
        phmlrp.getVehiclesList().get(randomRouteIdx2).set(randomNodeIdx2, temp);

//        System.out.println(" vehicle1: " + randomRouteIdx1 + " nodeIndex: " + randomNodeIdx1 +
//                " vehicle2: " + randomRouteIdx2 + " newIndex: " + randomNodeIdx2);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the two nodes
            temp = phmlrp.getVehiclesList().get(randomRouteIdx1).get(randomNodeIdx1);
            phmlrp.getVehiclesList().get(randomRouteIdx1).set(randomNodeIdx1,
                    phmlrp.getVehiclesList().get(randomRouteIdx2).get(randomNodeIdx2));
            phmlrp.getVehiclesList().get(randomRouteIdx2).set(randomNodeIdx2, temp);
            return false;
        }

        return true;
    }

    void edgeOpt(boolean isSimulatedAnnealing) {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return;

        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking two routes randomly
        int randomRouteIdx1 = random.nextInt(phmlrp.getVehiclesList().size());
        int randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());

        // if number of nodes in the first route is less than 3, we cannot remove any edge.
        if (phmlrp.getVehiclesList().get(randomRouteIdx1).size() < 3) return;

        while (randomRouteIdx1 == randomRouteIdx2) {
            // while the two randomly selected routes are the same, re-pick another one
            randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
        }

        // the two random indices, one from each route
        int randomNodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx1).size() - 1);
        int randomNewIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx2).size());

        // removing the node from the first route then add it to the new one
        int removedNode1 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
        int removedNode2 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
        phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNewIdx, removedNode1);
        phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNewIdx + 1, removedNode2);

//        System.out.println(" route1: " + randomRouteIdx1 + " nodeIndex: " + randomNodeIdx1 +
//                " route2: " + randomRouteIdx2 + " newIndex: " + randomNewIdx);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the node from new index then to add it into its original index
            removedNode1 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNewIdx);
            removedNode2 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNewIdx);
            phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1, removedNode1);
            phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1 + 1, removedNode2);
        }
    }

    void swapHubWithNode(boolean isSimulatedAnnealing) {
        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // TODO: Ask?? should the non-hub node be related to the randomly selected hub?
        // pick a random hub and a non-hub node in a random route
        int randomHubIdx = random.nextInt(phmlrp.getHubsArr().length);
        int randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
        int randomNodeIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());

        // swapping the hub with the node
        int temp = phmlrp.getHubsArr()[randomHubIdx];
        phmlrp.getHubsArr()[randomHubIdx] = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx);
        phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx, temp);

//        System.out.println(" hubIndex: " + randomHubIdx + " route: " + randomRouteIdx + " newIndex: " + randomNodeIdx);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the hub with the node
            temp = phmlrp.getHubsArr()[randomHubIdx];
            phmlrp.getHubsArr()[randomHubIdx] = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx);
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx, temp);
        }
    }

    void twoOptAlgorithm() {
        // TODO: Ask?? Do we include the hub in the route for 2Opt algorithm?
        //  Should we compare the best cost of the current route only or for the maxCost
        Random random = new Random();
        int randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
        List<Integer> bestRoute = phmlrp.getVehiclesList().get(randomRouteIdx);
        if (bestRoute.size() < 2) return;
        int hubIdx = randomRouteIdx / phmlrp.getNumVehiclesPerHub();
        int hub = phmlrp.getHubsArr()[hubIdx];
        bestRoute.add(0, hub);
        int n = bestRoute.size();

        int bestCost = calculateRouteCost(bestRoute);
//        System.out.println("2Opt Hub: " + hub + " Route: " + randomRouteIdx + " First cost: " + bestCost);

        for (int i = 0; i < n - 1; i++) {
            System.out.println(bestRoute.get(i));
            for (int j = i + 1; j < n; j++) {
                List<Integer> newRoute = new ArrayList<Integer>(bestRoute);
                Collections.reverse(newRoute.subList(i, j + 1));
                int newCost = calculateRouteCost(newRoute);

                if (newCost < bestCost) {
                    bestRoute.clear();
                    bestRoute.addAll(newRoute);
                    bestCost = newCost;
                }
            }
        }

//        System.out.println("2Opt Best cost: " + bestCost);

        phmlrp.getHubsArr()[hubIdx] = bestRoute.remove(0);
        phmlrp.getVehiclesList().set(randomRouteIdx, bestRoute);

        phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
    }

    void insertionLocalSearch() {
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                // going through each node recursively,
                // then inserting the current node in every possible index and calculating cost each time
                insertAfterEachNode(i, j);
            }
        }
    }

    void swapLocalSearch() {
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                // going through each node recursively,
                // then swapping the current node with every other node and calculating cost each time
                swapWithEachNode(i, j);
            }
        }
    }

    void insertTwoNodes(boolean isSimulatedAnnealing) {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return;

        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking two routes randomly
        int routeIdx1 = random.nextInt(phmlrp.getVehiclesList().size());

        // if number of nodes in the first route is less than 3, we cannot remove two nodes.
        if (phmlrp.getVehiclesList().get(routeIdx1).size() < 3) return;

        int routeIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
        while (routeIdx1 == routeIdx2) {
            // while the two randomly selected routes are the same, re-pick another one
            routeIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
        }

        // pick two random indices, one from each route
        int nodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(routeIdx1).size());
        int newIdx1 = random.nextInt(phmlrp.getVehiclesList().get(routeIdx2).size());
        // removing the first node from the first route then adding it to the new one
        int removedNode1 = phmlrp.getVehiclesList().get(routeIdx1).remove(nodeIdx1);
        phmlrp.getVehiclesList().get(routeIdx2).add(newIdx1, removedNode1);

        // again, pick two random indices, one from each route
        int nodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(routeIdx1).size());
        int newIdx2 = random.nextInt(phmlrp.getVehiclesList().get(routeIdx2).size());
        // removing the second node from the first route then adding it to the new one
        int removedNode2 = phmlrp.getVehiclesList().get(routeIdx1).remove(nodeIdx2);
        phmlrp.getVehiclesList().get(routeIdx2).add(newIdx2, removedNode2);

//        System.out.println(" vehicle1: " + routeIdx1 + " nodeIndex1: " + nodeIdx1 + " nodeIndex2: " + nodeIdx2
//                + "\nvehicle2: " + routeIdx2 + " newIndex1: " + newIdx1 + " newIdx2: " + newIdx2);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the two nodes from new indices then to add them into their original indices
            // adding the second node then the first one (opposite to the previous one)
            removedNode2 = phmlrp.getVehiclesList().get(routeIdx2).remove(newIdx2);
            phmlrp.getVehiclesList().get(routeIdx1).add(nodeIdx2, removedNode2);
            removedNode1 = phmlrp.getVehiclesList().get(routeIdx2).remove(newIdx1);
            phmlrp.getVehiclesList().get(routeIdx1).add(nodeIdx1, removedNode1);
        }
    }

    void nodesRemoveAndGreedyInsert(float removalPercentage) {
        int allowedNumNodesToRemove = phmlrp.getNumNodes() -
                phmlrp.getHubsArr().length - phmlrp.getVehiclesList().size();
        int numNodesToRemove = Math.round(phmlrp.getNumNodes() * removalPercentage);
//        System.out.println("allowedNumNodesToRemove: " + allowedNumNodesToRemove
//                + " numNodesToRemove: " + numNodesToRemove);
        if (numNodesToRemove > allowedNumNodesToRemove) numNodesToRemove = allowedNumNodesToRemove;

        int[] removedNodes = new int[numNodesToRemove];
        removeNodes(numNodesToRemove, removedNodes);

        int maxCost = phmlrp.getMaxCost();

        for (int i = 0; i < removedNodes.length; i++) {
            insertRemovedNode(maxCost, removedNodes[i]);
        }
    }

    private void removeNodes(int numNodesToRemove, int[] removedNodes) {
        Random random = new Random();

        for (int i = 0; i < numNodesToRemove; i++) {
            int randomRoute = random.nextInt(phmlrp.getVehiclesList().size());
            int routeSize = phmlrp.getVehiclesList().get(randomRoute).size();
            if (routeSize < 2) {
                i--;
                continue;
            }
            int randomNode = random.nextInt(routeSize);
            removedNodes[i] = phmlrp.getVehiclesList().get(randomRoute).remove(randomNode);
//            System.out.println("randomRoute: " + randomRoute + " randomNode: " + randomNode);
        }
    }

    private void insertAfterEachNode(int routeIdx, int nodeIdx) {
        int counter = 0;
        int bCounter = 0;
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                if (routeIdx == i && nodeIdx == j) continue;
                // insert the current node before each node
                if (routeIdx == i) {
                    if (insertNodeInRoute(false, routeIdx, nodeIdx, j)) counter++;
                } else {
                    if (insertNodeBetweenRoutes(false, routeIdx, i, nodeIdx, j)) bCounter++;
                }
            }
        }
//        System.out.println("Route: " + routeIdx + " Node: " + nodeIdx + " Counter: " + counter + " bCounter: " + bCounter);
    }

    private void insertRemovedNode(int originalMaxCost, int node) {
        phmlrp.resetMaxCost(originalMaxCost);

        int counter = 0;
        int bestCost = originalMaxCost;
        int routeIdx = 0;
        int index = 0;

        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            int newCost = insertNode(i, node, 0);
            if (newCost < bestCost) {
                bestCost = newCost;
                routeIdx = i;
                index = 0;
                counter++;
            }

            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                // insert the current node before each node
                newCost = insertNode(i, node, j + 1);
                if (newCost < bestCost) {
                    bestCost = newCost;
                    routeIdx = i;
                    index = j + 1;
                    counter++;
                }
            }
        }

//        System.out.println("Node: " + node + " Counter: " + counter);
        // adding the node at the index
        phmlrp.getVehiclesList().get(routeIdx).add(index, node);
    }

    private int insertNode(int routeIdx, int node, int index) {
        // adding the node at the index
        phmlrp.getVehiclesList().get(routeIdx).add(index, node);
        // get the new cost after the change
        int newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);

        phmlrp.print(false);

        // if the new cost is greater than or equal to the former cost,
        // remove the node from the index
        phmlrp.getVehiclesList().get(routeIdx).remove(index);
        return newCost;
    }

    private void swapWithEachNode(int routeIdx, int nodeIdx) {
        int counter = 0;
        int bCounter = 0;
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                if (routeIdx == i && nodeIdx == j) continue;
                // insert the current node before each node
                if (routeIdx == i) {
                    if (swapNodeInRoute(false, routeIdx, nodeIdx, j)) counter++;
                } else {
                    if (swapNodeWithinRoutes(false, routeIdx, i, nodeIdx, j)) bCounter++;
                }
            }
        }
//        System.out.println("Route: " + routeIdx + " Node: " + nodeIdx + " Counter: " + counter + " bCounter: " + bCounter);
    }

    private int calculateRouteCost(List<Integer> bestRoute) {
        int cost = 0;
        // loop on a vehicle's list and calculating the whole cost
        for (int i = 0; i < bestRoute.size() - 1; i++) {
            cost += phmlrp.getDistance(bestRoute.get(i), bestRoute.get(i + 1));
        }
        cost += phmlrp.getDistance(bestRoute.get(bestRoute.size() - 1), bestRoute.get(0));
        return cost;
    }
}
