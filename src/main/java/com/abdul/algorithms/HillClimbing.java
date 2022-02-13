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

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

public class HillClimbing {

    private final long MAX_RUN_TIME;

    private final Params params;
    private final boolean silent;

    private int bestIteration = 0;
    private PHCRP theBest;
    private double bestObj;

    public HillClimbing(Params params) {
        this.params = params;
        this.silent = params.getSilent();
        MAX_RUN_TIME = Utils.getMaxRunTime(params.getNumNodes());
    }

    public void runHC() {

        final String uniqueFileName = Utils.getUniqueFileName(params);
        OutputStream stream = outputStream(params, uniqueFileName);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        out.println("iteration, cost, hubs, routes");

        theBest = Utils.newPHCRPInstance(params);
        PHCRP initSol = new PHCRP(theBest);
        PHCRP current = Utils.newPHCRPInstance(params);

        // Global minimum
        bestObj = theBest.getMaxCost();
        double initObj = bestObj;

        // The zeroth iteration is the initial solution
        printLine(theBest, out, 0, initObj);

        final int L = params.isForce() ? theBest.getNumNodes() : theBest.getNumNodes() * 10;
        final long start = System.nanoTime();

        // Outer loop for max time stop condition
        int counter = 1;
        while (System.nanoTime() - start < MAX_RUN_TIME) {
            // SA variant: start with the best of the best for each new temperature value.
            if (params.isBest()) current = new PHCRP(theBest);

            int i = 1;
            while (i < L && System.nanoTime() - start < MAX_RUN_TIME) {

                PHCRP temp = new PHCRP(current);
                Operations operations = new Operations(temp);
                operations.move(true, true, -1);

                double difference = temp.getSaOperationCost() - current.getSaOperationCost();

                if (difference < 0) {
                    // Reassigns global minimum accordingly
                    current = temp;
                }

                // Update the best of the best
                if (current.getSaOperationCost() < bestObj) {
                    theBest = new PHCRP(current);
                    setBestValues(out, counter);
                }

                counter++;
            }

            doLS();
            if (theBest.getSaOperationCost() < bestObj) {
                setBestValues(out, counter);
            }
        }

        double solCPU = Utils.getSolCPU(start);
        solCPU += theBest.getInitCPU();

        out.flush();
        out.close();
        try {
            stream.close();
        } catch (IOException e) {
            // NO-OP
        }

        System.err.printf("%s\t%.2f\t%s\t%.2f\t%s\n",
                uniqueFileName, initObj, initSol.getRoutes(), bestObj, theBest.getRoutes());
    }

    private void setBestValues(PrintWriter out, int counter) {
        bestObj = theBest.getSaOperationCost();
        bestIteration = counter;

        printLine(theBest, out, counter, bestObj);

        if (!silent)
            System.out.println("the best updated!");
    }

    private void doLS() {
        Operations operations = new Operations(theBest);
        operations.localSearchInsertion();
        operations.localSearchSwapHubWithNode();
        operations.localSearchSwap();
        operations.localSearchEdgeOpt();

        theBest.setSaOperationCost(theBest.getMaxCost());
    }
}
