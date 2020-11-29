package com.abdul;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class IncompleteHubs {

    private PHMLRP phmlrp;
    private int numHubs;
    private List<Integer> hubsList;
    private int middleHubIdx = -1;
    private String path;
    private int numLinks;
    private int linksCount = 0;

    private List<Double> hubsMaxCosts;
    private double maxCost = 0;

//    private XSSFWorkbook workbook;
//    private XSSFSheet spreadsheet;
    private int sheetRowCount = 0;

    private String[] links;

    IncompleteHubs(PHMLRP phmlrp, String resultPath, int numLinks) {
        this.phmlrp = phmlrp;
        numHubs = phmlrp.getNumHubs();
        this.numLinks = numLinks;
        hubsMaxCosts = new ArrayList<>(numHubs);
        this.path = resultPath;

        links = new String[numLinks];

        Arrays.sort(phmlrp.getHubsArr());
        hubsList = Arrays.stream(phmlrp.getHubsArr()).boxed().collect(Collectors.toList());

//        prepareExcel();
    }

    public double getMaxCost() {
        return maxCost;
    }

    String[] getLinks() {
        return links;
    }

    void calculateBHCost() {
        Arrays.sort(phmlrp.getHubsArr());
        double[] betweenHubs = new double[numHubs];
        double bestHubCost = Integer.MAX_VALUE;
        System.out.println("Hubs Enumeration:");
        // loop on the hubs
        for (int h = 0; h < numHubs; h++) {
            System.out.println("Hub " + phmlrp.getHubsArr()[h]);
            // loop through other hubs
            for (int hh = 0; hh < numHubs; hh++) {
                // skipping current hub
                if (h == hh) continue;

                // calculateCost between hubs
                double betweenTwoHubs = phmlrp.getDistance(
                        phmlrp.getHubsArr()[h], phmlrp.getHubsArr()[hh]) * phmlrp.getHubToHubCFactor();
                betweenHubs[h] += betweenTwoHubs;
                System.out.println(phmlrp.getHubsArr()[h] + " - " + phmlrp.getHubsArr()[hh] +
                        " => " + betweenTwoHubs);
            }

            if (betweenHubs[h] < bestHubCost) {
                middleHubIdx = h;
                bestHubCost = betweenHubs[h];
            }
            System.out.println("Sum = " + betweenHubs[h]);
        }

        System.out.print("Minimum Hub Index is : " + phmlrp.getHubsArr()[middleHubIdx]);
        System.out.println(" Sum is : " + bestHubCost);

        createLinks();
    }

    void runIncomplete() {
        calculateMaxHubsLinks();
    }

    private void calculateMaxHubsLinks() {
        System.out.println("Calculating the Max link cost for each hub:");
        // the minimum max hubs cost
//        double minHubCost = Integer.MAX_VALUE;
        // loop on the hubs (first hub in link)
        for (int m = 0; m < numHubs; m++) {
            int mid = hubsList.get(m);
            System.out.println("Hub " + mid);

            double maxCost = 0;

            // loop on the hubs (first hub in link)
            for (int f = 0; f < numHubs; f++) {
                int first = hubsList.get(f);

                // skipping middle hub
                if (first == mid) continue;

                double linkCost = addLink(m, mid, f, first, -1, -1);
                sheetRowCount++;

                if (linkCost > maxCost) {
                    maxCost = linkCost;
                }

                // loop on the hubs (last hub in link)
                for (int l = 0; l < numHubs; l++) {
                    int last = hubsList.get(l);

                    // skipping current hub and middle hub
                    if (first == last || mid == last) continue;

                    linkCost = addLink(m, mid, f, first, l, last);
                    sheetRowCount++;

                    if (linkCost > maxCost) {
                        maxCost = linkCost;
                    }
                }
            }

            System.out.println(" Max = " + maxCost);
            hubsMaxCosts.add(maxCost);
        }

        sheetRowCount += 2;
//        createFirstRow(spreadsheet);

        createLinks();
    }

    private void createLinks() {
        System.out.println("Links Creation:");
        int m = hubsMaxCosts.indexOf(Collections.min(hubsMaxCosts));
        int mid = hubsList.get(m);
        System.out.println("Hub " + mid);
//        XSSFRow hRow = spreadsheet.createRow(sheetRowCount);
//        hRow.createCell(0, CellType.STRING).setCellValue("Mid-Hub " + mid);
        sheetRowCount++;
        // loop on the hubs (first hub in link)
        for (int f = 0; f < numHubs; f++) {
            int first = hubsList.get(f);

            // skipping middle hub
            if (first == mid) continue;

            double linkCost = addLink(m, mid, f, first, -1, -1);
            if (linkCost > maxCost) {
                maxCost = linkCost;
            }
            sheetRowCount++;
            linksCount++;

            // loop on the hubs (last hub in link)
            for (int l = 0; l < numHubs; l++) {
                int last = hubsList.get(l);

                // skipping current hub and middle hub
                if (first == last || mid == last) continue;

                addLink(m, mid, f, first, l, last);
                sheetRowCount++;
            }

            if (linksCount == numLinks)
                break;
        }

        System.out.println("links count " + linksCount);
        if (linksCount < numLinks && numHubs > 2) {
            // we did not reach q: required number of links
            // remove middleHub from the hubs array by replacing it with the last hub
            // and decrement number of hubs
            numHubs--;
            hubsList.remove(m);
            hubsMaxCosts.remove(m);
            createLinks();
        }

//        try {
//            Utils.createExcelFile(workbook,
//                    path + "/" + phmlrp.getNumNodes() + "_" +
//                            phmlrp.getNumHubs() + "_" +
//                            phmlrp.getNumVehiclesPerHub() + "_" +
//                            numLinks);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /*private int getMinimumMaxIdx() {
        int m = -1;
        double min = Integer.MAX_VALUE;
        Iterator it = hubsMaxCosts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            if ((Double) pair.getValue() < min) {
                min = (Double) pair.getValue();
                m = (Integer) pair.getKey();
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return m;
    }*/

    private double addLink(int m, int mid, int f, int first, int l, int last) {
        // First hub collection cost
        int fCollectionCost = phmlrp.getCollectionCostArr()[f];
        // calculateCost between first and middle hubs
        double fmBetweenHubs = phmlrp.getDistance(first, mid) * phmlrp.getHubToHubCFactor();
        // between middle and last hub
        double mlBetweenHubs = 0;
        int lDistributionCost;
        if (l == -1) {
            // mid hub as the last hub
            // Middle hub distribution cost
            if (m == -1)
                m = middleHubIdx;
            lDistributionCost = phmlrp.getDistributionCostArr()[m];
        } else {
            // calculateCost between middle and last hub
            mlBetweenHubs = phmlrp.getDistance(mid, last) * phmlrp.getHubToHubCFactor();
            // Last hub distribution cost
            lDistributionCost = phmlrp.getDistributionCostArr()[l];
        }

        double linkCost = fCollectionCost + fmBetweenHubs + mlBetweenHubs + lDistributionCost;

//        System.out.println(first + " - " + mid + " - " + last + " => " + linkCost);

//        XSSFRow row = spreadsheet.createRow(sheetRowCount);
        if (l == -1) {
//            row.createCell(0, CellType.STRING).setCellValue(first + " - " + mid);
//            row.createCell(3, CellType.STRING).setCellValue("NONE");
            // add one just for comparing with Delik Ucar
            links[linksCount] = (first+1) + "-" + (mid+1);
        } else {
//            row.createCell(0, CellType.STRING).setCellValue(first + " - " + mid + " - " + last);
//            row.createCell(3, CellType.NUMERIC).setCellValue(mlBetweenHubs);
        }
//        row.createCell(1, CellType.NUMERIC).setCellValue(fCollectionCost);
//        row.createCell(2, CellType.NUMERIC).setCellValue(fmBetweenHubs);
//        row.createCell(4, CellType.NUMERIC).setCellValue(lDistributionCost);
//        row.createCell(5, CellType.NUMERIC).setCellValue(linkCost);

        return linkCost;
    }

//    void createLinksEnumerusly() {
//        prepareExcel();
//        int rowCount = 1;
//        // use the best hub after enumerateBHCost() to create links
////        double bestLinkCost = Integer.MAX_VALUE;
////        int[] bestLink = new int[3];
//        System.out.println("Links Creation:");
//        // loop on the hubs (first hub in link)
//        for (int m = 0; m < numHubs; m++) {
//            int mid = phmlrp.getHubsArr()[m];
////            System.out.println("Hub " + mid);
//            XSSFRow hRow = spreadsheet.createRow(rowCount);
//            hRow.createCell(0, CellType.STRING).setCellValue("Mid-Hub " + mid);
//            rowCount++;
//            // loop on the hubs (first hub in link)
//            for (int f = 0; f < numHubs; f++) {
//                int first = phmlrp.getHubsArr()[f];
//
//                // skipping middle hub
//                if (first == mid) continue;
//
//                // loop on the hubs (last hub in link)
//                for (int l = 0; l < numHubs; l++) {
//                    int last = phmlrp.getHubsArr()[l];
//
//                    // skipping current hub and middle hub
//                    if (first == last || mid == last) continue;
//
//                    // First hub collection cost
//                    int fCollectionCost = phmlrp.getCollectionCostArr()[f];
//                    // calculateCost between first and middle hubs
//                    double fmBetweenHubs = phmlrp.getDistance(first, mid) * phmlrp.getHubToHubCFactor();
//                    // Middle hub collection cost
//                    int mCollectionCost = phmlrp.getCollectionCostArr()[m];
//                    // calculateCost between middle and second hubs
//                    double mlBetweenHubs = phmlrp.getDistance(mid, last) * phmlrp.getHubToHubCFactor();
//                    // Last hub distribution cost
//                    int lDistributionCost = phmlrp.getDistributionCostArr()[l];
//
//                    double linkCost = fCollectionCost + fmBetweenHubs +
//                            mCollectionCost + mlBetweenHubs + lDistributionCost;
//
//                    System.out.println(first + " - " + mid + " - " + last + " => " + linkCost);
//
//                    XSSFRow row = spreadsheet.createRow(rowCount);
//                    row.createCell(0, CellType.STRING).setCellValue(first + " - " + mid + " - " + last);
//                    row.createCell(1, CellType.NUMERIC).setCellValue(fCollectionCost);
//                    row.createCell(2, CellType.NUMERIC).setCellValue(fmBetweenHubs);
//                    row.createCell(3, CellType.NUMERIC).setCellValue(mlBetweenHubs);
//                    row.createCell(4, CellType.NUMERIC).setCellValue(lDistributionCost);
//                    row.createCell(5, CellType.NUMERIC).setCellValue(linkCost);
//                    rowCount++;
//
////                    if (linkCost < bestLinkCost) {
////                        bestLinkCost = linkCost;
////                        bestLink[0] = first;
////                        bestLink[1] = mid;
////                        bestLink[2] = last;
////                    }
//                }
//            }
//        }
//
//        try {
//            Utils.createExcelFile(workbook,
//                    "incompleteHubResults_" + path + "/" + phmlrp.getNumNodes() + "_" +
//                            phmlrp.getNumHubs() + "_" +
//                            phmlrp.getNumVehiclesPerHub());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        System.out.print("Best Link is: ");
////        for (int i : bestLink) {
////            System.out.print(i + " - ");
////        }
////        System.out.println();
////        System.out.println("Cost of: " + bestLinkCost);
//    }


//    private void prepareExcel() {
//        workbook = new XSSFWorkbook();
//        spreadsheet = workbook.createSheet(
//                phmlrp.getNumNodes() + "." +
//                        phmlrp.getNumHubs() + "." +
//                        phmlrp.getNumVehiclesPerHub());
//        createFirstRow(spreadsheet);
//    }

//    private void createFirstRow(XSSFSheet dpSpreadsheet) {
//        XSSFRow row = dpSpreadsheet.createRow(sheetRowCount);
//        row.createCell(0, CellType.STRING).setCellValue("Link");
//        row.createCell(1, CellType.STRING).setCellValue("fCollection");
//        row.createCell(2, CellType.STRING).setCellValue("fmBetweenHubs");
//        row.createCell(3, CellType.STRING).setCellValue("mlBetweenHubs");
//        row.createCell(4, CellType.STRING).setCellValue("lDistribution");
//        row.createCell(5, CellType.STRING).setCellValue("Total Cost");
//        sheetRowCount++;
//    }
}
