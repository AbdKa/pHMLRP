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
    private final long MAX_RUN_TIME;
    private final Params params;

    //    0, insertNodeBetweenRoutes
    //    1, edgeOptWithinRoutes
    //    2, insertTwoNodes
    //    3, twoOptAlgorithm
    //    4, insertNodeInRoute
    //    5, swapNodeInRoute
    //    6, swapNodeWithinRoutes
    //    7, swapHubWithNode
    //    , nodesRemoveAndGreedyInsert
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
        MAX_RUN_TIME = Utils.getMaxRunTime(params.getNumNodes());
    }

    void runVNS() {
        String uniqueFileName = Utils.getUniqueFileName(params);
        OutputStream stream = outputStream(params, uniqueFileName);
        out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        long start = System.nanoTime();
        doVNS(start);
        double currentObj = doLS(bestPHCRP);
        if (currentObj < minObj) {
            setValues(bestIteration + 1, bestPHCRP, currentObj);
        }
        double solCPU = Utils.getSolCPU(start);

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

    private void doVNS(final long start) {

        out.println("iteration, cost, hubs, routes");

        int iteration = 0;

        while (System.nanoTime() - start < MAX_RUN_TIME) {
            for (int combIdx = 0; combIdx < combinations.size() && System.nanoTime() - start < MAX_RUN_TIME; combIdx++) {
                // run on every combination

//              1) shaking
                PHCRP pHCRP = Utils.newPHCRPInstance(this.params);
                double currentInitObj = pHCRP.getMaxCost();

//              2&3) Best improvement & Neighborhood Change
                for (int k : combinations.get(combIdx)) {
                    // for each neighborhood
                    if (!silent)
                        System.out.println(combIdx + " " + k);

                    // if doesn't give a better solution or Maximum time reached, (VND)
                    // break and jump to next neighborhood (Neighborhood Change)
                    while (pHCRP.move(k) && System.nanoTime() - start < MAX_RUN_TIME) {
                        // change neighborhood until no better solution, jump to next one
                        iteration++;
                    }

                    double currentObj = pHCRP.getMaxCost();
                    if (currentObj < minObj) {
                        initObj = currentInitObj;
                        setValues(iteration, pHCRP, currentObj);
                    }

//                  LS part
                    iteration++;
                    currentObj = doLS(pHCRP);
                    if (currentObj < minObj) {
                        initObj = currentInitObj;
                        setValues(iteration, pHCRP, currentObj);
                    }

                    if (System.nanoTime() - start >= MAX_RUN_TIME)
                        break;
                }
            }
        }
    }

    private void setValues(int iteration, PHCRP pHCRP, double currentObj) {
        bestPHCRP = pHCRP;
        minObj = currentObj;
        initCPU = pHCRP.getInitCPU();
        bestIteration = iteration;

        printLine(bestPHCRP, out, bestIteration, minObj);
    }

    private double doLS(PHCRP pHCRP) {
        Operations operations = new Operations(pHCRP);

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

        return pHCRP.getMaxCost();
    }
}
