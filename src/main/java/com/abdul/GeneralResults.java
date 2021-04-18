package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.Arrays;

class GeneralResults {
    static IS[] initials;
    static ALGO[] algorithms;
    static double[] objectives;
    static double[] CPUs;
    static int[] iterations;
    static String[] hubsArr;
    static String[] routesArr;

    GeneralResults(int numOfRuns) {
//        int arrLength = numOfRuns * Consts.instances.length * IS.values().length * ALGO.values().length;
        int arrLength = numOfRuns * Consts.instances.length;
        initials = new IS[arrLength];
        algorithms = new ALGO[arrLength];
        objectives = new double[arrLength];
        Arrays.fill(objectives, Double.MAX_VALUE);
        CPUs = new double[arrLength];
        iterations = new int[arrLength];
        hubsArr = new String[arrLength];
        routesArr = new String[arrLength];
    }

    static int getIndex(Params params) {
        int runIdx = Integer.valueOf(params.getRunNum());
        int instanceIdx = Integer.valueOf(params.getInstanceIdx());
//        int initSolIdx = params.getInitSol().ordinal();
//        int algoIdx = params.getAlgorithm().ordinal();
        return runIdx * Consts.instances.length + instanceIdx;
//        return runIdx * Consts.instances.length * IS.values().length * ALGO.values().length +
//                instanceIdx * IS.values().length * ALGO.values().length +
//                initSolIdx * ALGO.values().length +
//                algoIdx;
    }

    static void printFinalResults() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Final Results");
        createSaFirstRow(spreadsheet);

        for (int i = 0; i < objectives.length; i++) {
            XSSFRow row = spreadsheet.createRow(i + 1);
            row.createCell(0, CellType.NUMERIC).setCellValue(i / Consts.instances.length + 1);
            row.createCell(1, CellType.STRING).setCellValue(Consts.instances[i % Consts.instances.length]);
            row.createCell(2, CellType.STRING).setCellValue(initials[i].toString());
            row.createCell(3, CellType.STRING).setCellValue(algorithms[i].toString());
            row.createCell(4, CellType.NUMERIC).setCellValue(objectives[i]);
            row.createCell(5, CellType.NUMERIC).setCellValue(CPUs[i]);
            row.createCell(6, CellType.NUMERIC).setCellValue(iterations[i]);
            row.createCell(7, CellType.STRING).setCellValue(hubsArr[i]);
            row.createCell(8, CellType.STRING).setCellValue(routesArr[i]);
        }

        try {
            Utils.createExcelFile(workbook, "results/" + "generalResults");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createSaFirstRow(XSSFSheet spreadsheet) {
        XSSFRow row = spreadsheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("run");
        row.createCell(1, CellType.STRING).setCellValue("prob");
        row.createCell(2, CellType.STRING).setCellValue("initSol");
        row.createCell(3, CellType.STRING).setCellValue("algo");
        row.createCell(4, CellType.STRING).setCellValue("obj");
        row.createCell(5, CellType.STRING).setCellValue("CPU (ms)");
        row.createCell(6, CellType.STRING).setCellValue("iteration");
        row.createCell(7, CellType.STRING).setCellValue("hubs");
        row.createCell(8, CellType.STRING).setCellValue("routes");
    }
}
