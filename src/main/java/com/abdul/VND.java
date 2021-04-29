package com.abdul;

import java.util.Arrays;
import java.util.List;

class VND {
    private Params params;
    private int runs = 10;
    private int replicasPerProb = 10;
    private int replicasPerCombination = 1;

//    private XSSFWorkbook workbook;
//    private XSSFSheet[] spreadsheets;

    private float[][] timeSum;
//    private long[] timeSumProbs;

    private String[] problemInstances = {
            "TR.10.2.1", "TR.10.2.2", "TR.10.3.1",

//            "TR.15.2.1", "TR.15.2.2",
//            "TR.25.2.1", "TR.25.2.5", "TR.25.5.1", "TR.25.5.2",
//            "TR.50.2.1", "TR.50.2.5", "TR.50.5.1", "TR.50.5.2",
//            "TR.81.2.1", "TR.81.2.5", "TR.81.5.1", "TR.81.5.2",
//            "TR.81.9.1", "TR.81.9.2", "TR.81.9.3", "TR.81.9.4", "TR.81.9.5",
//            "AP100.100.5.1", "AP100.100.5.2", "AP100.100.5.5",
//            "AP200.200.10.1", "AP200.200.10.2", "AP200.200.10.5"
    };

    //    0, insertNodeBetweenRoutes
    //    1, edgeOptWithinRoutes
    //    2, insertTwoNodes
    //    3, twoOptAlgorithm
    //    4, insertNodeInRoute
    //    5, swapNodeInRoute
    //    6, swapNodeWithinRoutes
    //    7, nodesRemoveAndGreedyInsert
    //    8, swapHubWithNode
    private List<List<Integer>> combinations;

//    private List<List<List<List<PHCRP>>>> bestReplicas = new ArrayList<>(problemInstances.length);

    private double[][] bestCosts;
    private PHCRP[][] bestSolutions;

    private final boolean silent;

    VND(Params params) {
        this.params = params;
        this.silent = params.getSilent();
        combinations = Utils.getCombinations("Combinations");

        bestSolutions = new PHCRP[problemInstances.length][combinations.size()];
        bestCosts = new double[problemInstances.length][combinations.size()];
        for (double[] arr : bestCosts) {
            Arrays.fill(arr, Integer.MAX_VALUE);
        }

        timeSum = new float[problemInstances.length][combinations.size()];
//        timeSumProbs = new long[problemInstances.length];
        // excel operations
        resetWorkbook();
    }

    void runVND() {
        long sum = 0;
        for (int i = 0; i < runs; i++) {
//            Run run = new Run(params, replicasPerProb, replicasPerCombination, spreadsheets, timeSum,
//                    timeSumProbs, problemInstances, combinations, bestReplicas);
//            run.start();

            long time = System.currentTimeMillis();
            doRun(i);
            sum += System.currentTimeMillis() - time;

            /*if ((i + 1) % 50 == 0) {
                try {
                    Utils.createCSVFile(workbook,
                            "results/VND_Best_combination" + (i - 49) + "_" + i);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                resetWorkbook();
            }*/
        }

        applyLocalSearch();

        writeBCtoExcel();

        System.out.println("A run average runtime: " + (sum / 1000) / runs);

//        for (int i = 0; i < runs; i++) {
//            System.out.println(i + " " + timeSum[i]/1000);
//        }

//        for (int i = 0; i < problemInstances.length; i++) {
//            System.out.println(problemInstances[i] + " " + timeSumProbs[i]);
//        }
    }

    private void resetWorkbook() {
//        workbook = new XSSFWorkbook();
//        spreadsheets = new XSSFSheet[problemInstances.length];
//
//        for (int i = 0; i < problemInstances.length; i++) {
//            spreadsheets[i] = workbook.createSheet(problemInstances[i]);
//            XSSFRow row = spreadsheets[i].createRow(0);
//            for (int j = 0; j < combinations.size(); j++) {
//                row.createCell(j, CellType.NUMERIC).setCellValue(j);
//            }
//        }
    }

    private void doRun(int i) {
//        for (int probIdx = 0; probIdx < problemInstances.length; probIdx++) {
//            // for each problem instance
////            long probStartTime = System.nanoTime();
////            List<List<List<PHCRP>>> repPerProbList = new ArrayList<>(replicasPerProb);
//            for (int replicaIdx = 0; replicaIdx < replicasPerProb; replicaIdx++) {
//                // run each problem instance n number of replicas
////                List<List<PHCRP>> combList = new ArrayList<>(combinations.size());
//                XSSFRow[] rows = new XSSFRow[replicasPerCombination];
//                for (int row = 0; row < rows.length; row++) {
////                            int rowNum = row + (i * replicaIdx * replicasPerCombination) + 1;
//                    int rowNum = spreadsheets[probIdx].getLastRowNum() + 1;
//                    if (!silent)
//                        System.out.println(rowNum);
//                    rows[row] = spreadsheets[probIdx].createRow(rowNum);
//                }
//
//                for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
//                    // run on every combination
////                    List<PHCRP> repPerCombList = new ArrayList<>(replicasPerCombination);
//                    for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination; repPerCombinationIdx++) {
//                        // run each problem instance n number of replicas
//
//                        long combStartTime = System.nanoTime();
//
//                        PHCRP PHCRP = newPHMLRPInstance(problemInstances[probIdx]);
//                        PHCRP.setSilent(silent);
//                        createInitSol(PHCRP);
//                        for (int k : combinations.get(combIdx)) {
//                            // for each neighborhood
//                            if (!silent)
//                                System.out.println(i +
//                                        " " + problemInstances[probIdx] +
//                                        " " + replicaIdx +
//                                        " " + combIdx +
//                                        " " + repPerCombinationIdx +
//                                        " " + k);
//
//                            while (true) {
//                                // change neighborhood until no better solution, jump to next one
//                                if (!PHCRP.callOperation(k)) {
//                                    // if doesn't give a better solution, break and jump to next neighborhood
//                                    break;
//                                }
//                            }
//                        }
//
//                        double bestCost = PHCRP.getMaxCost();
//                        rows[repPerCombinationIdx].createCell(combIdx, CellType.NUMERIC).setCellValue(bestCost);
//
//                        if (bestCost < bestCosts[probIdx][combIdx]) {
//                            long diff = System.nanoTime() - combStartTime;
//                            timeSum[probIdx][combIdx] = diff / 1000;
//                            bestCosts[probIdx][combIdx] = bestCost;
//
//                            bestSolutions[probIdx][combIdx] = PHCRP;
//                        }
//
////                        repPerCombList.add(PHCRP);
//                    }
//
////                    combList.add(repPerCombList);
//                }
//
////                repPerProbList.add(combList);
//            }
//
////            long diffProb = System.nanoTime() - probStartTime;
////            timeSumProbs[probIdx] += diffProb / 1000;
//
////            bestReplicas.add(repPerProbList);
//        }
    }

    private void writeBCtoExcel() {
//        XSSFWorkbook bcWorkbook = new XSSFWorkbook();
//        XSSFSheet bcSheet = bcWorkbook.createSheet("Best Costs");
//
//        for (int j = 0; j < bestCosts.length; j++) {
//            XSSFRow row0 = null;
//            if (j == 0) {
//                row0 = bcSheet.createRow(j);
//            }
//            XSSFRow row = bcSheet.createRow(j + 1);
//            row.createCell(0, CellType.NUMERIC).setCellValue(problemInstances[j]);
//            for (int k = 0; k < bestCosts[j].length; k++) {
//                if (j == 0) {
//                    row0.createCell(k + 1, CellType.NUMERIC).setCellValue(k);
////                    row0.createCell((k * 2)+1, CellType.NUMERIC).setCellValue(k);
////                    row0.createCell((k * 2)+2, CellType.STRING).setCellValue("T (Micro Sec)");
//                }
//                row.createCell(k + 1, CellType.NUMERIC).setCellValue(bestCosts[j][k]);
////                row.createCell((k * 2)+1, CellType.NUMERIC).setCellValue(bestCosts[j][k]);
////                row.createCell((k * 2)+2, CellType.NUMERIC).setCellValue(timeSum[j][k]);
//            }
//        }
//
//        try {
//            Utils.createExcelFile(bcWorkbook,
//                    "results/VND_Best_Costs");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void applyLocalSearch() {
//        XSSFWorkbook bcWorkbook = new XSSFWorkbook();
//        XSSFSheet bcSheet = bcWorkbook.createSheet("Best Costs");
//
//        for (int i = 0; i < bestSolutions.length; i++) {
//            XSSFRow row0 = null;
//            if (i == 0) {
//                row0 = bcSheet.createRow(0);
//            }
//            XSSFRow row = bcSheet.createRow(i + 1);
//            row.createCell(0, CellType.STRING).setCellValue(problemInstances[i]);
//            for (int k = 0; k < bestSolutions[i].length; k++) {
//                if (i == 0) {
//                    row0.createCell((k * 5) + 1, CellType.STRING).setCellValue("Before LS");
//                    row0.createCell((k * 5) + 2, CellType.STRING).setCellValue("Insertion LS");
//                    row0.createCell((k * 5) + 3, CellType.STRING).setCellValue("Swap LS");
//                    row0.createCell((k * 5) + 4, CellType.STRING).setCellValue("SwapHub LS");
//                    row0.createCell((k * 5) + 5, CellType.BLANK);
//                }
//
//                row.createCell((k * 5) + 1, CellType.NUMERIC).setCellValue(bestCosts[i][k]);
//
//                Operations operations = new Operations(bestSolutions[i][k]);
//
//                if (!silent)
//                    System.out.println("Insertion LS");
//                operations.localSearchInsertion();
//                row.createCell((k * 5) + 2, CellType.NUMERIC).setCellValue(bestSolutions[i][k].getMaxCost());
//
//                if (!silent)
//                    System.out.println("Swap LS");
//                operations.localSearchSwap();
//                row.createCell((k * 5) + 3, CellType.NUMERIC).setCellValue(bestSolutions[i][k].getMaxCost());
//
//                if (!silent)
//                    System.out.println("SwapHub LS");
//                operations.localSearchSwapHubWithNode();
//                row.createCell((k * 5) + 4, CellType.NUMERIC).setCellValue(bestSolutions[i][k].getMaxCost());
//
//                row.createCell((k * 5) + 5, CellType.BLANK);
//            }
//        }
//
//        try {
//            Utils.createExcelFile(bcWorkbook,
//                    "results/VND_With_LS");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void createInitSol(PHCRP pHCRP) {
        InitialSolutions initialSolutions = new InitialSolutions(pHCRP, params.getDataset(),
                params.getCollectionCostCFactor());
        initialSolutions.randomSolution();
        pHCRP.calculateCost(PHCRP.CostType.NORMAL);
    }

    private PHCRP newPHMLRPInstance(String problemInstance) {
        return Utils.newPHMLRPInstance(problemInstance, this.params);
    }
}
