package com.abdul;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Utils {

    static double CPU = 0;

    static List<List<Integer>> getCombinations(String fileName) {
        Map<String, Integer> hashMap;
        if (fileName.equals("ls_combinations")) {
            hashMap = Consts.localSearchMap;
        } else {
            hashMap = Consts.neighborhoods;
        }
        List<List<Integer>> combinationsList = new ArrayList<>();
        try {
            BufferedReader CSVFile = new BufferedReader(new FileReader(fileName + ".csv"));
            String dataRow = CSVFile.readLine();
            while (dataRow != null && !dataRow.equals("")) {
                // converting comma separate String to array of neighborhoods
                String[] combsStrArr = dataRow.split(",");
                List<Integer> comb = new ArrayList<>();

                for (String neighborhood : combsStrArr) {
                    int neighborhoodIdx = hashMap.getOrDefault(neighborhood, 0);
                    comb.add(neighborhoodIdx);
                }

                // add combination to the list
                combinationsList.add(comb);
                // read next line
                dataRow = CSVFile.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return combinationsList;
    }

    static XSSFSheet[] createSheets(XSSFWorkbook workbook, List<List<Integer>> combinations) {
        XSSFSheet[] list = new XSSFSheet[combinations.size()];
        for (List<Integer> comb : combinations) {
            StringBuilder sheetName = new StringBuilder();
            for (Integer nbhd : comb) {
                sheetName.append(Consts.neighborhoodsStr.getOrDefault(nbhd, ""));
                sheetName.append(".");
            }
        }
        return list;
    }

    static String createCombinationStr(List<Integer> combination) {
        StringBuilder combinationStr = new StringBuilder();
        for (Integer ls : combination) {
            combinationStr.append(Consts.localSearchesStr.getOrDefault(ls, "")).append(", ");
        }
        return combinationStr.toString();
    }

    static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    static void createExcelFile(XSSFWorkbook workbook, String fileName) throws IOException {
        //Write the workbook in file system
        FileOutputStream out = new FileOutputStream(
                new File(fileName + ".xlsx"));

        workbook.write(out);
        out.close();
        System.out.println(fileName + ".xlsx written successfully");
    }

    static void createTextFile() {
        try {
            File myObj = new File("filename.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static void writeToTextFile(FileWriter myWriter, String line) {
        try {
            myWriter.write(line + "\n");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static PHCRP getJsonInitSol(String fileName) {
        // TODO: change this to match the new output json
        String dataset = "";
        int N = 0;
        List<Integer> hubsList = new ArrayList<>();
        ArrayList<List<Integer>> vehiclesList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject a = (JSONObject) parser.parse(new FileReader("python/results/pHC_MTSP_" +
                    fileName + ".json"));
            JSONArray routesJson = (JSONArray) a.get("routes");
            dataset = (String) a.get("dataset");
            N = Math.toIntExact((long) a.get("N"));
            CPU = (double) a.get("CPU");
            System.out.println("Routes:");

            for (Object routeObj : routesJson) {
                String routeStr = (String) routeObj;
                System.out.println(routeStr);
                String[] route = routeStr.split(",");
                List<Integer> r = new ArrayList<>();
                for (int k = 0; k < route.length; k++) {
                    int node = Integer.parseInt(route[k]);
                    if (k == 0) {
                        if (hubsList.contains(node))
                            continue;
                        hubsList.add(node);
                        continue;
                    }
                    r.add(node);
                }

                vehiclesList.add(r);
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PHCRP pHCRP = new PHCRP(
                DS.valueOf(dataset),
                N,
                hubsList.size(),
                1,
                1,
                1,
                1,
                0.2f);

        int[] hubsArr = hubsList.stream().mapToInt(i -> i).toArray();
        pHCRP.setHubsArr(hubsArr);
        pHCRP.resetVehiclesList(vehiclesList);

        System.out.println("hubs:");
        for (int h :
                pHCRP.getHubsArr()) {
            System.out.print(h + ", ");
        }
        System.out.println();
        for (List<Integer> route :
                pHCRP.getVehiclesList()) {
            route.forEach(node -> System.out.print(node + ", "));
            System.out.println();
        }

        return pHCRP;
    }

    static PHCRP newPHMLRPInstance(String problemInstance, Params params) {

        return new PHCRP(
                DS.valueOf(problemInstance.split("\\.")[0]),
                Integer.parseInt(problemInstance.split("\\.")[1]),
                Integer.parseInt(problemInstance.split("\\.")[2]),
                Integer.parseInt(problemInstance.split("\\.")[3]),
                params.getCollectionCostCFactor(),
                params.getDistributionCostCFactor(),
                params.getHubToHubCFactor(),
                params.getRemovalPercentage());
    }
}
