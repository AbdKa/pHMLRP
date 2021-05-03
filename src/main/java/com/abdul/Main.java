package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

public class Main {
    public static void main(String[] args) {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        // TODO: remove this if statement after preparing VNS
        if (params.getAlgorithm() == ALGO.VNS) {
            PHCRP pHCRP = new PHCRP(params.getDataset(), params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                    params.getRemovalPercentage());
            pHCRP.setSilent(params.getSilent());

//            do initial solution
            InitialSolutions initialSolutions = new InitialSolutions(pHCRP, params, true);

            pHCRP.calculateCost(PHCRP.CostType.NORMAL);

//            run algorithm
            switch (params.getAlgorithm()) {
                case SA:
                    SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(pHCRP, params);
                    simulatedAnnealing.applySA();
                    break;
                case VNS:
                    LS_VND vnd = new LS_VND(params);
                    vnd.runVND();
                    break;
            }
        }
    }
}
