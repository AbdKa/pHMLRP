package com.abdul;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

class SimulatedAnnealing {

    private final Random random = new Random();

    private final PHCRP pHCRP;
    private final Params params;
    private final boolean silent;

    private ArrayList<List<Integer>> bestSol;

    /**
     * private final List<Double> temps = new ArrayList<>();
     * private final List<Double> costs = new ArrayList<>();
     * private final List<Double> differences = new ArrayList<>();
     * private final List<Integer> operationNums = new ArrayList<>();
     * private final List<String> hubsList = new ArrayList<>();
     * private final List<String> routesList = new ArrayList<>();
     **/

    // Simulated Annealing parameters
    // Initial temperature
    private double T = 1000000;
    // Temperature at which iteration terminates
    private final double minT = .0000001;
    // Decrease in temperature
    private final double alpha = 0.99;
    // Number of iterations of annealing before decreasing temperature
    private final int numIterations = 10;
    private int bestIteration = 0;
    //  private String bestHubs;
    //  private String bestRoutes;

    SimulatedAnnealing(PHCRP PHCRP, Params params) {
        this.pHCRP = PHCRP;
        this.params = params;
        setBestVehiclesList(PHCRP.getVehiclesList());
        this.silent = params.getSilent();
    }

    private void setBestVehiclesList(ArrayList<List<Integer>> vehiclesList) {
        this.bestSol = new ArrayList<>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<>(list);
            this.bestSol.add(innerList);
        }
    }

    void applySA() {

        final String uniqueFileName = Utils.getUniqueFileName(params);
        OutputStream stream = outputStream(params, uniqueFileName);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        out.println("iteration, cost, hubs, routes");

        // Global minimum
        double minObj = pHCRP.getMaxCost();
        double initObj = minObj;

        pHCRP.print();

        //TODO is the correct way to capture the initial solution's statistics?
        // The zeroth iteration is the initial solution
        printLine(pHCRP, out, 0, initObj);

        pHCRP.setSimulatedAnnealing(true);

        final long startTime = System.nanoTime();

        doLS();

//        TODO: the is just an example
        int repeatEach = 1000 / pHCRP.getNumNodes();

        // Continues annealing until reaching minimum
        // temperature
        int counter = 1;
        while (T > minT) {
            if (!silent)
                System.out.println("Temperature " + T);

            for (int i = 0; i < numIterations; i++) {
                doRandomOperation();
                if (i % repeatEach == 0) {
                    doLS();
                }
                double newObj = pHCRP.getSaOperationCost();
                double difference = minObj - newObj;

                if (difference <= 0) {
//                    add values to lists if greater than or equal to minObj otherwise add after doLS()
//                    addValuesToLists(counter, operationNum, newObj, difference);

                    double probability = Math.exp(difference / T);
                    if (probability > Math.random()) {
//                    System.out.println("temp: " + T + "\tdifference: " + difference);
                        setBestVehiclesList(pHCRP.getVehiclesList());
                    }
                    counter++;
                } else {
                    // Reassigns global minimum accordingly

                    newObj = pHCRP.getSaOperationCost();
                    setBestVehiclesList(pHCRP.getVehiclesList());
                    minObj = pHCRP.getSaOperationCost();

                    bestIteration = counter;
//                    bestHubs = pHCRP.getHubsString();
//                    bestRoutes = pHCRP.getVehiclesListString();
//                   add values to lists after doLS()
//                   addValuesToLists(counter, operationNum, newObj, difference);
                    printLine(pHCRP, out, counter, newObj);
                    counter++;
                }
            }

            T *= alpha; // Decreases T, cooling phase
        }

        double solCPU = Utils.getSolCPU(startTime);
        solCPU += pHCRP.getInitCPU();

//        set values of the solution resulted from this algorithm into the arrays
//        at AlgoResults (contains best results) GeneralResults (contains best of the best results)
//        AlgoResults.setAlgoValues(params, minObj, solCPU, bestIteration, bestHubs, bestRoutes);
//        GeneralResults.setGeneralValues(params, initObj, minObj, solCPU, bestIteration, bestHubs, bestRoutes);

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
                uniqueFileName, initObj, pHCRP.getInitCPU(), minObj, solCPU, bestIteration);

        pHCRP.setSimulatedAnnealing(false);

        pHCRP.resetVehiclesList(bestSol);
        pHCRP.print();
//        System.out.println(counter);
    }

    private void doRandomOperation() {

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
        pHCRP.setSimulatedAnnealing(false);
        Operations operations = new Operations(pHCRP);
        operations.localSearchInsertion();
        operations.localSearchSwapHubWithNode();
        operations.localSearchSwap();
        operations.localSearchEdgeOpt();

        pHCRP.setSaOperationCost(pHCRP.getMaxCost());

        pHCRP.setSimulatedAnnealing(true);
    }
}
