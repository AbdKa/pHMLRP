package com.abdul;

import java.util.ArrayList;
import java.util.List;

class VND {
    private Params params;
    private int replicasPerProb = 10;
    private int replicasPerCombination = 30;

    private String[] problemInstances = {
            "TR.10.2.1", "TR.10.2.2", "TR.10.3.1",
            "TR.15.2.1", "TR.15.2.2",
            "TR.25.2.1", "TR.25.2.5", "TR.25.5.1", "TR.25.5.2",
            "TR.50.2.1", "TR.50.2.5", "TR.50.5.1", "TR.50.5.2",
            "TR.81.2.1", "TR.81.2.5", "TR.81.5.1", "TR.81.5.2",
            "TR.81.9.1", "TR.81.9.2", "TR.81.9.3", "TR.81.9.4", "TR.81.9.5",
            "AP.100.5.1", "AP.100.5.2", "AP.100.5.5",
            "AP.200.10.1", "AP.200.10.2", "AP.200.10.5"
    };

//    private PHMLRP[] bestReplicas = new PHMLRP[problemInstances.length];

    //    0, insertNodeBetweenRoutes
    //    1, edgeOpt
    //    2, insertTwoNodes
    //    3, twoOptAlgorithm
    //    4, insertNodeInRoute
    //    5, swapNodeInRoute
    //    6, swapNodeWithinRoutes
    //    7, nodesRemoveAndGreedyInsert
    //    8, swapHubWithNode
    private Integer[][] combinations = {{0, 1, 2, 3, 4, 5, 6, 7, 8}, {4, 8}};

    private List<List<List<List<PHMLRP>>>> bestReplicas = new ArrayList<>(problemInstances.length);

    VND(Params params) {
        this.params = params;
    }

    void runVND() {
        for (String problemInstance : problemInstances) {
            // for each problem instance
            List<List<List<PHMLRP>>> repPerProbList = new ArrayList<>(replicasPerProb);
            for (int replicaIdx = 0; replicaIdx < replicasPerProb; replicaIdx++) {
                // run each problem instance n number of replicas
                List<List<PHMLRP>> combList = new ArrayList<>(combinations.length);
                for (Integer[] combination : combinations) {
                    // run on every combination
                    List<PHMLRP> repPerCombList = new ArrayList<>(replicasPerCombination);
                    for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination; repPerCombinationIdx++) {
                        // run each problem instance n number of replicas
                        PHMLRP phmlrp = newPHMLRPInstance(problemInstance);
                        createInitSol(phmlrp);
                        for (int k : combination) {
                            // for each neighborhood
                            while (true) {
                                if (!phmlrp.callOperation(k)) {
                                    // if doesn't give a better solution, break and jump to next neighborhood
                                    break;
                                }
                                // change neighborhood until no better solution, jump to next one
                            }
                        }

                        repPerCombList.add(phmlrp);
                    }

                    combList.add(repPerCombList);
                }

                repPerProbList.add(combList);
            }

            bestReplicas.add(repPerProbList);
        }
    }

    private void createInitSol(PHMLRP phmlrp) {
        InitialSolutions initialSolutions = new InitialSolutions(
                phmlrp,
                phmlrp.getNumNodes(),
                phmlrp.getNumHubs(),
                phmlrp.getNumVehiclesPerHub());
        initialSolutions.probabilisticInitSol();
    }

    private PHMLRP newPHMLRPInstance(String problemInstance) {
        return new PHMLRP(
                problemInstance.split(".")[0],
                Integer.valueOf(problemInstance.split(".")[1]),
                Integer.valueOf(problemInstance.split(".")[2]),
                Integer.valueOf(problemInstance.split(".")[3]),
                params.getCollectionCostCFactor(),
                params.getDistributionCostCFactor(),
                params.getHubToHubCFactor(),
                params.getRemovalPercentage());
    }
}
