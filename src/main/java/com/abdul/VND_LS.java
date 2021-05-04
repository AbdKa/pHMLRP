package com.abdul;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

class VND_LS {
    private final Params params;
    private int replicasPerProb = 10;
    private final int replicasPerCombination = 100;

    //    0, Insertion
    //    1, Swap
    //    2, HubMove
    //    3, EdgeOpt
    private final List<List<Integer>> combinations;

    private double initObj = Integer.MAX_VALUE;
    private double minObj = Integer.MAX_VALUE;
    private double initCPU = 0;
    private int bestIteration = 0;

    VND_LS(Params params) {
        this.params = params;
        combinations = Utils.getCombinations("ls_combinations");
    }

    void runVND() {
        String uniqueFileName = Utils.getUniqueFileName(params);

        long startTime = System.nanoTime();
        doVND(uniqueFileName);
        double solCPU = Utils.getSolCPU(startTime);

        System.err.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%d\n",
                uniqueFileName, initObj, initCPU, minObj, solCPU, bestIteration);
    }

    private void doVND(String uniqueFileName) {
        OutputStream stream = outputStream(params, uniqueFileName);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        out.println("iteration, cost, hubs, routes");

        int iteration = 0;
        for (int replicaIdx = 0; replicaIdx < replicasPerProb; replicaIdx++) {
            // run each problem instance n number of replicas
            for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
                // run on every combination
                for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination; repPerCombinationIdx++) {
                    // run each problem instance n number of replicas

                    PHCRP pHCRP = Utils.newPHMLRPInstance(this.params);
                    createInitSol(pHCRP);
                    double currentInitObj = pHCRP.getMaxCost();
                    Operations operations = new Operations(pHCRP);

                    for (int k : combinations.get(combIdx)) {
                        // for each neighborhood

                        if (!params.getSilent())
                            System.out.println(replicaIdx +
                                    " " + combIdx +
                                    " " + repPerCombinationIdx +
                                    " " + k);
                        operations.doLocalSearch(k);

                        iteration++;
                    }

                    double newObj = pHCRP.getMaxCost();

                    if (newObj < minObj) {
                        initObj = currentInitObj;
                        minObj = newObj;
                        initCPU = pHCRP.getInitCPU();
                        bestIteration = iteration;

                        printLine(pHCRP, out, iteration, minObj);
                    }
                }
            }
        }
    }

    private void createInitSol(PHCRP pHCRP) {
        InitialSolutions initialSolutions = new InitialSolutions(pHCRP, params, true);
        pHCRP.calculateCost(PHCRP.CostType.NORMAL);
    }
}
