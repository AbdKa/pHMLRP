package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class VndWithIncompleteHubs {
    private Params params;
    private final String resultPath;
    private int runs = 10_000;
    private int replicasPerProb = 1;
    private int replicasPerCombination = 1;

    private XSSFWorkbook workbook;
    private XSSFSheet spreadsheet;

    // problem instance: dataset.n.h.v.q
    private String[] problemInstances;
//            "TR.10.2.1.1", "TR.10.2.2.1", "TR.10.3.1.2",
//            "TR16.16.2.1.1", "TR16.10.2.2.1", "TR16.10.3.1.2",

//            "TR.15.2.1", "TR.15.2.2",
//            "TR.25.2.1", "TR.25.2.5", "TR.25.5.1", "TR.25.5.2",
//            "TR.50.2.1", "TR.50.2.5", "TR.50.5.1", "TR.50.5.2",
//            "TR.81.2.1", "TR.81.2.5", "TR.81.5.1", "TR.81.5.2",
//            "TR.81.9.1", "TR.81.9.2", "TR.81.9.3", "TR.81.9.4", "TR.81.9.5",
//    };

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

    int iterationCounts[];
    private double[] bestCosts;
    private float[] bestTimes;
    private int[] bestIterationNumber;
    private String[] bestLinks;
    private String[] bestRoutes;

    VndWithIncompleteHubs(Params params, String resultPath) {
        getProblemInstancesFromJson();
        this.params = params;
        combinations = Utils.getCombinations();
        runs = params.getNumRuns();

        iterationCounts = new int[problemInstances.length];
        Arrays.fill(iterationCounts, 0);
        bestCosts = new double[problemInstances.length];
        Arrays.fill(bestCosts, Integer.MAX_VALUE);
        bestTimes = new float[problemInstances.length];
        bestIterationNumber = new int[problemInstances.length];
        bestLinks = new String[problemInstances.length];
        bestRoutes = new String[problemInstances.length];

        this.resultPath = resultPath;
    }

    private void getProblemInstancesFromJson() {
        JSONParser parser = new JSONParser();
        try {
            JSONArray arr = (JSONArray) parser.parse(new FileReader("problem_instances.json"));
            List<String> list = new ArrayList<>();
            for (Object probInstance : arr) {
                list.add((String)probInstance);
            }

            problemInstances = new String[list.size()];
            list.toArray(problemInstances);

        } catch (IOException e) {
            System.out.println("Exception: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void runVND() {
        for (int i = 0; i < runs; i++) {
            doRun(i);
        }

        writeBCtoExcel();
    }

    private void doRun(int i) {
        for (int probIdx = 0; probIdx < problemInstances.length; probIdx++) {
            // for each problem instance
            for (int replicaIdx = 0; replicaIdx < replicasPerProb; replicaIdx++) {
                // run each problem instance n number of replicas
                for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
                    // run on every combination
                    for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination; repPerCombinationIdx++) {
                        // run each problem instance n number of replicas
                        long combStartTime = System.nanoTime();
                        iterationCounts[probIdx]++;
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

                            while (true) {
                                // change neighborhood until no better solution, jump to next one
                                if (!phmlrp.callOperation(k)) {
                                    // if doesn't give a better solution, break and jump to next neighborhood
                                    break;
                                }
                            }
                        }

                        double bestCost = phmlrp.getMaxCost();

                        if (bestCost < bestCosts[probIdx]) {
                            long diff = System.nanoTime() - combStartTime;
                            bestCosts[probIdx] = bestCost;
                            bestTimes[probIdx] = diff / 1000;
                            bestIterationNumber[probIdx] = iterationCounts[probIdx];
                            bestRoutes[probIdx] = phmlrp.getRoutes();

                            int numLinks = Integer.parseInt(problemInstances[probIdx].split("\\.")[4]);
                            IncompleteHubs incompleteHubs = new IncompleteHubs(phmlrp, "", numLinks);
                            incompleteHubs.runIncomplete();
                            bestLinks[probIdx] = String.join("; ", incompleteHubs.getLinks());
                        }
                    }
                }
            }
        }
    }

    private void writeBCtoExcel() {
        XSSFWorkbook bcWorkbook = new XSSFWorkbook();
        XSSFSheet bcSheet = bcWorkbook.createSheet("Best Iterations");

        XSSFRow row = bcSheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("problemInstance");
        row.createCell(1, CellType.STRING).setCellValue("bestCost");
        row.createCell(2, CellType.STRING).setCellValue("CPUTime");
        row.createCell(3, CellType.STRING).setCellValue("IterationNumber");
        row.createCell(4, CellType.STRING).setCellValue("Links");
        row.createCell(5, CellType.STRING).setCellValue("Routes");

        for (int i = 0; i < problemInstances.length; i++) {
            row = bcSheet.createRow(i + 1);
            row.createCell(0, CellType.STRING).setCellValue(problemInstances[i]);
            row.createCell(1, CellType.NUMERIC).setCellValue(bestCosts[i]);
            row.createCell(2, CellType.NUMERIC).setCellValue(bestTimes[i]);
            row.createCell(3, CellType.NUMERIC).setCellValue(bestIterationNumber[i]);
            row.createCell(4, CellType.STRING).setCellValue(bestLinks[i]);
            row.createCell(5, CellType.STRING).setCellValue(bestRoutes[i]);
        }

        try {
            Utils.createExcelFile(bcWorkbook, resultPath + "/results");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createInitSol(PHMLRP phmlrp) {
        InitialSolutions initialSolutions = new InitialSolutions(phmlrp);
        switch (params.getInitSol()) {
            case "greedy":
                initialSolutions.greedySolution();
                break;
            default:
                initialSolutions.randomSolution();
        }
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
