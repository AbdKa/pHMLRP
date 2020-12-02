package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        // creating results paths
        String currentPath = System.getProperty("user.dir");
//        String greedyPath = currentPath + "/incompleteHubResults_Greedy";
//        String randomPath = currentPath + "/incompleteHubResults_Random";
//        Files.createDirectories(Paths.get(greedyPath));
//        Files.createDirectories(Paths.get(randomPath));
        String path = currentPath + "/" + params.getResultPath();
        Files.createDirectories(Paths.get(path));

        Dataset dataset = new Dataset();

        // VND
        VndWithIncompleteHubs vnd = new VndWithIncompleteHubs(params, path, false);
        vnd.runVND();

//        randomObj.setHubsArr(new int[]{1, 10, 0, 13});
//        ArrayList<List<Integer>> vehicles = new ArrayList<>();
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(3)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(12)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(8)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(2)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(9)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(11)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(7)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(15)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(4)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(14)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(5)));
//        vehicles.add(new ArrayList<Integer>(Arrays.asList(6)));
//        randomObj.resetVehiclesList(vehicles);

        // random
//        PHMLRP randomObj = new PHMLRP(params.getDataset(), params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
//                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
//                params.getRemovalPercentage());

//
//        InitialSolutions randomSolutions = new InitialSolutions(randomObj);
//        randomSolutions.randomSolution();
//        randomObj.calculateCost(PHMLRP.CostType.NORMAL);
//        randomObj.print(false);
//
//        IncompleteHubs randomIncompleteHubs = new IncompleteHubs(randomObj, randomPath, params.getNumLinks());
//        randomIncompleteHubs.runIncomplete();

        // greedy
//        PHMLRP greedyObj = new PHMLRP(params.getDataset(), params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
//                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
//                params.getRemovalPercentage());
//
//        InitialSolutions greedySolutions = new InitialSolutions(greedyObj);
//        greedySolutions.greedySolution();
//        greedyObj.calculateCost(PHMLRP.CostType.NORMAL);
//        greedyObj.print(false);
//
//        IncompleteHubs greedyIncompleteHubs = new IncompleteHubs(greedyObj, greedyPath, params.getNumLinks());
//        greedyIncompleteHubs.runIncomplete();

//        InitialSolutions initialSolutions = new InitialSolutions(phmlrpObj, params.getNumNodes(), params.getNumHubs(), params.getNumVehicles());
//        initialSolutions.probabilisticInitSol();
//        phmlrpObj.calculateCost(PHMLRP.CostType.NORMAL);
//        phmlrpObj.print(false);


//        Gurobi gurobi = new Gurobi(phmlrpObj, params.getNumNodes(), params.getNumHubs(), params.getNumVehicles());
//        gurobi.getInitSol();
//        gurobi.optimizeRoute(new int[]{0,36,77,23,61,25,10,8,47});
//        gurobi.getSolWithHubs(new int[]{0,1});

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFWorkbook saWorkbook = new XSSFWorkbook();
        XSSFWorkbook dpWorkbook = new XSSFWorkbook();

//        applyAlgorithms(params, 2, 1, workbook, saWorkbook, dpWorkbook);
//        applyAlgorithms(params, 2, 2, workbook, saWorkbook, dpWorkbook, "10.2.2");
//        applyAlgorithms(params, 3, 1, workbook, saWorkbook, dpWorkbook, "10.3.1");
//        applyAlgorithms(params, 3, 2, workbook, saWorkbook, dpWorkbook, "10.3.2");

        /*int counter = 10;
        int timeSum = 0;
        for (int n = 0; n < counter; n++) {
            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                    params.getRemovalPercentage());
            phmlrp.greedySolution();
            phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
            timeSum += phmlrp.getMaxCost();
        }
        System.out.println("greedySolution: " + timeSum / counter);

        timeSum = 0;
        for (int n = 0; n < counter; n++) {
            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                    params.getRemovalPercentage());
            phmlrp.semiGreedySolution();
            phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
            timeSum += phmlrp.getMaxCost();
        }
        System.out.println("semiGreedySolution: " + timeSum / counter);

        timeSum = 0;
        for (int n = 0; n < counter; n++) {
            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                    params.getRemovalPercentage());
            phmlrp.semiGreedySolution2();
            phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
            timeSum += phmlrp.getMaxCost();
        }
        System.out.println("semiGreedySolution2: " + timeSum / counter);

        timeSum = 0;
        for (int n = 0; n < counter; n++) {
            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                    params.getRemovalPercentage());
            phmlrp.randomSolution();
            phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
            timeSum += phmlrp.getMaxCost();
        }
        System.out.println("randomSolution: " + timeSum / counter);*/

//        int counter = 0;
//        for (int n = 0; n < 100; n++) {
//            PHMLRP phmlrp = new PHMLRP(params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
//                    params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
//                    params.getRemovalPercentage());
//            phmlrp.randomSolution();
//            phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
//            Operations operations = new Operations(phmlrp);
//
//            for (int i = 0; i < 100000; i++) {
//                if (phmlrp.getMaxCost() < 2652) {
//                    counter++;
//                    break;
//                }
//                operations.localSearchSwap();
////                operations.localSearchSwapHubWithNode();
////                operations.localSearchInsertion();
//            }
//            phmlrp.print(false);
//        }
//        System.out.println("successful count: " + counter);
    }

    private static void applyAlgorithms(Params params, int numHubs, int numVehicles,
                                        XSSFWorkbook workbook, XSSFWorkbook saWorkbook, XSSFWorkbook dpWorkbook) throws IOException {
//        PHMLRP phmlrp = new PHMLRP(params.getDataset(), params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
//                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
//                params.getRemovalPercentage());
//        randomSolutionAndCost(phmlrp);
//
        String sheetName = String.valueOf(params.getNumNodes()) + '.' + params.getNumHubs() + '.' + params.getNumVehicles();
//        XSSFSheet spreadsheet = workbook.createSheet(sheetName);
//        createFirstRow(spreadsheet);
//        DeterministicExplore de = new DeterministicExplore(phmlrp);
//        de.doDeterministicExplore(workbook, spreadsheet);

        // Simulated Annealing
        PHMLRP saPhmlrp = new PHMLRP(params.getDataset(), params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                params.getRemovalPercentage());
        saPhmlrp.setSimulatedAnnealing(true);
        randomSolutionAndCost(saPhmlrp);

        XSSFSheet saSpreadsheet = saWorkbook.createSheet(sheetName);
        createSaFirstRow(saSpreadsheet);
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(saPhmlrp);
        simulatedAnnealing.applySA(saWorkbook, saSpreadsheet);

//        PHMLRP dpPhmlrp = new PHMLRP(10, numHubs, numVehicles,
//                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
//                params.getRemovalPercentage());
//        randomSolutionAndCost(dpPhmlrp);
//
//        XSSFSheet dpSpreadsheet = dpWorkbook.createSheet(sheetName);
//        createDpFirstRow(dpSpreadsheet);
//        DeterministicPermutation deterministicOperation = new DeterministicPermutation(dpPhmlrp);
//        deterministicOperation.deterministicOperationOrder(dpWorkbook, dpSpreadsheet);
    }

    private static void randomSolutionAndCost(PHMLRP phmlrp) {
        InitialSolutions initialSolutions = new InitialSolutions(phmlrp);
        initialSolutions.randomSolution();
        phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
        phmlrp.print(false);
    }

    private static void createFirstRow(XSSFSheet spreadsheet) {
        XSSFRow row = spreadsheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("Operation Name");
        row.createCell(1, CellType.STRING).setCellValue("Run#");
        row.createCell(2, CellType.STRING).setCellValue("Difference");
        row.createCell(3, CellType.STRING).setCellValue("Best Cost");
        row.createCell(4, CellType.STRING).setCellValue("Elapsed Time (nano)");
        row.createCell(5, CellType.STRING).setCellValue("hubs");
        row.createCell(6, CellType.STRING).setCellValue("routes");
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

    private static void createDpFirstRow(XSSFSheet dpSpreadsheet) {
        XSSFRow row = dpSpreadsheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("Solution#");
        row.createCell(1, CellType.STRING).setCellValue("Order");
        row.createCell(2, CellType.STRING).setCellValue("Min Cost");
        row.createCell(3, CellType.STRING).setCellValue("Hubs");
        row.createCell(4, CellType.STRING).setCellValue("Nodes");
    }
}
