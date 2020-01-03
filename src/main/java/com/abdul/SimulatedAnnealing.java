package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SimulatedAnnealing {

    private PHMLRP phmlrp;
    private ArrayList<List<Integer>> bestSol;

    SimulatedAnnealing(PHMLRP phmlrp) {
        this.phmlrp = phmlrp;
        setBestVehiclesList(phmlrp.getVehiclesList());
    }

    private void setBestVehiclesList(ArrayList<List<Integer>> vehiclesList) {
        this.bestSol = new ArrayList<List<Integer>>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<Integer>(list);
            this.bestSol.add(innerList);
        }
    }

    void applySA(XSSFWorkbook workbook, XSSFSheet spreadsheet) throws IOException {
        // Initial temperature
        double T = 1000000;

        // Simulated Annealing parameters

        // Temperature at which iteration terminates
        final double minT = .0000001;
        // Decrease in temperature
        final double alpha = 0.99;
        // Number of iterations of annealing before decreasing temperature
        final int numIterations = 1;
        // Global minimum
        int min = phmlrp.getMaxCost();
        // new solution initialization
        ArrayList<List<Integer>> newSol;

        int counter = 1;
        phmlrp.setSimulatedAnnealing(true);

        XSSFRow row;

        // Continues annealing until reaching minimum
        // temperature
        while (T > minT) {
            row = spreadsheet.createRow(counter);
            for (int i = 0; i < numIterations; i++) {
                doRandomOperation();
                newSol = phmlrp.getVehiclesList();
                int newCost = phmlrp.getSaOperationCost();
                int difference = min - newCost;

                row.createCell(0, CellType.NUMERIC).setCellValue(T);
                row.createCell(1, CellType.NUMERIC).setCellValue(counter);
                row.createCell(2, CellType.NUMERIC).setCellValue(newCost);
                row.createCell(3, CellType.NUMERIC).setCellValue(difference);
                printHubsAndRoutesToExcel(row);

                counter++;

                // Reassigns global minimum accordingly
                if (difference > 0) {
                    setBestVehiclesList(newSol);
                    min = newCost;
                    continue;
                }

                double probability = Math.pow(Math.E, difference / T);
                if (probability > Math.random()) {
                    System.out.println("temp: " + T + "\tdifference: " + difference);
                    setBestVehiclesList(newSol);
                }
            }

            T *= alpha; // Decreases T, cooling phase
        }

        //Write the workbook in file system
        FileOutputStream out = new FileOutputStream(
                new File("sa_results.xlsx"));

        workbook.write(out);
        out.close();
        System.out.println("sa_results.xlsx written successfully");

        phmlrp.setSimulatedAnnealing(false);

        phmlrp.resetVehiclesList(bestSol);
        phmlrp.print(false);
        System.out.println(counter);
    }

    private void printHubsAndRoutesToExcel(XSSFRow row) {
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
        row.createCell(4, CellType.STRING).setCellValue(hubs.toString());
        row.createCell(5, CellType.STRING).setCellValue(routes.toString());
    }

    private void doRandomOperation() {
        Random random = new Random();
        int randOpr = random.nextInt(5);

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
                operations.edgeOpt(true);
                break;
            case 5:
                operations.swapHubWithNode(true);
                break;
        }
    }
}
