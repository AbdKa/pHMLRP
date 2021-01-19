package com.abdul;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class IncompleteHubs {

    private final PHMLRP phmlrp;
    private int numHubs;
    private final List<Integer> hubsList;
    private final List<Double> collectionCostArr;
    private final List<Double> distributionCostArr;
    private int middleHubIdx = -1;
    private final String path;
    private final int numLinks;
    private int linksCount = 0;

    private final List<Double> hubsMaxCosts;
    private double maxCost = 0;

    //    private XSSFWorkbook workbook;
//    private XSSFSheet spreadsheet;
    private int sheetRowCount = 0;

    private final String[] links;

    //    FileWriter myWriter;
    private String tempStr = "";
    private int numVehiclesPerHub;

    IncompleteHubs(PHMLRP phmlrp, String resultPath, int numLinks, FileWriter myWriter) {
        this.phmlrp = phmlrp;
        numHubs = phmlrp.getNumHubs();
        this.numLinks = numLinks;
        hubsMaxCosts = new ArrayList<>(numHubs);
        this.path = resultPath;
        this.numVehiclesPerHub = phmlrp.getNumVehiclesPerHub();

        links = new String[numLinks];

        hubsList = Arrays.stream(phmlrp.getHubsArr()).boxed().collect(Collectors.toList());
        collectionCostArr = Arrays.stream(phmlrp.getCollectionCostArr()).boxed().collect(Collectors.toList());
        distributionCostArr = Arrays.stream(phmlrp.getDistributionCostArr()).boxed().collect(Collectors.toList());

        sort();

//        hubsMaxCosts.add(23453245.0);
//        hubsMaxCosts.add(234534.0);
//        hubsMaxCosts.add(1.0);
//        hubsMaxCosts.add(2.0);

//        this.myWriter = myWriter;
//        prepareExcel();
    }

    void sort() {
//        System.out.println("Before sorting");
//        for (Integer i : hubsList) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
//        for (double i : collectionCostArr) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
//        for (double i : distributionCostArr) {
//            System.out.print(i + " ");
//        }

        int n = hubsList.size();

        // One by one move boundary of unsorted subarray
        for (int i = 0; i < n - 1; i++) {
            // Find the minimum element in unsorted array
            int min_idx = i;
            for (int j = i + 1; j < n; j++)
                if (hubsList.get(j) < hubsList.get(min_idx))
                    min_idx = j;

            // Swap the found minimum element with the first element
            int temp = hubsList.get(min_idx);
            hubsList.set(min_idx, hubsList.get(i));
            hubsList.set(i, temp);

//            System.out.println(min_idx);
            int count = 0;
            for (int v = min_idx * numVehiclesPerHub; v < ((min_idx + 1) * numVehiclesPerHub); v++) {
//                System.out.print(i * numVehiclesPerHub + count + " ");
                Collections.swap(collectionCostArr, i * numVehiclesPerHub + count, v);
                Collections.swap(distributionCostArr, i * numVehiclesPerHub + count, v);
                count++;
            }
//            System.out.println();
        }

//        System.out.println("After sorting");
//        for (Integer i : hubsList) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
//        for (double i : collectionCostArr) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
//        for (double i : distributionCostArr) {
//            System.out.print(i + " ");
//        }
    }

    public double getMaxCost() {
        return maxCost;
    }

    String[] getLinks() {
        return links;
    }

    void calculateBHCost() {
//        Arrays.sort(phmlrp.getHubsArr());
        double[] betweenHubs = new double[numHubs];
        double bestHubCost = Integer.MAX_VALUE;
//        System.out.println("Hubs Enumeration:");
        // loop on the hubs
        for (int h = 0; h < numHubs; h++) {
//            System.out.println("Hub " + phmlrp.getHubsArr()[h]);
            // loop through other hubs
            for (int hh = 0; hh < numHubs; hh++) {
                // skipping current hub
                if (h == hh) continue;

                // calculateCost between hubs
                double betweenTwoHubs = phmlrp.getDistance(
                        phmlrp.getHubsArr()[h], phmlrp.getHubsArr()[hh]) * phmlrp.getHubToHubCFactor();
                betweenHubs[h] += betweenTwoHubs;
//                System.out.println(phmlrp.getHubsArr()[h] + " - " + phmlrp.getHubsArr()[hh] +
//                        " => " + betweenTwoHubs);
            }

            if (betweenHubs[h] < bestHubCost) {
                middleHubIdx = h;
                bestHubCost = betweenHubs[h];
            }
//            System.out.println("Sum = " + betweenHubs[h]);
        }

//        System.out.print("Minimum Hub Index is : " + phmlrp.getHubsArr()[middleHubIdx]);
//        System.out.println(" Sum is : " + bestHubCost);

        createLinks();
    }

    void runIncomplete() {
        calculateMaxHubsLinks();
//        createLinks();
    }

    private void calculateMaxHubsLinks() {
//        System.out.println("Calculating the Max link cost for each hub:");
        // the minimum max hubs cost
//        double minHubCost = Integer.MAX_VALUE;
        // loop on the hubs (mid hub in link)
        for (int m = 0; m < numHubs; m++) {
            int mid = hubsList.get(m);
//            System.out.print("Hub " + mid);

            double hubMaxCost = 0;

            // loop on the hubs (first hub in link)
            for (int f = 0; f < numHubs; f++) {
                int first = hubsList.get(f);

                // skipping middle hub
                if (first == mid) continue;

                // loop through vehicles in first hub
                for (int vf = f * numVehiclesPerHub; vf < ((f + 1) * numVehiclesPerHub); vf++) {
                    // loop through vehicles in middle hub
                    for (int vm = m * numVehiclesPerHub; vm < ((m + 1) * numVehiclesPerHub); vm++) {
                        double linkCost = addLink(vm, mid, vf, first, -1, -1);
                        sheetRowCount++;

                        if (linkCost > hubMaxCost) {
                            hubMaxCost = linkCost;
                        }
                    }

                    // loop on the hubs (last hub in link)
                    for (int l = 0; l < numHubs; l++) {
                        int last = hubsList.get(l);

                        // skipping current hub and middle hub
                        if (first == last || mid == last) continue;

                        // loop through vehicles in a hub
                        for (int vl = l * numVehiclesPerHub; vl < ((l + 1) * numVehiclesPerHub); vl++) {
                            double linkCost = addLink(-1, mid, vf, first, vl, last);
                            sheetRowCount++;

                            if (linkCost > hubMaxCost) {
                                hubMaxCost = linkCost;
                            }
                        }
                    }
                }
            }

//            System.out.println(" Max = " + hubMaxCost);
            hubsMaxCosts.add(hubMaxCost);
        }

        sheetRowCount += 2;
//        createFirstRow(spreadsheet);

        createLinks();
    }

    double calculateCost() {
        ArrayList<String> tempStrArr = new ArrayList<>();
        // loop on the hubs
        for (int h = 0; h < numHubs; h++) {
            double cost;
            // loop through vehicles in a hub
            for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
                double collectionCost = collectionCostArr.get(i);

                // loop on other vehicles in the same hub
                for (int ii = h * numVehiclesPerHub; ii < ((h + 1) * numVehiclesPerHub); ii++) {
                    // skipping current vehicle
                    if (i == ii) continue;

                    double distributionCost = distributionCostArr.get(ii);
                    cost = collectionCost + distributionCost;

                    if (cost > maxCost) {
                        maxCost = cost;
                    }
                }

                // loop through other hubs
                for (int hh = 0; hh < numHubs; hh++) {
                    // skipping current hub
                    if (h == hh) continue;

                    // calculateCost between hubs
                    double betweenHubs = phmlrp.getDistance(hubsList.get(h), hubsList.get(hh));

                    if (h < hh && !tempStrArr.contains(h + "" + hh)) {
                        tempStrArr.add(h + "" + hh);
//                        System.out.println("betweenHubs " + h + " and " + hh + ": " + betweenHubs);
                    }

                    // loop through other hub's vehicles
                    for (int ii = hh * numVehiclesPerHub; ii < ((hh + 1) * numVehiclesPerHub); ii++) {

                        double distributionCost = distributionCostArr.get(ii);
                        cost = collectionCost + betweenHubs + distributionCost;

                        if (cost > maxCost) {
                            maxCost = cost;
                        }
                    }
                }
            }
        }

        return maxCost;
    }

    private void createLinks() {
//        System.out.println("Links Creation:");
//        Utils.writeToTextFile(myWriter, "Links Creation:");
        int m = hubsMaxCosts.indexOf(Collections.min(hubsMaxCosts));
//        System.out.println(m);
        int mid = hubsList.get(m);
//        System.out.println("Hub " + mid);
//        Utils.writeToTextFile(myWriter, "Hub " + mid);
//        XSSFRow hRow = spreadsheet.createRow(sheetRowCount);
//        hRow.createCell(0, CellType.STRING).setCellValue("Mid-Hub " + mid);
        sheetRowCount++;
        // loop on the hubs (first hub in link)
        for (int f = 0; f < numHubs; f++) {
            int first = hubsList.get(f);

            // skipping middle hub
            if (first == mid) continue;

            links[linksCount] = (first + 1) + "-" + (mid + 1);
            sheetRowCount++;
            linksCount++;

            // loop through vehicles in first hub
            for (int vf = f * numVehiclesPerHub; vf < ((f + 1) * numVehiclesPerHub); vf++) {
                // loop through vehicles in middle hub
                for (int vm = m * numVehiclesPerHub; vm < ((m + 1) * numVehiclesPerHub); vm++) {
                    double linkCost = addLink(vm, mid, vf, first, -1, -1);
                    if (linkCost > maxCost) {
                        maxCost = linkCost;
//                        Utils.writeToTextFile(myWriter, "direct maxCost: " + tempStr);
                    }
                }

                // loop on the hubs (last hub in link)
                for (int l = 0; l < numHubs; l++) {
                    int last = hubsList.get(l);

                    // skipping current hub and middle hub
                    if (first == last || mid == last) continue;

                    // loop through vehicles in a hub
                    for (int vl = l * numVehiclesPerHub; vl < ((l + 1) * numVehiclesPerHub); vl++) {
                        double linkCost = addLink(-1, mid, vf, first, vl, last);

                        if (linkCost > maxCost) {
                            maxCost = linkCost;
//                            Utils.writeToTextFile(myWriter, "indirect maxCost: " + tempStr);
                        }
                        sheetRowCount++;
                    }
                }
            }

            if (linksCount == numLinks)
                break;
        }

//        System.out.println("links count " + linksCount);
        if (linksCount < numLinks && numHubs > 2) {
            // we did not reach q: required number of links
            // remove middleHub from the hubs array by replacing it with the last hub
            // and decrement number of hubs
            numHubs--;
            hubsList.remove(m);
            hubsMaxCosts.remove(m);
            removeCollectionAndDistribution(m);
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

    private double addLink(int vm, int mid, int vf, int first, int vl, int last) {
        // First hub collection cost
        double fCollectionCost = collectionCostArr.get(vf);
        // calculateCost between first and middle hubs
        double fmBetweenHubs = phmlrp.getDistance(first, mid) * phmlrp.getHubToHubCFactor();
        // between middle and last hub
        double mlBetweenHubs = 0;
        double distributionCost;
        if (vl == -1) {
            // mid hub as the last hub
            // Middle hub distribution cost
            if (vm == -1)
                vm = middleHubIdx;
            distributionCost = distributionCostArr.get(vm);
        } else {
            // calculateCost between middle and last hub
            mlBetweenHubs = phmlrp.getDistance(mid, last) * phmlrp.getHubToHubCFactor();
            // Last hub distribution cost
            distributionCost = distributionCostArr.get(vl);
        }

        double linkCost = fCollectionCost + fmBetweenHubs + mlBetweenHubs + distributionCost;
        tempStr = "fCollectionCost " + (first + 1) + " " + fCollectionCost +
                " fmBetweenHubs " + (mid + 1) + " " + fmBetweenHubs +
                " fmBetweenHubs " + (last + 1) + " " + mlBetweenHubs +
                " distributionCost " + (last + 1) + " " + distributionCost +
                " linkCost " + linkCost;

//        System.out.println(first + " - " + mid + " - " + last + " => " + linkCost);

//        XSSFRow row = spreadsheet.createRow(sheetRowCount);
        if (vl == -1) {
//            row.createCell(0, CellType.STRING).setCellValue(first + " - " + mid);
//            row.createCell(3, CellType.STRING).setCellValue("NONE");
            // add one just for comparing with Delik Ucar
        } else {
//            row.createCell(0, CellType.STRING).setCellValue(first + " - " + mid + " - " + last);
//            row.createCell(3, CellType.NUMERIC).setCellValue(mlBetweenHubs);
        }
//        row.createCell(1, CellType.NUMERIC).setCellValue(fCollectionCost);
//        row.createCell(2, CellType.NUMERIC).setCellValue(fmBetweenHubs);
//        row.createCell(4, CellType.NUMERIC).setCellValue(distributionCost);
//        row.createCell(5, CellType.NUMERIC).setCellValue(linkCost);

        return linkCost;
    }

    private void removeCollectionAndDistribution(int h) {
        int numVehiclesPerHub = phmlrp.getNumVehiclesPerHub();
        for (int i = ((h + 1) * numVehiclesPerHub) - 1; i >= h * numVehiclesPerHub; i--) {
            collectionCostArr.remove(i);
            distributionCostArr.remove(i);
        }
    }

//    private double getVehiclesCollections(int h) {
//        double collection = 0;
//        int numVehiclesPerHub = phmlrp.getNumVehiclesPerHub();
//        for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
//            collection += collectionCostArr.get(i);
//            Utils.writeToTextFile(myWriter, h + " collection " + collection);
//        }
//
//        return collection;
//    }
//
//    private double getVehiclesDistributions(int h) {
//        double distribution = 0;
//        int numVehiclesPerHub = phmlrp.getNumVehiclesPerHub();
//        for (int i = h * numVehiclesPerHub; i < ((h + 1) * numVehiclesPerHub); i++) {
//            distribution += distributionCostArr.get(i);
//            Utils.writeToTextFile(myWriter, h + " distribution " + distribution);
//        }
//
//        return distribution;
//    }

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
