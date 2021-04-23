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

            AlgoResults.setInitValues(params, pHCRP);
            GeneralResults.setInitValues(params, pHCRP);

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
