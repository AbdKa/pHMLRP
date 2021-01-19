package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.List;

public class Run extends Thread {

    private Params params;
    private int replicasPerProb = 1;
    private int replicasPerCombination = 100;

    private XSSFSheet[] spreadsheets;

    private long[] timeSum;
    private long[] timeSumProbs;

    private String[] problemInstances;

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

    private List<List<List<List<PHMLRP>>>> bestReplicas;

    Run(
            Params params,
            int replicasPerProb,
            int replicasPerCombination,
            XSSFSheet[] spreadsheets,
            long[] timeSum,
            long[] timeSumProbs,
            String[] problemInstances,
            List<List<Integer>> combinations,
            List<List<List<List<PHMLRP>>>> bestReplicas) {
        this.params = params;
        this.replicasPerProb = replicasPerProb;
        this.replicasPerCombination = replicasPerCombination;
        this.spreadsheets = spreadsheets;
        this.timeSum = timeSum;
        this.timeSumProbs = timeSumProbs;
        this.problemInstances = problemInstances;
        this.combinations = combinations;
        this.bestReplicas = bestReplicas;
    }

    @Override
    public void run() {
        // Displaying the thread that is running
        System.out.println("Thread " +
                Thread.currentThread().getId() +
                " is running");
        doRun();
    }

    private void doRun() {
        for (int probIdx = 0; probIdx < problemInstances.length; probIdx++) {
            // for each problem instance
            long probStartTime = System.nanoTime();
            List<List<List<PHMLRP>>> repPerProbList = new ArrayList<>(replicasPerProb);
            for (int replicaIdx = 0; replicaIdx < replicasPerProb; replicaIdx++) {
                // run each problem instance n number of replicas
                List<List<PHMLRP>> combList = new ArrayList<>(replicasPerCombination);
                for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination; repPerCombinationIdx++) {
                    // run each problem instance n number of replicas
                    List<PHMLRP> repPerCombList = new ArrayList<>(combinations.size());

                    XSSFRow row;
                    synchronized (this) {
                        int rowNum = spreadsheets[probIdx].getLastRowNum() + 1;
                        System.out.println("rowNum " + rowNum);
                        row = spreadsheets[probIdx].createRow(rowNum);
                    }
                    /*XSSFRow[] rows = new XSSFRow[replicasPerCombination];
                    for (int row = 0; row < rows.length; row++) {
                        int rowNum = spreadsheets[probIdx].getLastRowNum() + 1;
                        rows[row] = spreadsheets[probIdx].createRow(rowNum);
                    }*/

                    for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
                        // run on every combination
                        PHMLRP phmlrp = newPHMLRPInstance(problemInstances[probIdx]);
                        createInitSol(phmlrp);
                        for (int k : combinations.get(combIdx)) {
                            // for each neighborhood
                            System.out.println("Thread " +
                                    Thread.currentThread().getId() +
                                    " " + problemInstances[probIdx] +
                                    " " + replicaIdx +
                                    " " + combIdx +
                                    " " + repPerCombinationIdx +
                                    " " + k);

                            long time = System.nanoTime();
                            while (true) {
                                // change neighborhood until no better solution, jump to next one
                                if (!phmlrp.callOperation(k)) {
                                    // if doesn't give a better solution, break and jump to next neighborhood
                                    break;
                                }
                            }
                            long diff = System.nanoTime() - time;
//                            timeSum[k] += diff / 1000;
                        }

                        double bestCost = phmlrp.getMaxCost();
                        row.createCell(combIdx, CellType.NUMERIC).setCellValue(bestCost);

                        repPerCombList.add(phmlrp);
                    }

                    combList.add(repPerCombList);
                }

                repPerProbList.add(combList);
            }

            long diffProb = System.nanoTime() - probStartTime;
            timeSumProbs[probIdx] += diffProb / 1000;

            bestReplicas.add(repPerProbList);
        }
    }

    public XSSFSheet[] getSpreadsheets() {
        return spreadsheets;
    }

    private void createInitSol(PHMLRP phmlrp) {
        InitialSolutions initialSolutions = new InitialSolutions(phmlrp);
        initialSolutions.randomSolution();
        phmlrp.calculateCost(PHMLRP.CostType.NORMAL);
    }

    private PHMLRP newPHMLRPInstance(String problemInstance) {
        return new PHMLRP(
                DS.valueOf(problemInstance.split("\\.")[0]),
                Integer.valueOf(problemInstance.split("\\.")[1]),
                Integer.valueOf(problemInstance.split("\\.")[2]),
                Integer.valueOf(problemInstance.split("\\.")[3]),
                params.getCollectionCostCFactor(),
                params.getDistributionCostCFactor(),
                params.getHubToHubCFactor(),
                params.getRemovalPercentage());
    }
}
