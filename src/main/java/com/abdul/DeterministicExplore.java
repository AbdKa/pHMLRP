package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DeterministicExplore {
    private final PHMLRP phmlrp;

    private int successCount = 0;

    DeterministicExplore(PHMLRP phmlrp) {
        this.phmlrp = phmlrp;
    }

    void doDeterministicExplore(XSSFWorkbook workbook, XSSFSheet spreadsheet) throws IOException {
        // Clone hubs array, vehicles list and max cost for reset after numOfIterationForEachOperation
        int[] initHubsArr = phmlrp.getHubsArr().clone();
        ArrayList<List<Integer>> initVehiclesList = new ArrayList<>();
        for (List<Integer> list : phmlrp.getVehiclesList()) {
            List<Integer> innerList = new ArrayList<>(list);
            initVehiclesList.add(innerList);
        }
        double initMaxCost = phmlrp.getMaxCost();

        XSSFRow row;
        int numberOfOperations = 9;
        int numOfIterationForEachOne = 100_000;
        String[] countsArr = new String[numberOfOperations];
        int operNum = 0;
        String operName = "";
        // loop numOfIterations on each operation
        for (int i = 0; i < numberOfOperations * numOfIterationForEachOne + 1; i++) {
            // reset the hubsArr, vehicles list and maxCost
            if (i % numOfIterationForEachOne == 0 && i > 0 || i == 801000) {
                phmlrp.setHubsArr(initHubsArr.clone());
                phmlrp.resetVehiclesList(initVehiclesList);
                countsArr[operNum] = operName + "," + successCount + "," + phmlrp.getMaxCost();
                phmlrp.setMaxCost(initMaxCost);
                successCount = 0;
                System.out.println(operNum + " -------------------------------------------- " + operName);
                operNum++;
            }

            // create a row for the excel sheet
            row = spreadsheet.createRow(i + 1);
            switch (operNum) {
                case 0:
                    // operation 0
                    doOperation(0, "insertNodeInRoute", row, i);
                    operName = "insertNodeInRoute";
                    break;
                case 1:
                    // operation 1
                    doOperation(1, "insertNodeBetweenRoutes", row, i);
                    operName = "insertNodeBetweenRoutes";
                    break;
                case 2:
                    // operation 2
                    doOperation(2, "swapNodeInRoute", row, i);
                    operName = "swapNodeInRoute";
                    break;
                case 3:
                    // operation 3
                    doOperation(3, "swapNodeWithinRoutes", row, i);
                    operName = "swapNodeWithinRoutes";
                    break;
                case 4:
                    // operation 4
                    doOperation(4, "edgeOptWithinRoutes", row, i);
                    operName = "edgeOptWithinRoutes";
                    break;
                case 5:
                    // operation 5
                    doOperation(5, "swapHubWithNode", row, i);
                    operName = "swapHubWithNode";
                    break;
                case 6:
                    // operation 6
                    doOperation(6, "twoOptAlgorithm", row, i);
                    operName = "twoOptAlgorithm";
                    break;
                case 7:
                    // operation 7
                    doOperation(7, "insertTwoNodes", row, i);
                    operName = "insertTwoNodes";
                    break;
                case 8:
                    // operation 8
                    doOperation(8, "nodesRemoveAndGreedyInsert", row, i);
                    operName = "nodesRemoveAndGreedyInsert";
                    System.out.println(i);
                    break;
            }

            printHubsAndRoutesToExcel(row);

            if (i == 801000) break;
        }

        XSSFSheet secondSS = workbook.createSheet(spreadsheet.getSheetName() + " timeSum");
        XSSFRow xssfRow = secondSS.createRow(0);
        xssfRow.createCell(0, CellType.STRING).setCellValue("Operation");
        xssfRow.createCell(1, CellType.STRING).setCellValue("Successful Count");
        xssfRow.createCell(2, CellType.STRING).setCellValue("Best Cost");
        for (int i = 0; i < countsArr.length; i++) {
            xssfRow = secondSS.createRow(i + 1);
            xssfRow.createCell(0, CellType.STRING).setCellValue(countsArr[i].split(",")[0]);
            xssfRow.createCell(1, CellType.NUMERIC).setCellValue(Integer.parseInt(countsArr[i].split(",")[1]));
            xssfRow.createCell(2, CellType.NUMERIC).setCellValue(Integer.parseInt(countsArr[i].split(",")[2]));
        }

        //Write the workbook in file system
        Utils.createExcelFile(workbook, "deterministic_results_" + spreadsheet.getSheetName());
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
        row.createCell(5, CellType.STRING).setCellValue(hubs.toString());
        row.createCell(6, CellType.STRING).setCellValue(routes.toString());
    }

    private void doOperation(int operationNum, String operationName, XSSFRow row, int i) {
        row.createCell(0, CellType.STRING).setCellValue(operationName);
        row.createCell(1, CellType.NUMERIC).setCellValue(i + 1);
        double priorCost = phmlrp.getMaxCost();
        //operation start time in milliseconds
        long startTime = System.nanoTime();
        phmlrp.callOperation(operationNum);
        //operation end time in milliseconds
        long endTime = System.nanoTime();
        //time elapsed
        double costDifference = priorCost - phmlrp.getMaxCost();
        if (costDifference > 0) {
            successCount++;
            phmlrp.print();
        }
        row.createCell(2, CellType.NUMERIC).setCellValue(costDifference);
        row.createCell(3, CellType.NUMERIC).setCellValue(phmlrp.getMaxCost());
        long elapsed = endTime - startTime;
        row.createCell(4, CellType.NUMERIC).setCellValue(elapsed);
    }
}
