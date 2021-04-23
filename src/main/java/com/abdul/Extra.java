package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.Arrays;

class Extra {
    static void getGreedyHubs(Params params) {
        String[] instances = Consts.instances;
        String[] hubs = new String[instances.length];
        long[] CPUs = new long[instances.length];
        for (int i = 0; i <instances.length; i++) {
            long time = System.currentTimeMillis();
            PHCRP PHCRP = Utils.newPHMLRPInstance(instances[i], params);
            InitialSolutions initialSolutions = new InitialSolutions(PHCRP, params.getDataset(),
                    params.getCollectionCostCFactor());
            initialSolutions.greedyPickHubs();
            StringBuilder hubsSB = new StringBuilder();
            Arrays.sort(PHCRP.getHubsArr());
            for (int hub : PHCRP.getHubsArr()) {
                hubsSB.append(hub).append(", ");
            }
            hubs[i] = hubsSB.toString();
            CPUs[i] = System.currentTimeMillis() - time;
        }

        printToExcel(params, instances, hubs, CPUs);
    }

    private static void printToExcel(Params params, String[] instances, String[] hubs, long[] CPUs) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Greedy_Hubs");
        XSSFRow row = spreadsheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("Instance");
        row.createCell(1, CellType.STRING).setCellValue("Hubs");
        row.createCell(2, CellType.STRING).setCellValue("CPU (millisecond)");

        for (int i = 0; i < instances.length; i++) {
            row = spreadsheet.createRow(i + 1);
            row.createCell(0, CellType.STRING).setCellValue(instances[i]);
            row.createCell(1, CellType.STRING).setCellValue(hubs[i]);
            row.createCell(2, CellType.NUMERIC).setCellValue(CPUs[i]);
        }

        try {
            Utils.createExcelFile(workbook, params.getResultPath() + "/" + "Greedy_Hubs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    print objective functions for gurobi json files
    //            String jsonPrefix = params.getInitSol() == IS.GRB ? "GRB" : "HUBS_GRB";
//            String instance = jsonPrefix + "_" +
//                    params.getDataset().toString() + "_" +
//                    params.getNumNodes() + "_" +
//                    params.getNumHubs() + "_" +
//                    params.getNumVehicles();
//            System.out.format("%25s%25s%n", instance, pHCRP.getMaxCost());
}
