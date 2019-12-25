package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        PHMLRP bound = null;
        int maxCost = 0;
        int maxCostWithoutMinEdge = 0;

        for (int i = 0; i < 1; i++) {
            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                    params.getRemovalPercentage());
            phmlrp.randomSolution();

            final int cost = phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
            final int costWithoutMinEdge = phmlrp.costWithoutMinEdge();

            if (cost > maxCost) {
                bound = phmlrp;
                maxCost = cost;
            }
            if (costWithoutMinEdge > maxCostWithoutMinEdge) {
                maxCostWithoutMinEdge = costWithoutMinEdge;
            }
        }

        if (bound != null) {
            bound.print(params.getVerbose());
        }

        assert bound != null;
//        SAOperations saOperations = new SAOperations(bound);
        DeterministicPermutation deterministicOperation = new DeterministicPermutation(bound);
        deterministicOperation.deterministicOperationOrder();

//        for (int i = 0; i < 1; i++) {
//            bound.randomOperation();
//        }
    }
}
