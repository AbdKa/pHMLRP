package com.abdul.algorithms;

import com.abdul.Operations;
import com.abdul.PHCRP;
import com.abdul.Params;
import com.abdul.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

class GVNS {
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
    private int[] neighborhoods = {0, 1, 2, 3, 4, 5, 6};

    private PHCRP bestPHCRP;
    private double initObj = Integer.MAX_VALUE;
    private double minObj = Integer.MAX_VALUE;
    private double initCPU = 0;
    private int bestIteration = 0;
    private int iteration = 0;
    private long start;

    private final boolean silent;

    private PrintWriter out;

    GVNS(Params params) {
        this.params = params;
        this.silent = params.getSilent();
        MAX_RUN_TIME = Utils.getMaxRunTime(params.getNumNodes());
    }

    void runGVNS() {
        String uniqueFileName = Utils.getUniqueFileName(params);
        OutputStream stream = outputStream(params, uniqueFileName);
        out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        start = System.nanoTime();
        doGVNS(start);
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

    private void doGVNS(final long start) {
        out.println("iteration, cost, hubs, routes");

        PHCRP pHCRP = Utils.newPHCRPInstance(this.params);
        bestPHCRP = new PHCRP(pHCRP);
        initObj = pHCRP.getMaxCost();
        minObj = initObj;
        initCPU = pHCRP.getInitCPU();

        while (System.nanoTime() - start < MAX_RUN_TIME) {

//            Operations operations = new Operations(pHCRP);
//            operations.move(false);

            int k = 0;
            while (k < neighborhoods.length) {
                // for each neighborhood

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
                    operations.move(false, false, neighborhoods[k]);
                    i++;
                }

//              2) Best improvement
                VND(pHCRP);

                iteration++;

                double currentObj = pHCRP.getMaxCost();
                if (currentObj < minObj) {
                    if (!silent) System.out.println(neighborhoods[k] + " " + currentObj);
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

    private void VND(PHCRP pHCRP) {
//      order neighborhoods randomly
        Integer[] vndNeighborhoods = {0, 1, 2, 3, 4, 5, 6};
        List<Integer> list = Arrays.asList(vndNeighborhoods);
        Collections.shuffle(list);
        list.toArray(vndNeighborhoods);

        for (int l = 0; l < vndNeighborhoods.length; l++) {
            if (System.nanoTime() - start >= MAX_RUN_TIME)
                break;

            Operations operations = new Operations(pHCRP);
            while (operations.move(false, false, vndNeighborhoods[l])) {
                if (System.nanoTime() - start >= MAX_RUN_TIME)
                    break;

                doLS(pHCRP);
            }

            double currentObj = pHCRP.getMaxCost();
            if (currentObj < minObj) {
                setValues(iteration, pHCRP, currentObj);
                l = 0;
            }
        }
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
