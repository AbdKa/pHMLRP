package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

class SimulatedAnnealing {

    private final Random random = new Random();

    private final PHCRP PHCRP;
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
    private final double alpha = 0.95;
    // Number of iterations of annealing before decreasing temperature
    private final int numIterations = 10;
    private double solCPU;
    private int bestIteration;
    private String bestHubs;
    private String bestRoutes;
    private String hubs;
    private String routes;

    SimulatedAnnealing(PHCRP PHCRP, Params params) {
        this.PHCRP = PHCRP;
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
        double minCost = PHCRP.getMaxCost();
        // new solution initialization
        ArrayList<List<Integer>> newSol;

        PHCRP.print();

        PHCRP.setSimulatedAnnealing(true);

        // Continues annealing until reaching minimum
        // temperature
        int counter = 0;
        while (T > minT) {
            if (!silent)
                System.out.println("Temperature " + T);

            for (int i = 0; i < numIterations; i++) {
                int operationNum = doRandomOperation();
                newSol = PHCRP.getVehiclesList();
                double newCost = PHCRP.getSaOperationCost();
                double difference = minCost - newCost;

                addValuesToLists(counter, operationNum, newCost, difference);
                counter++;

                // Reassigns global minimum accordingly
                if (difference > 0) {
                    setBestVehiclesList(newSol);
                    minCost = newCost;
                    bestIteration = counter-1;
                    bestHubs = hubs;
                    bestRoutes = routes;

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

        solCPU = (System.nanoTime() - startTime) / 1e6;

        setGeneralValues(minCost);

        String uniqueFileName = params.getDataset() + "." + params.getNumNodes() + "." + params.getNumHubs() + "." +
                params.getNumVehicles() + "-" + params.getInitSol() + "-SA" + "-" +
                UUID.randomUUID().toString().replaceAll("-", "");
        printResultsExcel(uniqueFileName);

        // Capture the desired output by saving standard error to a file.
        // Later of you can open this dump with excel and apply text to columns.
        // You can create pivot tables, analyse results, min, max, average, compare algorithms etc.
        System.err.println(uniqueFileName + "\t" + PHCRP.getSaOperationCost());

        PHCRP.setSimulatedAnnealing(false);

        PHCRP.resetVehiclesList(bestSol);
        PHCRP.print();
//        System.out.println(counter);
    }

    private void addValuesToLists(int counter, int operationNum, double newCost, double difference) {
        temps.add(counter, T);
        costs.add(counter, newCost);
        differences.add(counter, difference);
        operationNums.add(counter, operationNum);

        hubs = PHCRP.getHubsString();
        routes = PHCRP.getVehiclesListString();
        hubsList.add(counter, hubs);
        routesList.add(counter, routes);
    }

    private void setGeneralValues(double minCost) {
//        get the index of the current solution
        int solIdx = GeneralResults.getIndex(params);
        double generalCost = GeneralResults.objectives[solIdx];
        if (minCost < generalCost) {
            GeneralResults.initials[solIdx] = params.getInitSol();
            GeneralResults.algorithms[solIdx] = params.getAlgorithm();
            GeneralResults.objectives[solIdx] = minCost;
            GeneralResults.CPUs[solIdx] = solCPU;
            GeneralResults.iterations[solIdx] = bestIteration;
            if (bestHubs != null) {
                GeneralResults.hubsArr[solIdx] = bestHubs;
                GeneralResults.routesArr[solIdx] = bestRoutes;
            }
        }
    }

    private int doRandomOperation() {

        int randOpr = random.nextInt(7);

        Operations operations = new Operations(PHCRP);

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
            case 6:
                operations.swapHubWithNode(true, -1, -1, -1);
                break;
        }

//        operations.localSearchInsertion();
//        operations.localSearchSwapHubWithNode();
//        operations.localSearchSwap();
//        operations.localSearchEdgeOpt();

        return randOpr;
    }

    private void printResultsExcel(String uniqueFileName) {
        XSSFWorkbook saWorkbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = saWorkbook.createSheet("SA Temps");
        createSaFirstRow(spreadsheet);

        for (int i = 0; i < temps.size(); i++) {
            XSSFRow row = spreadsheet.createRow(i + 1);
//            row.createCell(0, CellType.NUMERIC).setCellValue(temps.get(i));
            row.createCell(0, CellType.NUMERIC).setCellValue(i + 1);
            row.createCell(1, CellType.NUMERIC).setCellValue(costs.get(i));
//            row.createCell(3, CellType.NUMERIC).setCellValue(differences.get(i));
            row.createCell(2, CellType.NUMERIC).setCellValue(hubsList.get(i));
            row.createCell(3, CellType.NUMERIC).setCellValue(routesList.get(i));
//            row.createCell(6, CellType.NUMERIC).setCellValue(operationNums.get(i));
        }

        try {
            Utils.createExcelFile(saWorkbook, params.getResultPath() + "/" + uniqueFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createSaFirstRow(XSSFSheet saSpreadsheet) {
        XSSFRow row = saSpreadsheet.createRow(0);
//        row.createCell(0, CellType.STRING).setCellValue("Temp");
        row.createCell(0, CellType.STRING).setCellValue("Iteration#");
        row.createCell(1, CellType.STRING).setCellValue("Solution Cost");
//        row.createCell(3, CellType.STRING).setCellValue("Difference");
        row.createCell(2, CellType.STRING).setCellValue("hubs");
        row.createCell(3, CellType.STRING).setCellValue("routes");
//        row.createCell(6, CellType.STRING).setCellValue("Executed Operation");
    }
}
