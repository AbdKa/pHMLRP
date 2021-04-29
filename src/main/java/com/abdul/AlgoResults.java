/**
 * AlgoResults (contains best results)
 **/

package com.abdul;

import java.io.File;
import java.io.IOException;

class AlgoResults {
    static IS[] initSols;
    static ALGO[] algorithms;
    static double[] initObjectives;
    static double[] objectives;
    static double[] CPUs;
    static int[] iterations;
    static String[] hubsArr;
    static String[] routesArr;

    private static int numOfRuns;

    AlgoResults(int numOfRuns) {
        AlgoResults.numOfRuns = numOfRuns;
        int arrLength = numOfRuns * Consts.instances.length * IS.values().length * ALGO.values().length;
        initSols = new IS[arrLength];
        algorithms = new ALGO[arrLength];
        initObjectives = new double[arrLength];
        objectives = new double[arrLength];
        CPUs = new double[arrLength];
        iterations = new int[arrLength];
        hubsArr = new String[arrLength];
        routesArr = new String[arrLength];
    }

    static int getSolIndex(Params params) {
//        int runIdx = Integer.valueOf(params.getRunNum());
//        int instanceIdx = Integer.valueOf(params.getInstanceIdx());
//        int initSolIdx = params.getInitSol().ordinal();
//        int algoIdx = params.getAlgorithm().ordinal();
//        return runIdx * Consts.instances.length * IS.values().length * ALGO.values().length +
//                instanceIdx * IS.values().length * ALGO.values().length +
//                initSolIdx * ALGO.values().length +
//                algoIdx;
        return 0;
    }

    static void setAlgoValues(Params params, double minCost, double solCPU,
                              int bestIteration, String bestHubs, String bestRoutes) {
//        get the index of the current solution
        int solIdx = getSolIndex(params);

        initSols[solIdx] = params.getInitSol();
        algorithms[solIdx] = params.getAlgorithm();
//        initObjectives[solIdx] = initObj;
        objectives[solIdx] = minCost;
        CPUs[solIdx] = solCPU;
        iterations[solIdx] = bestIteration;
        if (bestHubs != null) {
            hubsArr[solIdx] = bestHubs;
            routesArr[solIdx] = bestRoutes;
        }
    }

    static void printAlgoResults() {
//        XSSFWorkbook workbook = new XSSFWorkbook();
////        initialize spreed sheets
//        XSSFSheet[] spreadsheet = new XSSFSheet[ALGO.values().length];
//        for (ALGO algo : ALGO.values()) {
//            spreadsheet[algo.ordinal()] = workbook.createSheet(algo.toString());
//            createFirstRow(spreadsheet[algo.ordinal()]);
//        }
//
//        for (int i = 0; i < objectives.length; i++) {
//            int lastRowIdx = spreadsheet[algorithms[i].ordinal()].getLastRowNum();
//            XSSFRow row = spreadsheet[algorithms[i].ordinal()].createRow(lastRowIdx + 1);
//            int currentRun = ((i / (Consts.instances.length * IS.values().length * ALGO.values().length)) % numOfRuns) + 1;
//            row.createCell(0, CellType.NUMERIC).setCellValue(currentRun);
//            int instanceIdx = (i / (IS.values().length * ALGO.values().length)) % Consts.instances.length;
//            row.createCell(1, CellType.STRING).setCellValue(Consts.instances[instanceIdx]);
//            row.createCell(2, CellType.STRING).setCellValue(initSols[i].toString());
//            row.createCell(3, CellType.NUMERIC).setCellValue(initObjectives[i]);
//            row.createCell(4, CellType.NUMERIC).setCellValue(objectives[i]);
//            row.createCell(5, CellType.NUMERIC).setCellValue(CPUs[i]);
//            row.createCell(6, CellType.NUMERIC).setCellValue(iterations[i]);
//            row.createCell(7, CellType.STRING).setCellValue(hubsArr[i]);
//            row.createCell(8, CellType.STRING).setCellValue(routesArr[i]);
//        }
//
//        try {
//            Utils.createExcelFile(workbook, "results" + File.separator + "AlgoResults-" +
//                    System.currentTimeMillis());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    private static void createFirstRow(XSSFSheet spreadsheet) {
//        XSSFRow row = spreadsheet.createRow(0);
//        row.createCell(0, CellType.STRING).setCellValue("run");
//        row.createCell(1, CellType.STRING).setCellValue("prob");
//        row.createCell(2, CellType.STRING).setCellValue("init sol");
//        row.createCell(3, CellType.STRING).setCellValue("init obj");
//        row.createCell(4, CellType.STRING).setCellValue("obj");
//        row.createCell(5, CellType.STRING).setCellValue("CPU (seconds)");
//        row.createCell(6, CellType.STRING).setCellValue("best iteration");
//        row.createCell(7, CellType.STRING).setCellValue("hubs");
//        row.createCell(8, CellType.STRING).setCellValue("routes");
//    }

    static void setInitValues(Params params, PHCRP pHCRP) {
        // add initial solution's values to the results arrays
        int solIdx = getSolIndex(params);
        initSols[solIdx] = params.getInitSol();
        algorithms[solIdx] = params.getAlgorithm();
        initObjectives[solIdx] = pHCRP.getMaxCost();
        objectives[solIdx] = pHCRP.getMaxCost();
        CPUs[solIdx] = pHCRP.getInitCPU();
        iterations[solIdx] = 0;
        hubsArr[solIdx] = pHCRP.getHubsString();
        routesArr[solIdx] = pHCRP.getVehiclesListString();
    }
}
