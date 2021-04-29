package com.abdul;

import java.util.ArrayList;
import java.util.List;

class DeterministicPermutation {
    private final PHCRP PHCRP;
    private Permutation[] bestPermutations;
    private double bestPermutationCost;
    private ArrayList<List<Integer>> initVehiclesList;
    private final double initMaxCost;

    DeterministicPermutation(PHCRP PHCRP) {
        this.PHCRP = PHCRP;
        initMaxCost = PHCRP.getMaxCost();
        bestPermutationCost = PHCRP.getMaxCost();
    }

//    void deterministicOperationOrder(XSSFWorkbook workbook, XSSFSheet spreadsheet) throws IOException {
//        initVehiclesList = new ArrayList<>();
//        for (List<Integer> list : PHCRP.getVehiclesList()) {
//            List<Integer> innerList = new ArrayList<>(list);
//            initVehiclesList.add(innerList);
//        }
//
//        int numOfSolutions = 30;
//        bestPermutations = new Permutation[numOfSolutions];
//        int[] operationsIndices = {0, 1, 2, 3};
//        int numberOfOperations = operationsIndices.length;
//
//        for (int sol = 0; sol < numOfSolutions; sol++) {
//            heapPermutation(operationsIndices, numberOfOperations, sol);
//            bestPermutationCost = initMaxCost;
//        }
//
//        XSSFRow row;
//        System.out.println("counter : " + counter);
//        for (int i = 0; i < bestPermutations.length; i++) {
//            row = spreadsheet.createRow(i + 1);
//            row.createCell(0, CellType.NUMERIC).setCellValue(i + 1);
//            String orderStr = buildOrderStr(bestPermutations[i].getOperationOrder());
//            row.createCell(1, CellType.STRING).setCellValue(orderStr);
//            row.createCell(2, CellType.NUMERIC).setCellValue(bestPermutations[i].getCost());
//            bestPermutations[i].printHubsAndRoutesToExcel(row);
//        }
//
//        //Write the workbook in file system
//        FileOutputStream out = new FileOutputStream(
//                new File("permutation_results.xlsx"));
//
//        workbook.write(out);
//        out.close();
//        System.out.println("xlsx written successfully");
//    }

    private String buildOrderStr(int[] operationOrder) {
        StringBuilder str = new StringBuilder();
        for (int operIdx :
                operationOrder) {
            str.append(getOperationName(operIdx)).append(", ");
        }
        return str.toString();
    }

    private String getOperationName(int operIdx) {
        switch (operIdx) {
            case 0:
                return "insertNodeInRoute";
            case 1:
                return "insertNodeBetweenRoutes";
            case 2:
                return "swapNodeInRoute";
            case 3:
                return "swapNodeWithinRoutes";
            case 4:
                return "edgeOptWithinRoutes";
            case 5:
                return "swapHubWithNode";
            case 6:
                return "twoOptAlgorithm";
            case 7:
                return "insertTwoNodes";
            case 8:
                return "nodesRemoveAndGreedyInsert";
            default:
                return "";
        }
    }

    //Generating permutation using Heap Algorithm
    private void heapPermutation(int[] arr, int size, int solNum) {
        // if size becomes 1 then prints the obtained
        // permutation
        if (size == 1) {
            Permutation permutation = executePermutation(arr);
            if (permutation.getCost() < bestPermutationCost) {
                bestPermutationCost = permutation.getCost();
                bestPermutations[solNum] = permutation;
            }
        }

        for (int i = 0; i < size; i++) {
            heapPermutation(arr, size - 1, solNum);
            if (size % 2 == 1) {
                // if size is odd, swap first and last element
                int temp = arr[0];
                arr[0] = arr[size - 1];
                arr[size - 1] = temp;
            } else {
                // If size is even, swap ith and last element
                int temp = arr[i];
                arr[i] = arr[size - 1];
                arr[size - 1] = temp;
            }
        }
    }

    private int counter = 0;

    private Permutation executePermutation(int[] arr) {
        counter++;
        int iterationsForEachPermutation = 1000;
        double bestCost = PHCRP.getMaxCost();
        int[] bestOrder = new int[arr.length];
        for (int iter = 0; iter < iterationsForEachPermutation; iter++) {
            for (int operationIdx : arr) {
                PHCRP.callOperation(operationIdx);
            }
            if (PHCRP.getMaxCost() < bestCost) {
                bestCost = PHCRP.getMaxCost();
                bestOrder = arr;
            }
        }
        Permutation permutation = new Permutation(bestOrder, bestCost, PHCRP.getHubsArr(), PHCRP.getVehiclesList());
        PHCRP.setMaxCost(initMaxCost);
        PHCRP.resetVehiclesList(initVehiclesList);
        return permutation;
    }
}

class Permutation {
    private final int[] operationOrder;
    private final double cost;
    private final int[] hubsArr;
    private ArrayList<List<Integer>> vehiclesList;

    Permutation(int[] operationOrder, double cost, int[] hubsArr, ArrayList<List<Integer>> vehiclesList) {
        this.operationOrder = operationOrder;
        this.cost = cost;
        this.hubsArr = hubsArr.clone();
        setVehiclesList(vehiclesList);
    }

    int[] getOperationOrder() {
        return operationOrder;
    }

    double getCost() {
        return cost;
    }

    private void setVehiclesList(ArrayList<List<Integer>> vehiclesList) {
        this.vehiclesList = new ArrayList<>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<>(list);
            this.vehiclesList.add(innerList);
        }
    }

//    void printHubsAndRoutesToExcel(XSSFRow row) {
//        StringBuilder hubs = new StringBuilder();
//        StringBuilder routes = new StringBuilder();
//        for (int hub : this.hubsArr) {
//            hubs.append(hub).append(", ");
//        }
//        for (List<Integer> route : this.vehiclesList) {
//            for (int node : route) {
//                routes.append(node).append(", ");
//            }
//            routes.append("; ");
//        }
//        row.createCell(3, CellType.STRING).setCellValue(hubs.toString());
//        row.createCell(4, CellType.STRING).setCellValue(routes.toString());
//    }
}
