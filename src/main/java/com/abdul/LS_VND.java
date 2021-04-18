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

class LS_VND {
    private final Params params;
    private final int replicasPerCombination;

    private final float[][] bestTimes;

    private String[] problemInstances;

    //    0, Insertion
    //    1, Swap
    //    2, HubMove
    //    3, EdgeOpt
    private final List<List<Integer>> combinations;

    private final double[][] bestCosts;
    private final int[][] bestIterations;

    private void getProblemInstancesFromJson() {
        JSONParser parser = new JSONParser();
        try {
            JSONArray arr = (JSONArray) parser.parse(new FileReader("problem_instances.json"));
            List<String> list = new ArrayList<>();
            for (Object probInstance : arr) {
                list.add((String) probInstance);
            }

            problemInstances = new String[list.size()];
            list.toArray(problemInstances);

        } catch (IOException e) {
            System.out.println("Exception: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    LS_VND(Params params) {
        this.params = params;
        getProblemInstancesFromJson();
        combinations = Utils.getCombinations("ls_combinations");

        bestCosts = new double[problemInstances.length][combinations.size()];
        for (double[] arr : bestCosts) {
            Arrays.fill(arr, Integer.MAX_VALUE);
        }

        bestIterations = new int[problemInstances.length][combinations.size()];
        bestTimes = new float[problemInstances.length][combinations.size()];

        replicasPerCombination = params.getNumReplicasPerCombination();
    }

    void runVND() {
        System.out.println("*************************************************");
        System.out.println("Local Search VND");
        doRun();
        writeBCtoExcel();

        System.out.println("*************************************************");
    }

    private void doRun() {
        for (int probIdx = 0; probIdx < problemInstances.length; probIdx++) {
            // for each problem instance
            for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
                int iteration = 0;
                // run on every combination
                for (int repPerCombinationIdx = 0; repPerCombinationIdx < replicasPerCombination; repPerCombinationIdx++) {
                    // run each problem instance n number of replicas

                    long combStartTime = System.nanoTime();

                    PHCRP PHCRP = newPHMLRPInstance(problemInstances[probIdx]);
                    createInitSol(PHCRP);
                    Operations operations = new Operations(PHCRP);
                    iteration++;

                    for (int k : combinations.get(combIdx)) {
                        // for each neighborhood
                        System.out.println(
                                problemInstances[probIdx] +
                                        " " + combIdx +
                                        " " + repPerCombinationIdx +
                                        " " + k);
                        operations.doLocalSearch(k);
                    }

                    double bestCost = PHCRP.getMaxCost();

                    if (bestCost < bestCosts[probIdx][combIdx]) {

                        bestTimes[probIdx][combIdx] = (float) (System.nanoTime() - combStartTime) / 1000;
                        bestCosts[probIdx][combIdx] = bestCost;
                        bestIterations[probIdx][combIdx] = iteration;
                    }
                }
            }
        }
    }

    private void writeBCtoExcel() {
        XSSFWorkbook bcWorkbook = new XSSFWorkbook();

        for (int j = 0; j < bestCosts.length; j++) {
            XSSFSheet bcSheet = bcWorkbook.createSheet(problemInstances[j]);
            XSSFRow row = bcSheet.createRow(0);
            row.createCell(0, CellType.STRING).setCellValue("LS Combination");
            row.createCell(1, CellType.STRING).setCellValue("IterationNumber");
            row.createCell(2, CellType.STRING).setCellValue("bestCost");
            row.createCell(3, CellType.STRING).setCellValue("CPUTime (micro)");

            for (int k = 0; k < bestCosts[j].length; k++) {
                row = bcSheet.createRow(k + 1);
                row.createCell(0, CellType.NUMERIC).setCellValue(
                        Utils.createCombinationStr(combinations.get(k)));
                row.createCell(1, CellType.NUMERIC).setCellValue(bestIterations[j][k]);
                row.createCell(2, CellType.NUMERIC).setCellValue(bestCosts[j][k]);
                row.createCell(3, CellType.NUMERIC).setCellValue(bestTimes[j][k]);
            }
        }

        try {
            Utils.createExcelFile(bcWorkbook,
                    params.getResultPath() + "/LS_VND_" + combinations.size() + "_combinations");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createInitSol(PHCRP PHCRP) {
        InitialSolutions initialSolutions = new InitialSolutions(PHCRP, params.getDataset(),
                params.getCollectionCostCFactor());
        if (params.getInitSol().equals("greedy")) {
            initialSolutions.greedySolution();
        } else {
            initialSolutions.randomSolution();
        }

        PHCRP.calculateCost(PHCRP.CostType.NORMAL);
    }

    private PHCRP newPHMLRPInstance(String problemInstance) {
        return Utils.newPHMLRPInstance(problemInstance, this.params);
    }
}
