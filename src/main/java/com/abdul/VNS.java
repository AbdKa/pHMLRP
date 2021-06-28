package com.abdul;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

class VNS {
    private final long MAX_RUN_TIME;
    private final Params params;

    //    0, insertNodeBetweenRoutes
    //    1, edgeOptWithinRoutes
    //    2, insertNodeInRoute
    //    3, swapNodeInRoute
    //    4, swapNodeWithinRoutes
    //    5, edgeOptInRoute
    //    6, swapHubWithNode
    //    , insertTwoNodes
    //    , twoOptAlgorithm
    //    , nodesRemoveAndGreedyInsert
    private int[] combination = {0, 1, 2, 3, 4, 5, 6};

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

        PHCRP pHCRP = Utils.newPHCRPInstance(this.params);
        bestPHCRP = new PHCRP(pHCRP);
        initObj = pHCRP.getMaxCost();
        minObj = initObj;
        initCPU = pHCRP.getInitCPU();

        while (System.nanoTime() - start < MAX_RUN_TIME) {

            int k = 0;
            while (k < combination.length) {
                // for each neighborhood

//              break loop if MAX_RUN_TIME is reached
                if (System.nanoTime() - start >= MAX_RUN_TIME)
                    break;

//              copy the best PHCRP
                pHCRP = new PHCRP(bestPHCRP);
//              1) shaking: random move
                Operations operations = new Operations(pHCRP);
                operations.move(true, true, -1);
//              number of moves to shake (shuffle) the solution
                int i = 0;
                while(i < 20) {
                    operations.move(false, false, combination[k]);
                    i++;
                }

//              2) Best improvement
                doLS(pHCRP);

                iteration++;

                double currentObj = pHCRP.getMaxCost();
                if (currentObj < minObj) {
                    if (!silent) System.out.println(combination[k] + " " + currentObj);
                    setValues(iteration, pHCRP, currentObj);
                    k = 0;
                }

//              3) Neighborhood Change
                k++;
            }
        }
    }

    private void setValues(int iteration, PHCRP pHCRP, double currentObj) {
        bestPHCRP = new PHCRP(pHCRP);
        minObj = currentObj;
        bestIteration = iteration;

        printLine(bestPHCRP, out, bestIteration, minObj);
    }

    private double doLS(PHCRP pHCRP) {
        Operations operations = new Operations(pHCRP);

//        if (!silent)
//            System.out.println("Insertion LS");
        operations.localSearchInsertion();
//        if (!silent)
//            System.out.println("SwapHub LS");
        operations.localSearchSwapHubWithNode();
//        if (!silent)
//            System.out.println("Swap LS");
        operations.localSearchSwap();
//        if (!silent)
//            System.out.println("EdgeOpt LS");
        operations.localSearchEdgeOpt();

        return pHCRP.getMaxCost();
    }
}
