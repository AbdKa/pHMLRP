/**
 * GeneralResults (contains best of the best results)
 **/

package com.abdul;

import java.util.Arrays;

class GeneralResults {
    static IS[] initSols;
    static ALGO[] algorithms;
    private static double[] initObjectives;
    static double[] objectives;
    static double[] CPUs;
    static int[] iterations;
    static String[] hubsArr;
    static String[] routesArr;

    private static int numOfRuns;

    GeneralResults(int numOfRuns) {
        GeneralResults.numOfRuns = numOfRuns;
        int arrLength = numOfRuns * Consts.instances.length;
        initSols = new IS[arrLength];
        algorithms = new ALGO[arrLength];
        initObjectives = new double[arrLength];
        Arrays.fill(initObjectives, Double.MAX_VALUE);
        objectives = new double[arrLength];
        Arrays.fill(objectives, Double.MAX_VALUE);
        CPUs = new double[arrLength];
        iterations = new int[arrLength];
        hubsArr = new String[arrLength];
        routesArr = new String[arrLength];
    }

    static int getSolIndex(Params params) {
//        int runIdx = Integer.valueOf(params.getRunNum());
//        int instanceIdx = Integer.valueOf(params.getInstanceIdx());
//        return runIdx * Consts.instances.length + instanceIdx;
        return 0;
    }

    static void setGeneralValues(Params params, double initObj, double minCost, double solCPU,
                                 int bestIteration, String bestHubs, String bestRoutes) {
//        get the index of the current solution
        int solIdx = getSolIndex(params);
        double generalCost = objectives[solIdx];
        if (minCost < generalCost) {
            initSols[solIdx] = params.getInitSol();
            algorithms[solIdx] = params.getAlgorithm();
            initObjectives[solIdx] = initObj;
            objectives[solIdx] = minCost;
            CPUs[solIdx] = solCPU;
            iterations[solIdx] = bestIteration;
            if (bestHubs != null) {
                hubsArr[solIdx] = bestHubs;
                routesArr[solIdx] = bestRoutes;
            }
        }
    }

    static void printFinalResults() {
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet spreadsheet = workbook.createSheet("Final Results");
//        createSaFirstRow(spreadsheet);
//
//        for (int i = 0; i < objectives.length; i++) {
//            XSSFRow row = spreadsheet.createRow(i + 1);
//            row.createCell(0, CellType.NUMERIC).setCellValue(i / Consts.instances.length + 1);
//            row.createCell(1, CellType.STRING).setCellValue(Consts.instances[i % Consts.instances.length]);
//            row.createCell(2, CellType.STRING).setCellValue(initSols[i].toString());
////            row.createCell(2, CellType.STRING).setCellValue(initSols[i] != null ? initSols[i].toString() : "INIT");
//            row.createCell(3, CellType.STRING).setCellValue(algorithms[i].toString());
//            row.createCell(4, CellType.NUMERIC).setCellValue(initObjectives[i]);
//            row.createCell(5, CellType.NUMERIC).setCellValue(objectives[i]);
//            row.createCell(6, CellType.NUMERIC).setCellValue(CPUs[i]);
//            row.createCell(7, CellType.NUMERIC).setCellValue(iterations[i]);
//            row.createCell(8, CellType.STRING).setCellValue(hubsArr[i]);
//            row.createCell(9, CellType.STRING).setCellValue(routesArr[i]);
//        }
//
//        try {
//            Utils.createExcelFile(workbook, "results" + File.separator + "GeneralResults-" +
//                    System.currentTimeMillis());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    private static void createSaFirstRow(XSSFSheet spreadsheet) {
//        XSSFRow row = spreadsheet.createRow(0);
//        row.createCell(0, CellType.STRING).setCellValue("run");
//        row.createCell(1, CellType.STRING).setCellValue("prob");
//        row.createCell(2, CellType.STRING).setCellValue("init sol");
//        row.createCell(3, CellType.STRING).setCellValue("algo");
//        row.createCell(4, CellType.STRING).setCellValue("init obj");
//        row.createCell(5, CellType.STRING).setCellValue("obj");
//        row.createCell(6, CellType.STRING).setCellValue("CPU (seconds)");
//        row.createCell(7, CellType.STRING).setCellValue("best iteration");
//        row.createCell(8, CellType.STRING).setCellValue("hubs");
//        row.createCell(9, CellType.STRING).setCellValue("routes");
//    }

    static void setInitValues(Params params, PHCRP pHCRP) {
        // add initial solution's hubs and routes to the general results
        int solIdx = getSolIndex(params);
        if (pHCRP.getMaxCost() < objectives[solIdx]) {
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
}
