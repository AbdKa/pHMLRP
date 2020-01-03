package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        PHMLRP bound = null;
        int maxCost = 0;
        int maxCostWithoutMinEdge = 0;

//        for (int i = 0; i < 1; i++) {
//            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
//                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
//                    params.getRemovalPercentage());
//            phmlrp.randomSolution();
//
//            final int cost = phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
//            final int costWithoutMinEdge = phmlrp.costWithoutMinEdge();
//
//            if (cost > maxCost) {
//                bound = phmlrp;
//                maxCost = cost;
//            }
//            if (costWithoutMinEdge > maxCostWithoutMinEdge) {
//                maxCostWithoutMinEdge = costWithoutMinEdge;
//            }
//        }
//
//        if (bound != null) {
//            bound.print(params.getVerbose());
//        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFWorkbook saWorkbook = new XSSFWorkbook();

        applyAlgorithms(params, 2, 1, workbook, saWorkbook, "10.2.1");
        applyAlgorithms(params, 2, 2, workbook, saWorkbook, "10.2.2");
        applyAlgorithms(params, 3, 1, workbook, saWorkbook, "10.3.1");
        applyAlgorithms(params, 3, 2, workbook, saWorkbook, "10.3.2");

//        DeterministicPermutation deterministicOperation = new DeterministicPermutation(bound);
//        deterministicOperation.deterministicOperationOrder();

//        for (int i = 0; i < 1; i++) {
//            bound.randomOperation();
//        }
    }

    private static void applyAlgorithms(Params params, int numHubs, int numVehicles, XSSFWorkbook workbook, XSSFWorkbook saWorkbook, String sheetName) throws IOException {
        PHMLRP phmlrp = new PHMLRP(10, numHubs, numVehicles,
                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                params.getRemovalPercentage());
        phmlrpSolutionAndCost(phmlrp);

        XSSFSheet spreadsheet = workbook.createSheet(sheetName);
        XSSFRow row = spreadsheet.createRow(0);
        createFirstRow(row);
        phmlrp.deterministicExplore(workbook, spreadsheet);

        // Simulated Annealing
        PHMLRP saPhmlrp = new PHMLRP(10, numHubs, numVehicles,
                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                params.getRemovalPercentage());
        phmlrpSolutionAndCost(saPhmlrp);

        XSSFSheet saSpreadsheet = saWorkbook.createSheet(sheetName);
        XSSFRow saRow = saSpreadsheet.createRow(0);
        createSaFirstRow(saRow);
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(saPhmlrp);
        simulatedAnnealing.applySA(saWorkbook, saSpreadsheet);
    }

    private static void phmlrpSolutionAndCost(PHMLRP phmlrp) {
        phmlrp.randomSolution();
        phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
    }

    private static void createFirstRow(XSSFRow row) {
        row.createCell(0, CellType.STRING).setCellValue("Operation Name");
        row.createCell(1, CellType.STRING).setCellValue("Run#");
        row.createCell(2, CellType.STRING).setCellValue("Difference");
        row.createCell(3, CellType.STRING).setCellValue("Best Cost");
        row.createCell(4, CellType.STRING).setCellValue("Elapsed Time (nano)");
        row.createCell(5, CellType.STRING).setCellValue("hubs");
        row.createCell(6, CellType.STRING).setCellValue("routes");
    }

    private static void createSaFirstRow(XSSFRow row) {
        row.createCell(0, CellType.STRING).setCellValue("Temp");
        row.createCell(1, CellType.STRING).setCellValue("Iteration#");
        row.createCell(2, CellType.STRING).setCellValue("Solution Cost");
        row.createCell(3, CellType.STRING).setCellValue("Difference");
        row.createCell(4, CellType.STRING).setCellValue("hubs");
        row.createCell(5, CellType.STRING).setCellValue("routes");
    }
}
