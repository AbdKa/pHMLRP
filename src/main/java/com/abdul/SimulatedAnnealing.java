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

    private final PHMLRP phmlrp;
    private final Params params;
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

    SimulatedAnnealing(PHMLRP phmlrp, Params params) {
        this.phmlrp = phmlrp;
        this.params = params;
        setBestVehiclesList(phmlrp.getVehiclesList());
    }

    private boolean silent = false;

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    private void setBestVehiclesList(ArrayList<List<Integer>> vehiclesList) {
        this.bestSol = new ArrayList<>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<>(list);
            this.bestSol.add(innerList);
        }
    }

    void applySA() {
        // Global minimum
        double min = phmlrp.getMaxCost();
        // new solution initialization
        ArrayList<List<Integer>> newSol;

        phmlrp.print(false);

        phmlrp.setSimulatedAnnealing(true);

        // Continues annealing until reaching minimum
        // temperature
        int counter = 0;
        while (T > minT) {
            if (!silent)
                System.out.println("Temperature " + T);

            temps.add(counter, T);
            costs.add(counter, -1.0);
            differences.add(counter, -1.0);
            operationNums.add(counter, -1);
            hubsList.add(counter, "");
            routesList.add(counter, "");

            for (int i = 0; i < numIterations; i++) {
                int operationNum = doRandomOperation();
                newSol = phmlrp.getVehiclesList();
                double newCost = phmlrp.getSaOperationCost();
                double difference = min - newCost;

                costs.set(counter, newCost);
                differences.set(counter, difference);
                operationNums.set(counter, operationNum);
                addHubsAndRoutesStr(counter);

                // Reassigns global minimum accordingly
                if (difference > 0) {
                    setBestVehiclesList(newSol);
                    min = newCost;
                    continue;
                }

                double probability = Math.exp(difference / T);
                if (probability > Math.random()) {
//                    System.out.println("temp: " + T + "\tdifference: " + difference);
                    setBestVehiclesList(newSol);
                }
            }

            counter++;
            T *= alpha; // Decreases T, cooling phase
        }

        String uniqueFileName = params.getDataset() + "." + params.getNumNodes() + "." + params.getNumHubs() + "." +
                params.getNumVehicles() + "-" + params.getInitSol() + "-SA" + "-" +
                UUID.randomUUID().toString().replaceAll("-", "");
        printResultsExcel(uniqueFileName);

        // Capture the desired output by saving standard error to a file.
        // Later of you can open this dump with excel and apply text to columns.
        // You can create pivot tables, analyse results, min, max, average, compare algorithms etc.
        System.err.println(uniqueFileName + "\t" + phmlrp.getMaxCost());

        phmlrp.setSimulatedAnnealing(false);

        phmlrp.resetVehiclesList(bestSol);
        phmlrp.print(false);
//        System.out.println(counter);
    }

    private void addHubsAndRoutesStr(int counter) {
        StringBuilder hubs = new StringBuilder();
        StringBuilder routes = new StringBuilder();
        for (int hub : phmlrp.getHubsArr()) {
            hubs.append(hub).append(", ");
        }
        for (List<Integer> route : phmlrp.getVehiclesList()) {
            for (int node : route) {
                routes.append(node).append(", ");
            }
            routes.append("; ");
        }
        hubsList.set(counter, hubs.toString());
        routesList.set(counter, routes.toString());
    }

    private int doRandomOperation() {

        int randOpr = random.nextInt(7);

        Operations operations = new Operations(phmlrp);

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
            row.createCell(0, CellType.NUMERIC).setCellValue(temps.get(i));
            row.createCell(1, CellType.NUMERIC).setCellValue(i + 1);
            row.createCell(2, CellType.NUMERIC).setCellValue(costs.get(i));
            row.createCell(3, CellType.NUMERIC).setCellValue(differences.get(i));
            row.createCell(4, CellType.NUMERIC).setCellValue(hubsList.get(i));
            row.createCell(5, CellType.NUMERIC).setCellValue(routesList.get(i));
            row.createCell(6, CellType.NUMERIC).setCellValue(operationNums.get(i));
        }

        try {
            Utils.createExcelFile(saWorkbook, params.getResultPath() + "/" + uniqueFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createSaFirstRow(XSSFSheet saSpreadsheet) {
        XSSFRow row = saSpreadsheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("Temp");
        row.createCell(1, CellType.STRING).setCellValue("Iteration#");
        row.createCell(2, CellType.STRING).setCellValue("Solution Cost");
        row.createCell(3, CellType.STRING).setCellValue("Difference");
        row.createCell(4, CellType.STRING).setCellValue("hubs");
        row.createCell(5, CellType.STRING).setCellValue("routes");
        row.createCell(6, CellType.STRING).setCellValue("Executed Operation");
    }
}
