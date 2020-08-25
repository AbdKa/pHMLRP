package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class VND {
    private Params params;
    private int runs = 1000;
    private int replicasPerProb = 10;
    private int replicasPerCombination = 30;

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet[] spreadsheets;

//    private long[] timeSum;
//    private long[] timeSumProbs;

    private String[] problemInstances = {
            "TR.10.2.1", "TR.10.2.2", "TR.10.3.1",
            "TR.15.2.1", "TR.15.2.2",
            "TR.25.2.1", "TR.25.2.5", "TR.25.5.1", "TR.25.5.2",
            "TR.50.2.1", "TR.50.2.5", "TR.50.5.1", "TR.50.5.2",
//            "TR.81.2.1", "TR.81.2.5", "TR.81.5.1", "TR.81.5.2",
//            "TR.81.9.1", "TR.81.9.2", "TR.81.9.3", "TR.81.9.4", "TR.81.9.5",
//            "AP100.100.5.1", "AP100.100.5.2", "AP100.100.5.5",
//            "AP200.200.10.1", "AP200.200.10.2", "AP200.200.10.5"
    };

    //    0, insertNodeBetweenRoutes
    //    1, edgeOpt
    //    2, insertTwoNodes
    //    3, twoOptAlgorithm
    //    4, insertNodeInRoute
    //    5, swapNodeInRoute
    //    6, swapNodeWithinRoutes
    //    7, nodesRemoveAndGreedyInsert
    //    8, swapHubWithNode
    private List<List<Integer>> combinations;

//    private List<List<List<List<PHMLRP>>>> bestReplicas = new ArrayList<>(problemInstances.length);

    VND(Params params) {
        this.params = params;
        combinations = Utils.getCombinations();
        // TODO: remove time sums
//        timeSum = new long[runs];
//        timeSumProbs = new long[problemInstances.length];
        // excel operations
        spreadsheets = new XSSFSheet[problemInstances.length];

        for (int i = 0; i < problemInstances.length; i++) {
            spreadsheets[i] = workbook.createSheet(problemInstances[i]);
            XSSFRow row = spreadsheets[i].createRow(0);
            for (int j = 0; j < combinations.size(); j++) {
                row.createCell(j, CellType.NUMERIC).setCellValue(j);
            }
        }
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
        }

        try {
            Utils.createExcelFile(workbook,
                    "VND_Best_combination");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("A run average runtime: " + (sum/1000)/runs);

//        for (int i = 0; i < runs; i++) {
//            System.out.println(i + " " + timeSum[i]/1000);
//        }

//        for (int i = 0; i < problemInstances.length; i++) {
//            System.out.println(problemInstances[i] + " " + timeSumProbs[i]);
//        }
    }

    private void doRun(int i) {
        for (int probIdx = 0; probIdx < problemInstances.length; probIdx++) {
            // for each problem instance
//            long probStartTime = System.nanoTime();
//            List<List<List<PHMLRP>>> repPerProbList = new ArrayList<>(replicasPerProb);
            for (int replicaIdx = 0; replicaIdx < replicasPerProb; replicaIdx++) {
                // run each problem instance n number of replicas
//                List<List<PHMLRP>> combList = new ArrayList<>(combinations.size());
                XSSFRow[] rows = new XSSFRow[replicasPerCombination];
                for (int row = 0; row < rows.length; row++) {
//                            int rowNum = row + (i * replicaIdx * replicasPerCombination) + 1;
                    int rowNum = spreadsheets[probIdx].getLastRowNum() + 1;
                    System.out.println(rowNum);
                    rows[row] = spreadsheets[probIdx].createRow(rowNum);
                }

                for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
                    // run on every combination
//                    List<PHMLRP> repPerCombList = new ArrayList<>(replicasPerCombination);
                    for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination; repPerCombinationIdx++) {
                        // run each problem instance n number of replicas
                        PHMLRP phmlrp = newPHMLRPInstance(problemInstances[probIdx]);
                        createInitSol(phmlrp);
                        for (int k : combinations.get(combIdx)) {
                            // for each neighborhood
                                System.out.println(i +
                                        " " + problemInstances[probIdx] +
                                        " " + replicaIdx +
                                        " " + combIdx +
                                        " " + repPerCombinationIdx +
                                        " " + k);

//                            long time = System.nanoTime();
                            while (true) {
                                // change neighborhood until no better solution, jump to next one
                                if (!phmlrp.callOperation(k)) {
                                    // if doesn't give a better solution, break and jump to next neighborhood
                                    break;
                                }
                            }

//                            long diff = System.nanoTime() - time;
//                            timeSum[k] += diff / 1000;
                        }

                        double bestCost = phmlrp.getMaxCost();
                        rows[repPerCombinationIdx].createCell(combIdx, CellType.NUMERIC).setCellValue(bestCost);

//                        repPerCombList.add(phmlrp);
                    }

//                    combList.add(repPerCombList);
                }

//                repPerProbList.add(combList);
            }

//            long diffProb = System.nanoTime() - probStartTime;
//            timeSumProbs[probIdx] += diffProb / 1000;

//            bestReplicas.add(repPerProbList);
        }
    }

    private void createInitSol(PHMLRP phmlrp) {
//        InitialSolutions initialSolutions = new InitialSolutions(
//                phmlrp,
//                phmlrp.getNumNodes(),
//                phmlrp.getNumHubs(),
//                phmlrp.getNumVehiclesPerHub());
//        initialSolutions.probabilisticInitSol();
        phmlrp.randomSolution();
        phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
    }

    private PHMLRP newPHMLRPInstance(String problemInstance) {
        return new PHMLRP(
                problemInstance.split("\\.")[0],
                Integer.valueOf(problemInstance.split("\\.")[1]),
                Integer.valueOf(problemInstance.split("\\.")[2]),
                Integer.valueOf(problemInstance.split("\\.")[3]),
                params.getCollectionCostCFactor(),
                params.getDistributionCostCFactor(),
                params.getHubToHubCFactor(),
                params.getRemovalPercentage());
    }
}
