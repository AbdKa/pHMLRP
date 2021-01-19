package com.abdul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Operations {
    private final PHMLRP phmlrp;

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

        double currentCost = phmlrp.getMaxCost();

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
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
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

        double currentCost = phmlrp.getMaxCost();

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
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
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
        if (randomRouteIdx == -1) {
            boolean thereIsValidRoute = false;
            for (List<Integer> route :
                    phmlrp.getVehiclesList()) {
                if (route.size() > 1) {
                    thereIsValidRoute = true;
                    break;
                }
            }
            if (!thereIsValidRoute) return false;
        }
        double currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        if (randomRouteIdx == -1) {
            // picking a route randomly, if not called from swap local search operation
            randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
            // if number of nodes in this random route is less than 3, re-pick.
            while (phmlrp.getVehiclesList().get(randomRouteIdx).size() < 2) {
                randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
            }

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
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the two nodes
            temp = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1);
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1,
                    phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2));
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2, temp);
            phmlrp.setMaxCost(currentCost);
            return false;
        }

        return true;
    }

    boolean swapNodeWithinRoutes(boolean isSimulatedAnnealing, int randomRouteIdx1, int randomRouteIdx2,
                                 int randomNodeIdx1, int randomNodeIdx2) {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return false;

        double currentCost = phmlrp.getMaxCost();

        if (randomRouteIdx1 == -1) {
            // picking two route randomly, if not called from swap local search operation
            Random random = new Random();
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
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the two nodes
            temp = phmlrp.getVehiclesList().get(randomRouteIdx1).get(randomNodeIdx1);
            phmlrp.getVehiclesList().get(randomRouteIdx1).set(randomNodeIdx1,
                    phmlrp.getVehiclesList().get(randomRouteIdx2).get(randomNodeIdx2));
            phmlrp.getVehiclesList().get(randomRouteIdx2).set(randomNodeIdx2, temp);
            phmlrp.setMaxCost(currentCost);
            return false;
        }

        return true;
    }

    boolean edgeOptInRoute(boolean isSimulatedAnnealing, int randomRouteIdx, int randomNodeIdx1, int randomNodeIdx2) {
        if (randomRouteIdx == -1) {
            boolean thereIsValidRoute = false;
            for (List<Integer> route :
                    phmlrp.getVehiclesList()) {
                if (route.size() > 3) {
                    thereIsValidRoute = true;
                    break;
                }
            }
            if (!thereIsValidRoute) return false;
        }
        double currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        if (randomRouteIdx == -1) {
            // picking a route randomly, if not passed from edgeOpt local search operation
            randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
            // if number of nodes in this random route is less than 3, re-pick.
            while (phmlrp.getVehiclesList().get(randomRouteIdx).size() < 4) {
                randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
            }

            // the two random nodes indices from the random route, if not passed from edgeOpt local search operation
            randomNodeIdx1 = random.nextInt((phmlrp.getVehiclesList().get(randomRouteIdx).size()) / 2) * 2;
//                    random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx).size() - 1);
            randomNodeIdx2 = random.nextInt((phmlrp.getVehiclesList().get(randomRouteIdx).size()) / 2) * 2;

            while (Math.abs(randomNodeIdx1 - randomNodeIdx2) < 2) {
                // while the two randomly selected indices are the same, regenerate another one
                randomNodeIdx2 = random.nextInt((phmlrp.getVehiclesList().get(randomRouteIdx).size()) / 2) * 2;
            }
        }

//        System.out.println("randomRouteIdx: " + randomRouteIdx +
//                " randomNodeIdx1: " + randomNodeIdx1 +
//                " randomNodeIdx2: " + randomNodeIdx2);
//        phmlrp.print(false);

        // swapping the two edges
        int temp1 = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1);
        int temp2 = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1 + 1);
        phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1,
                phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2));
        phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2, temp1);
        phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1 + 1,
                phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2 + 1));
        phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2 + 1, temp2);

        // get the new cost after the change
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the two nodes
            temp1 = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1);
            temp2 = phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx1 + 1);
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1,
                    phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2));
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2, temp1);
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx1 + 1,
                    phmlrp.getVehiclesList().get(randomRouteIdx).get(randomNodeIdx2 + 1));
            phmlrp.getVehiclesList().get(randomRouteIdx).set(randomNodeIdx2 + 1, temp2);
            phmlrp.setMaxCost(currentCost);
            return false;
        }

        return true;
    }

    boolean edgeOptWithinRoutes(boolean isSimulatedAnnealing, int randomRouteIdx1, int randomRouteIdx2,
                                int randomNodeIdx1, int randomNodeIdx2) {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return false;

        double currentCost = phmlrp.getMaxCost();

        if (randomRouteIdx1 == -1) {
            int validRoutes = 0;
            for (List<Integer> route :
                    phmlrp.getVehiclesList()) {
                if (route.size() > 1) {
                    validRoutes++;
                }
            }
            if (validRoutes < 2) return false;

            Random random = new Random();
            // picking two routes randomly
            randomRouteIdx1 = random.nextInt(phmlrp.getVehiclesList().size());
            // if number of nodes in this random route is less than 3, re-pick.
            while (phmlrp.getVehiclesList().get(randomRouteIdx1).size() < 2) {
                randomRouteIdx1 = random.nextInt(phmlrp.getVehiclesList().size());
            }

            randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());

            while (randomRouteIdx1 == randomRouteIdx2 || phmlrp.getVehiclesList().get(randomRouteIdx2).size() < 2) {
                // while the two randomly selected routes are the same, re-pick another one
                randomRouteIdx2 = random.nextInt(phmlrp.getVehiclesList().size());
            }

            // the two random indices, one from each route
            randomNodeIdx1 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx1).size() - 1);
            randomNodeIdx2 = random.nextInt(phmlrp.getVehiclesList().get(randomRouteIdx2).size() - 1);
        }

//        System.out.println(" route1: " + randomRouteIdx1 + " randomNodeIdx: " + randomNodeIdx1 +
//                " route2: " + randomRouteIdx2 + " randomNodeIdx: " + randomNodeIdx2);

        // swap the two edges in the first and second route
        int removedR1N1 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
        int removedR1N2 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
        int removedR2N1 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNodeIdx2);
        int removedR2N2 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNodeIdx2);
        phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1, removedR2N1);
        phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1 + 1, removedR2N2);
        phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNodeIdx2, removedR1N1);
        phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNodeIdx2 + 1, removedR1N2);

//        phmlrp.print(false);

        // get the new cost after the change
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the node from new index then to add it into its original index
            removedR1N1 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
            removedR1N2 = phmlrp.getVehiclesList().get(randomRouteIdx1).remove(randomNodeIdx1);
            removedR2N1 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNodeIdx2);
            removedR2N2 = phmlrp.getVehiclesList().get(randomRouteIdx2).remove(randomNodeIdx2);
            phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1, removedR2N1);
            phmlrp.getVehiclesList().get(randomRouteIdx1).add(randomNodeIdx1 + 1, removedR2N2);
            phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNodeIdx2, removedR1N1);
            phmlrp.getVehiclesList().get(randomRouteIdx2).add(randomNodeIdx2 + 1, removedR1N2);
            return false;
        }

        return true;
    }

    boolean swapHubWithNode(boolean isSimulatedAnnealing, int hubIdx, int routeIdx, int nodeIdx) {
        double currentCost = phmlrp.getMaxCost();

        if (hubIdx == -1) {
            Random random = new Random();
            // TODO: Ask?? should the non-hub node be related to the randomly selected hub?
            // pick a random hub and a non-hub node in a random route
            hubIdx = random.nextInt(phmlrp.getHubsArr().length);
            routeIdx = random.nextInt(phmlrp.getVehiclesList().size());
            nodeIdx = random.nextInt(phmlrp.getVehiclesList().get(routeIdx).size());
        }
        // swapping the hub with the node
        int temp = phmlrp.getHubsArr()[hubIdx];
        phmlrp.getHubsArr()[hubIdx] = phmlrp.getVehiclesList().get(routeIdx).get(nodeIdx);
        phmlrp.getVehiclesList().get(routeIdx).set(nodeIdx, temp);

//        System.out.println("hubIndex: " + hubIdx + " route: " + routeIdx + " newIndex: " + nodeIdx);

        // get the new cost after the change
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, re-swap the hub with the node
            temp = phmlrp.getHubsArr()[hubIdx];
            phmlrp.getHubsArr()[hubIdx] = phmlrp.getVehiclesList().get(routeIdx).get(nodeIdx);
            phmlrp.getVehiclesList().get(routeIdx).set(nodeIdx, temp);
            phmlrp.setMaxCost(currentCost);
            return false;
        }

        return true;
//        phmlrp.print(false);
    }

    boolean twoOptAlgorithm() {
        // TODO: Ask?? Do we include the hub in the route for 2Opt algorithm?
        //  Should we compare the best cost of the current route only or for the maxCost
        if (phmlrp.getNumNodes() <= (phmlrp.getNumHubs() * phmlrp.getNumVehiclesPerHub()) + phmlrp.getNumHubs()) {
            return false;
        }
        double currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        int randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
        List<Integer> bestRoute = phmlrp.getVehiclesList().get(randomRouteIdx);
        while (bestRoute.size() < 2) {
            randomRouteIdx = random.nextInt(phmlrp.getVehiclesList().size());
            bestRoute = phmlrp.getVehiclesList().get(randomRouteIdx);
        }
        int hubIdx = randomRouteIdx / phmlrp.getNumVehiclesPerHub();
        int hub = phmlrp.getHubsArr()[hubIdx];
        List<Integer> oldRoute = new ArrayList<>(bestRoute);
        bestRoute.add(0, hub);
        int n = bestRoute.size();

        int bestCost = calculateRouteCost(bestRoute);
//        System.out.println("2Opt Hub: " + hub + " Route: " + randomRouteIdx + " First cost: " + bestCost);

        for (int i = 0; i < n - 1; i++) {
//            System.out.println(bestRoute.get(i));
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

        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);

        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost, reset the hub and route
            phmlrp.getHubsArr()[hubIdx] = hub;
            phmlrp.getVehiclesList().set(randomRouteIdx, oldRoute);
            phmlrp.setMaxCost(currentCost);
            return false;
        }

        return true;
    }

    boolean insertTwoNodes(boolean isSimulatedAnnealing) {
        // if we have less than 2 routes, return.
        if (phmlrp.getVehiclesList().size() < 2) return false;

        double currentCost = phmlrp.getMaxCost();

        Random random = new Random();
        // picking two routes randomly
        int routeIdx1 = random.nextInt(phmlrp.getVehiclesList().size());

        // if number of nodes in the first route is less than 3, we cannot remove two nodes.
        if (phmlrp.getVehiclesList().get(routeIdx1).size() < 3) return false;

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
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);
        if (isSimulatedAnnealing) {
            return true;
        }
        if (newCost >= currentCost) {
            // if the new cost is greater than or equal to the former cost,
            // remove the two nodes from new indices then to add them into their original indices
            // adding the second node then the first one (opposite to the previous one)
            removedNode2 = phmlrp.getVehiclesList().get(routeIdx2).remove(newIdx2);
            phmlrp.getVehiclesList().get(routeIdx1).add(nodeIdx2, removedNode2);
            removedNode1 = phmlrp.getVehiclesList().get(routeIdx2).remove(newIdx1);
            phmlrp.getVehiclesList().get(routeIdx1).add(nodeIdx1, removedNode1);
            return false;
        }

        return true;
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

        double maxCost = phmlrp.getMaxCost();

        for (int removedNode : removedNodes) {
            insertRemovedNode(maxCost, removedNode);
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

    private void insertRemovedNode(double originalMaxCost, int node) {
        phmlrp.setMaxCost(originalMaxCost);

        int counter = 0;
        double bestCost = originalMaxCost;
        int routeIdx = 0;
        int index = 0;

        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            double newCost = insertNode(i, node, 0);
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

    private double insertNode(int routeIdx, int node, int index) {
        // adding the node at the index
        phmlrp.getVehiclesList().get(routeIdx).add(index, node);
        // get the new cost after the change
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.OPERATION);

//        phmlrp.print(false);

        // if the new cost is greater than or equal to the former cost,
        // remove the node from the index
        phmlrp.getVehiclesList().get(routeIdx).remove(index);
        return newCost;
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

    /**
     * localSearchInsertion start
     */
    void localSearchInsertion() {
        // create a list of all non-hub nodes
        List<Integer> initList = new ArrayList<>();
        for (List<Integer> list : phmlrp.getVehiclesList()) {
            List<Integer> innerList = new ArrayList<>(list);
            initList.addAll(innerList);
        }

        for (int i = 0; i < initList.size(); i++) {
            // going through each node recursively,
            // then inserting the current node in every possible index and calculating cost each time
            int[] routeAndNode = searchInMainList(initList.get(i));
            if (phmlrp.getVehiclesList().get(routeAndNode[0]).size() < 2) {
                int singleNodeRouteCount = 0;
                for (List<Integer> route : phmlrp.getVehiclesList()) {
                    // at least one route with single node will be found
                    if (route.size() < 2) singleNodeRouteCount++;
                }
                if (i < initList.size() - singleNodeRouteCount) {
                    initList.add(initList.remove(i));
                    i--;
                }
                continue;
            }
            phmlrp.getVehiclesList().get(routeAndNode[0]).remove(routeAndNode[1]);
            insertAfterEachNode(initList.get(i), routeAndNode);

//            localSearchSwap();
//            localSearchSwapHubWithNode();
//            phmlrp.print(false);
        }
    }

    private int[] searchInMainList(int node) {
        int route = 0, nodeIdx = 0;
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                if (phmlrp.getVehiclesList().get(i).get(j) == node) {
                    route = i;
                    nodeIdx = j;
                }
            }
        }
        return new int[]{route, nodeIdx};
    }

    private void insertAfterEachNode(int node, int[] routeAndNode) {
        int bestRoute = routeAndNode[0];
        int bestIdx = routeAndNode[1];
//        Random random = new Random();
//        int bestRoute = random.nextInt(phmlrp.getVehiclesList().size());
//        int bestIdx = random.nextInt(phmlrp.getVehiclesList().get(bestRoute).size());
        double bestCost = phmlrp.getMaxCost();
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            for (int j = 0; j <= phmlrp.getVehiclesList().get(i).size(); j++) {
                // insert the current node before each node
                double cost = insertNodeLocalSearch(i, node, j);
//                System.out.println("route " + i + " idx " + j + " " + cost);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestRoute = i;
                    bestIdx = j;
                    System.out.println(phmlrp.getMaxCost());
                    phmlrp.print(false);
                }
            }
        }
        phmlrp.getVehiclesList().get(bestRoute).add(bestIdx, node);
        phmlrp.setMaxCost(bestCost);

//        System.out.println("Node: " + node);
    }

    private double insertNodeLocalSearch(int routeIdx, int node, int newIdx) {
        // add the node at the new one
        phmlrp.getVehiclesList().get(routeIdx).add(newIdx, node);
        // get the new cost after the change
        double newCost = phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
        // remove the node again
        phmlrp.getVehiclesList().get(routeIdx).remove(newIdx);
        return newCost;
    }
    /**
     * localSearchInsertion end
     * */

    /**
     * localSearchSwap start
     */
    void localSearchSwap() {
        // create a list of all non-hub nodes
        List<Integer> initList = new ArrayList<>();
        for (List<Integer> list : phmlrp.getVehiclesList()) {
            List<Integer> innerList = new ArrayList<>(list);
            initList.addAll(innerList);
        }

        for (int i = 0; i < initList.size(); i++) {
            // going through each node recursively,
            // then swapping the current node with every other node and calculating cost each time
            int[] routeAndNode = searchInMainList(initList.get(i));
            swapWithEachNode(routeAndNode[0], routeAndNode[1]);
//            localSearchInsertion();
//            localSearchSwapHubWithNode();
        }
    }

    private void swapWithEachNode(int routeIdx, int nodeIdx) {
        int counter = 0;
        int bCounter = 0;
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                if (routeIdx == i && nodeIdx == j) continue;
                // insert the current node before each node
                if (routeIdx == i) {
                    swapNodeInRoute(false, routeIdx, nodeIdx, j);
//                        phmlrp.print(false);
                } else {
                    swapNodeWithinRoutes(false, routeIdx, i, nodeIdx, j);
//                        phmlrp.print(false);
                }
            }
        }
//        System.out.println("Route: " + routeIdx + " Node: " + nodeIdx + " Counter: " + counter + " bCounter: " + bCounter);
    }

    /**
     * localSearchSwap end
     */

    void localSearchSwapHubWithNode() {
        for (int h = 0; h < phmlrp.getHubsArr().length; h++) {
            for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
                for (int j = 0; j < phmlrp.getVehiclesList().get(i).size(); j++) {
                    // going through each node recursively,
                    // then swapping the current node with every other node and calculating cost each time
                    swapHubWithNode(false, h, i, j);
                }
            }
        }
    }

    void localSearchEdgeOpt() {
        // create a list of all non-hub nodes
        List<Integer> initList = new ArrayList<Integer>();
        for (int i = 0; i < phmlrp.getVehiclesList().size(); i++) {
            if (phmlrp.getVehiclesList().get(i).size() < 2) {
                continue;
            }
            for (int j = 0; j < phmlrp.getVehiclesList().get(i).size() - 1; j += 2) {
                initList.add(phmlrp.getVehiclesList().get(i).get(j));
//                System.out.println(phmlrp.getVehiclesList().get(i).get(j));
            }
        }

        for (int edge : initList) {
            // going through each edge recursively,
            // then swapping the current edge with every other edge and calculating cost each time
//            System.out.println("edge " + edge);
            int counter = 0;
            int bCounter = 0;
            for (int edge2 : initList) {
                if (edge == edge2) continue;

                int[] routeAndNode = searchInMainList(edge);
                int routeIdx1 = routeAndNode[0];
                int nodeIdx1 = routeAndNode[1];
                int[] routeAndNode2 = searchInMainList(edge2);
                int routeIdx2 = routeAndNode2[0];
                int nodeIdx2 = routeAndNode2[1];

                // edgeOpt the current edge with each edge
                if (routeIdx1 == routeIdx2) {
                    boolean a = edgeOptInRoute(false, routeIdx1, nodeIdx1, nodeIdx2);
//                    phmlrp.print(false);
                    if (a) counter++;
                    else bCounter++;
                } else {
                    boolean a = edgeOptWithinRoutes(false, routeIdx1, routeIdx2, nodeIdx1, nodeIdx2);
//                    phmlrp.print(false);
                    if (a) counter++;
                    else bCounter++;
                }

//                System.out.println("Route: " + routeIdx1 + " Node: " + edge2 + " Counter: " + counter + " bCounter: " + bCounter);
            }
        }
    }

    void doLocalSearch(int k) {
        switch (k) {
            case 0:
                localSearchInsertion();
                break;
            case 1:
                localSearchSwap();
                break;
            case 2:
                localSearchSwapHubWithNode();
                break;
            case 3:
                localSearchEdgeOpt();
                break;
        }
    }
}
