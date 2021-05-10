package com.abdul;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

class VNS {
    private Params params;
    private int replicasPerProb = 100;
    private int replicasPerCombination = 100;

    //    0, insertNodeBetweenRoutes
    //    1, edgeOptWithinRoutes
    //    2, insertTwoNodes
    //    3, twoOptAlgorithm
    //    4, insertNodeInRoute
    //    5, swapNodeInRoute
    //    6, swapNodeWithinRoutes
    //    7, nodesRemoveAndGreedyInsert
    //    8, swapHubWithNode
    private List<List<Integer>> combinations;

    private PHCRP bestPHCRP;
    private double initObj = Integer.MAX_VALUE;
    private double minObj = Integer.MAX_VALUE;
    private double initCPU = 0;
    private int bestIteration = 0;

    private final boolean silent;

    private PrintWriter out;

    VNS(Params params) {
        this.params = params;
        this.silent = params.getSilent();
        combinations = Utils.getCombinations("Combinations");
    }

    void runVNS() {
        String uniqueFileName = Utils.getUniqueFileName(params);
        OutputStream stream = outputStream(params, uniqueFileName);
        out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        long startTime = System.nanoTime();
        doVNS();
        doLS();
        double solCPU = Utils.getSolCPU(startTime);

        out.flush();
        out.close();
        try {
            stream.close();
        } catch (IOException e) {
            // NO-OP
        }

        System.err.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%d\n",
                uniqueFileName, initObj, initCPU, minObj, solCPU, bestIteration);
    }

    private void doVNS() {

        out.println("iteration, cost, hubs, routes");

        int iteration = 0;

        for (int replicaIdx = 0; replicaIdx < replicasPerProb; replicaIdx++) {
            // run each problem instance n number of replicas
            for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
                // run on every combination
                for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination;
                     repPerCombinationIdx++) {
                    // run each combination n number of replicas

                    PHCRP pHCRP = Utils.newPHCRPInstance(this.params);
                    double currentInitObj = pHCRP.getMaxCost();

                    for (int k : combinations.get(combIdx)) {
                        // for each neighborhood
                        if (!silent)
                            System.out.println(replicaIdx +
                                    " " + combIdx +
                                    " " + repPerCombinationIdx +
                                    " " + k);

                        // if doesn't give a better solution, break and jump to next neighborhood
                        while (pHCRP.callOperation(k)) {
                            // change neighborhood until no better solution, jump to next one
                            iteration++;
                        }
                    }

                    double newObj = pHCRP.getMaxCost();

                    if (newObj < minObj) {
                        bestPHCRP = pHCRP;
                        initObj = currentInitObj;
                        minObj = newObj;
                        initCPU = pHCRP.getInitCPU();
                        bestIteration = iteration;

                        printLine(bestPHCRP, out, bestIteration, minObj);
                    }
                }
            }
        }
    }

    private void doLS() {
        Operations operations = new Operations(bestPHCRP);

        if (!silent)
            System.out.println("Insertion LS");
        operations.localSearchInsertion();
        if (!silent)
            System.out.println("SwapHub LS");
        operations.localSearchSwapHubWithNode();
        if (!silent)
            System.out.println("Swap LS");
        operations.localSearchSwap();
        if (!silent)
            System.out.println("EdgeOpt LS");
        operations.localSearchEdgeOpt();

        double newObj = bestPHCRP.getMaxCost();

        if (newObj < minObj) {
            minObj = bestPHCRP.getMaxCost();
            printLine(bestPHCRP, out, bestIteration + 1, minObj);
        }
    }
}
