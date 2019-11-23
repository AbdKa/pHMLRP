package com.abdul;

import java.util.*;

class Operations {

    PHMLRP phmlrp;

    Operations(PHMLRP phmlrp) {
        this.phmlrp = phmlrp;
    }

    void insertNodeInRoute() {
        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking a route randomly
        int randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
        // if number of nodes in this random route is less than 3, return.
        if (phmlrp.getVehiclesList().get(randomRouteIdx).size() < 3) return;

        // the two random indices from the random route
        int randomNodeIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());
        int randomNewIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());

        while (randomNodeIdx == randomNewIdx) {
            // while the two randomly selected indices are the same, regenerate another one
            randomNewIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());
        }

        // removing the node from its index then add it at the new one
        int removedNode = phmlrp.getVehiclesList().get(randomRouteIdx).remove(randomNodeIdx);
        phmlrp.getVehiclesList().get(randomRouteIdx).add(randomNewIdx, removedNode);

//        System.out.println("randomRouteIdx: " + randomRouteIdx +
//                " randomNodeIdx: " + randomNodeIdx +
//                " randomNewIdx: " + randomNewIdx);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(true);
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the node from new index then to add it into its original index
            removedNode = phmlrp.getVehiclesList().get(randomRouteIdx).remove(randomNewIdx);
            phmlrp.getVehiclesList().get(randomRouteIdx).add(randomNodeIdx, removedNode);
        }
    }

    void insertNodeBetweenRoutes() {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return;

        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking two routes randomly
        int randomRouteIdx1 = random.nextInt(phmlrp.getVehiclesList().size());
        int randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());

        // if number of nodes in the first route is less than 2, we cannot remove any node.
        if (phmlrp.getVehiclesList().get(randomRouteIdx1).size() < 2) return;

        while (randomRouteIdx1 == randomRouteIdx2) {
            // while the two randomly selected routes are the same, re-pick another one
            randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
        }

        // the two random indices, one from each route
        int randomNodeIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx1).size());
        int randomNewIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx2).size());

        // removing the node from the first route then add it to the new one
        int removedNode = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx);
        phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNewIdx, removedNode);

//        System.out.println(" vehicle1: " + randomRouteIdx1 + " nodeIndex: " + randomNodeIdx +
//                " vehicle2: " + randomRouteIdx2 + " newIndex: " + randomNewIdx);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(true);
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the node from new index then to add it into its original index
            removedNode = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNewIdx);
            phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx, removedNode);
        }
    }

    void swapNodeInRoute() {
        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking a route randomly
        int randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
        // if number of nodes in the route is less than 3, return.
        if (phmlrp.getVehiclesList().get(randomRouteIdx).size() < 3) return;
        // the two random nodes indices from the random route
        int randomNodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());
        int randomNodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());

        while (randomNodeIdx1 == randomNodeIdx2) {
            // while the two randomly selected indices are the same, regenerate another one
            randomNodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size());
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
        int newCost = phmlrp.calculateCost(true);
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the two nodes
            temp = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1);
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1,
                    phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2));
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2, temp);
        }
    }

    void swapNodeWithinRoutes() {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return;

        int currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking two route randomly
        int randomRouteIdx1 = random.nextInt(phmlrp.getVehiclesList().size());
        int randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
        while (randomRouteIdx1 == randomRouteIdx2) {
            // while the two randomly selected routes are the same, re-pick another one
            randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
        }
        // the two random indices, one from each route
        int randomNodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx1).size());
        int randomNodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx2).size());
        // swapping the two nodes
        int temp = phmlrp.getVehiclesList().get(randomRouteIdx1).get(randomNodeIdx1);
        phmlrp.getVehiclesList().get(randomRouteIdx1).set(randomNodeIdx1,
                phmlrp.getVehiclesList().get(randomRouteIdx2).get(randomNodeIdx2));
        phmlrp.getVehiclesList().get(randomRouteIdx2).set(randomNodeIdx2, temp);

        System.out.println(" vehicle1: " + randomRouteIdx1 + " nodeIndex: " + randomNodeIdx1 +
                " vehicle2: " + randomRouteIdx2 + " newIndex: " + randomNodeIdx2);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(true);
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the two nodes
            temp = phmlrp.getVehiclesList().get(randomRouteIdx1).get(randomNodeIdx1);
            phmlrp.getVehiclesList().get(randomRouteIdx1).set(randomNodeIdx1,
                    phmlrp.getVehiclesList().get(randomRouteIdx2).get(randomNodeIdx2));
            phmlrp.getVehiclesList().get(randomRouteIdx2).set(randomNodeIdx2, temp);
        }
    }

    void edgeOpt() {
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
        int randomNodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx1).size()-1);
        int randomNewIdx = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx2).size());

        // removing the node from the first route then add it to the new one
        int removedNode1 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
        int removedNode2 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
        phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNewIdx, removedNode1);
        phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNewIdx+1, removedNode2);

//        System.out.println(" route1: " + randomRouteIdx1 + " nodeIndex: " + randomNodeIdx1 +
//                " route2: " + randomRouteIdx2 + " newIndex: " + randomNewIdx);

        // get the new cost after the change
        int newCost = phmlrp.calculateCost(true);
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the node from new index then to add it into its original index
            removedNode1 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNewIdx);
            removedNode2 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNewIdx);
            phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1, removedNode1);
            phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1+1, removedNode2);
        }
    }

    void swapHubWithNode() {
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
        int newCost = phmlrp.calculateCost(true);
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
        int hubIdx = randomRouteIdx/phmlrp.getNumVehiclesPerHub();
        int hub = phmlrp.getHubsArr()[hubIdx];
        bestRoute.add(0, hub);
        int n = bestRoute.size();

        int bestCost = calculateRouteCost(bestRoute);
        System.out.println("2Opt Hub: " + hub + " Route: " + randomRouteIdx + " First cost: " + bestCost);

        for (int i = 0; i < n-1; i++) {
            System.out.println(bestRoute.get(i));
            for (int j = i+1; j < n; j++) {
                List<Integer> newRoute = new ArrayList<Integer>(bestRoute);
                Collections.reverse(newRoute.subList(i, j+1));
                int newCost = calculateRouteCost(newRoute);

                if (newCost < bestCost) {
                    bestRoute.clear();
                    bestRoute.addAll(newRoute);
                    bestCost = newCost;
                }
            }
        }

        System.out.println("2Opt Best cost: " + bestCost);

        phmlrp.getHubsArr()[hubIdx] = bestRoute.remove(0);
        phmlrp.getVehiclesList().set(randomRouteIdx, bestRoute);

        phmlrp.calculateCost(true);
    }

    void insertLocalSearch() {
        
    }

    private int calculateRouteCost(List<Integer> bestRoute) {
        int cost = 0;
        // loop on a vehicle's list and calculating the whole cost
        for (int i = 0; i < bestRoute.size()-1; i++) {
            cost += phmlrp.getDistance(bestRoute.get(i), bestRoute.get(i + 1));
        }
        cost += phmlrp.getDistance(bestRoute.get(bestRoute.size()-1), bestRoute.get(0));
        return cost;
    }
}
