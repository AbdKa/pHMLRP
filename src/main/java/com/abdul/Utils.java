package com.abdul;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

class Utils {


    static List<List<Integer>> getCombinations() {
        List<List<Integer>> combinationsList = new ArrayList<>();
        try {
            BufferedReader CSVFile = new BufferedReader(new FileReader("Combinations.csv"));
            String dataRow = CSVFile.readLine();
            while (dataRow != null && !dataRow.equals("")) {
                // converting comma separate String to array of neighborhoods
                String[] combsStrArr = dataRow.split(",");
                List<Integer> comb = new ArrayList<>();

                for (String neighborhood : combsStrArr) {
                    int nbhd = Consts.neighborhoods.getOrDefault(neighborhood, 0);
                    comb.add(nbhd);
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
        for (List<Integer> comb : combinations){
            StringBuilder sheetName = new StringBuilder();
            for (Integer nbhd: comb) {
                sheetName.append(Consts.neighborhoodsStr.getOrDefault(nbhd, ""));
                sheetName.append(".");
            }
        }
        return list;
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
}
