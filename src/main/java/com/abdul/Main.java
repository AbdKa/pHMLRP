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

        PHMLRP bound = null;
        int maxCost = 0;
        int maxCostWithoutMinEdge = 0;

        for (int i = 0; i < 1; i++) {
            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor());
            phmlrp.randomSolution();

            final int cost = phmlrp.cost();
            final int costWithoutMinEdge = phmlrp.costWithoutMinEdge();

            if (cost > maxCost) {
                bound = phmlrp;
                maxCost = cost;
            }
            if (costWithoutMinEdge > maxCostWithoutMinEdge) {
                maxCostWithoutMinEdge = costWithoutMinEdge;
            }
        }

        bound.print(params.getVerbose());
    }
}
