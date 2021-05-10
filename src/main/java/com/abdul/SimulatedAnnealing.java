package com.abdul;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

class SimulatedAnnealing {

    //    2 hours
//    private static final double MAX_RUN_TIME = 7.2e+12;
    private static final double MAX_RUN_TIME = 6e+10;

    private final Params params;
    private final boolean silent;

    // Simulated Annealing parameters
    // Initial temperature
    private double T = 1000000;
    // Temperature at which iteration terminates
    private final double minT = .0000001;
    // Decrease in temperature
    private final double alpha = 0.99;
    private int bestIteration = 0;

    private PHCRP theBest;
    private double bestObj;

    SimulatedAnnealing(Params params) {
        this.params = params;
        this.silent = params.getSilent();
    }

    void runSA() {

        final String uniqueFileName = Utils.getUniqueFileName(params);
        OutputStream stream = outputStream(params, uniqueFileName);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        out.println("iteration, cost, hubs, routes");

        theBest = Utils.newPHCRPInstance(params);
        PHCRP current = Utils.newPHCRPInstance(params);

        // Global minimum
        bestObj = theBest.getMaxCost();
        double initObj = bestObj;

        //TODO is the correct way to capture the initial solution's statistics?
        // The zeroth iteration is the initial solution
        printLine(theBest, out, 0, initObj);

        final int L = params.isForce() ? theBest.getNumNodes() : theBest.getNumNodes() * 10;
        final long start = System.nanoTime();

        // Continues annealing until reaching minimum
        // temperature
        int counter = 1;
        while (T > minT) {
            if (!silent)
                System.out.println("Temperature " + T);

            // SA variant: start with the best of the best for each new temperature value.
            if (params.isBest()) current = new PHCRP(theBest);

            int i = 1;
            while (i < L && System.nanoTime() - start < MAX_RUN_TIME) {

                PHCRP temp = new PHCRP(current);
                doRandomOperation(temp);

                double difference = temp.getSaOperationCost() - current.getSaOperationCost();

                if (difference > 0) {

                    double probability = Math.exp(-difference / T);
                    // accept/apply a bad solution (i.e. delta >= 0)
                    if (probability >= Math.random()) {
//                    System.out.println("temp: " + T + "\tdifference: " + difference);
                        current = temp;
                        i++;
                    } else {
                        // SA variant: increment the inner-loop counter when a bad solution is ignored.
                        if (!params.isForce()) i++;
                    }
                } else {
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

            T *= alpha; // Decreases T, cooling phase
        }

        double solCPU = Utils.getSolCPU(start);
        solCPU += theBest.getInitCPU();

//        set values of the solution resulted from this algorithm into the arrays
//        at AlgoResults (contains best results) GeneralResults (contains best of the best results)
//        AlgoResults.setAlgoValues(params, bestObj, solCPU, bestIteration, bestHubs, bestRoutes);
//        GeneralResults.setGeneralValues(params, initObj, bestObj, solCPU, bestIteration, bestHubs, bestRoutes);

        // printResultsCSV(uniqueFileName);
        out.flush();
        out.close();
        try {
            stream.close();
        } catch (IOException e) {
            // NO-OP
        }

        // Capture the desired output by saving standard error to a file.
        // Later of you can open this dump with CSV and apply text to columns.
        // You can create pivot tables, analyse results, min, max, average, compare algorithms etc.
        //TODO is this correct for capturing: initial solution's objective/CPU and SA's best solution's objective/CPU.
        System.err.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%d\n",
                uniqueFileName, initObj, theBest.getInitCPU(), bestObj, solCPU, bestIteration);
    }

    private void setBestValues(PrintWriter out, int counter) {
        bestObj = theBest.getSaOperationCost();
        bestIteration = counter;

        printLine(theBest, out, counter, bestObj);

        if (!silent)
            System.out.printf("the best updated!\tT = %.5f\n", T);
    }

    private void doRandomOperation(PHCRP pHCRP) {

        Random random = new Random();
        int randOpr = random.nextInt(8);

        Operations operations = new Operations(pHCRP);

        switch (randOpr) {
            case 0:
                operations.insertNodeInRoute(true, -1, -1, -1);
                break;
            case 1:
                operations.insertNodeBetweenRoutes(true, -1, -1, -1, -1);
                break;
            case 2:
                operations.swapNodeInRoute(true, -1, -1, -1);
                break;
            case 3:
                operations.swapNodeWithinRoutes(true, -1, -1, -1, -1);
                break;
            case 4:
                operations.edgeOptInRoute(true, -1, -1, -1);
                break;
            case 5:
                operations.edgeOptWithinRoutes(true, -1, -1, -1, -1);
                break;
//          two chances for swap hub with node
            case 6:
            case 7:
                operations.swapHubWithNode(true, -1, -1, -1);
                break;
        }
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
