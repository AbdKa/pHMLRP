package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        // creating results paths
        String currentPath = System.getProperty("user.dir");
        String path = currentPath + File.separator + params.getResultPath();

        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        // call this to create greedy hubs for each problem instance
//        Extra.getGreedyHubs(params);

        // TODO: remove this if statement after preparing VNS
        if (params.getAlgorithm() == ALGO.SA) {
            PHCRP pHCRP = new PHCRP(params.getDataset(), params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                    params.getRemovalPercentage());
            pHCRP.setSilent(params.getSilent());
            InitialSolutions initialSolutions = new InitialSolutions(pHCRP, params.getDataset(),
                    params.getCollectionCostCFactor());

            switch (params.getInitSol()) {
                case RND:
                    initialSolutions.randomSolution();
                    break;
                case GREEDY:
                    initialSolutions.greedySolution();
                    break;
                case GREEDY_RND:
                    initialSolutions.greedyRandomSolution();
                    break;
                case RND_GREEDY:
                    initialSolutions.randomGreedySolution();
                    break;
                case PROB:
                    initialSolutions.probabilisticInitSol();
                    break;
                case GREEDY_GRB:
                case GRB:
                    initialSolutions.gurobiSolution(params.getInitSol());
                    break;
            }

            pHCRP.calculateCost(PHCRP.CostType.NORMAL);
//            add initial solution's hubs and routes to the general results
            int solIdx = GeneralResults.getIndex(params);
            GeneralResults.hubsArr[solIdx] = pHCRP.getHubsString();
            GeneralResults.routesArr[solIdx] = pHCRP.getVehiclesListString();

            switch (params.getAlgorithm()) {
                case SA:
                    SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(pHCRP, params);
                    simulatedAnnealing.applySA();
                    break;
                case VNS:
                    VND vnd = new VND(params);
                    vnd.runVND();
                    break;
            }
        }
    }
}
