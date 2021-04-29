package com.abdul;

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class SimulatedAnnealing {

    private final Random random = new Random();

    private final PHCRP pHCRP;
    private final Params params;
    private final boolean silent;

    private ArrayList<List<Integer>> bestSol;

    private final List<Double> temps = new ArrayList<>();
    private final List<Double> costs = new ArrayList<>();
    private final List<Double> differences = new ArrayList<>();
    private final List<Integer> operationNums = new ArrayList<>();
    private final List<String> hubsList = new ArrayList<>();
    private final List<String> routesList = new ArrayList<>();

    // Simulated Annealing parameters
    // Initial temperature
    private double T = 1000000;
    // Temperature at which iteration terminates
    private final double minT = .0000001;
    // Decrease in temperature
    private final double alpha = 0.99;
    // Number of iterations of annealing before decreasing temperature
    private final int numIterations = 10;
    private double initObj;
    private double initCPU;
    private double solCPU;
    private int bestIteration;
    private String bestHubs;
    private String bestRoutes;

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
        long startTime = System.nanoTime();
        // Global minimum
        double minObj = pHCRP.getMaxCost();
        initObj = minObj;
        // new solution initialization
        ArrayList<List<Integer>> newSol;

        pHCRP.print();

        pHCRP.setSimulatedAnnealing(true);

        doLS();

        // Continues annealing until reaching minimum
        // temperature
        int counter = 0;
        while (T > minT) {
            if (!silent)
                System.out.println("Temperature " + T);

            for (int i = 0; i < numIterations; i++) {
                int operationNum = doRandomOperation();
                newSol = pHCRP.getVehiclesList();
                double newCost = pHCRP.getSaOperationCost();
                double difference = minObj - newCost;

                if (difference <= 0) {
//                    add values to lists if greater than or equal to minObj otherwise add after doLS()
                    addValuesToLists(counter, operationNum, newCost, difference);
                    counter++;
                } else {
                    // Reassigns global minimum accordingly
                    doLS();
                    newCost = pHCRP.getSaOperationCost();
                    setBestVehiclesList(pHCRP.getVehiclesList());
                    minObj = pHCRP.getSaOperationCost();
                    bestIteration = counter;
                    bestHubs = pHCRP.getHubsString();
                    bestRoutes = pHCRP.getVehiclesListString();
                    initCPU = pHCRP.getInitCPU();
//                   add values to lists after doLS()
                    addValuesToLists(counter, operationNum, newCost, difference);
                    counter++;

                    continue;
                }

                double probability = Math.exp(difference / T);
                if (probability > Math.random()) {
//                    System.out.println("temp: " + T + "\tdifference: " + difference);
                    setBestVehiclesList(newSol);
                }

            }

            T *= alpha; // Decreases T, cooling phase
        }

        solCPU = (System.nanoTime() - startTime) / 1e9;
        solCPU += initCPU;

//        set values of the solution resulted from this algorithm into the arrays
//        at AlgoResults (contains best results) GeneralResults (contains best of the best results)
//        AlgoResults.setAlgoValues(params, minObj, solCPU, bestIteration, bestHubs, bestRoutes);
//        GeneralResults.setGeneralValues(params, initObj, minObj, solCPU, bestIteration, bestHubs, bestRoutes);

        String uniqueFileName = Utils.getUniqueFileName(params);
        printResultsCSV(uniqueFileName);

        // Capture the desired output by saving standard error to a file.
        // Later of you can open this dump with CSV and apply text to columns.
        // You can create pivot tables, analyse results, min, max, average, compare algorithms etc.
        System.err.println(uniqueFileName + "\t" + minObj);

        pHCRP.setSimulatedAnnealing(false);

        pHCRP.resetVehiclesList(bestSol);
        pHCRP.print();
//        System.out.println(counter);
    }

    private void addValuesToLists(int counter, int operationNum, double newCost, double difference) {
        temps.add(counter, T);
        costs.add(counter, newCost);
        differences.add(counter, difference);
        operationNums.add(counter, operationNum);

        String hubs = pHCRP.getHubsString();
        String routes = pHCRP.getVehiclesListString();
        hubsList.add(counter, hubs);
        routesList.add(counter, routes);
    }

    private int doRandomOperation() {

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

        return randOpr;
    }

    private void doLS() {
        Operations operations = new Operations(pHCRP);
        operations.localSearchInsertion();
        operations.localSearchSwapHubWithNode();
        operations.localSearchSwap();
        operations.localSearchEdgeOpt();
    }

    private void printResultsCSV(String uniqueFileName) {
//        just a range from 1 to temps.size() to input in CSV
        List<Integer> iterations = IntStream.rangeClosed(1, temps.size())
                .boxed().collect(Collectors.toList());

        try {
            Utils.createCSVFile(params.getResultPath() +
                            File.separator + params.getAlgorithm().toString() + File.separator + uniqueFileName,
                    "Iteration#, Solution Cost, hubs, routes",
                    iterations, costs, hubsList, routesList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
