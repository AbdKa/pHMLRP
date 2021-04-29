package com.abdul;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

class Utils {

    static double CPU = 0;

    private static final int BUFFER_SIZE = 1 << 16; // 64K

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

    /**
     * creates a CSV file
     * @param header is the first row in CSV
     * @param lists any type and number of lists
    * */
    static void createCSVFile(String fileName, String header, List... lists) throws IOException {
        //Write csv file
        try {
            File csvOutputFile = new File(fileName + ".csv");
            OutputStream stream = new GZIPOutputStream(Files.newOutputStream(csvOutputFile.toPath(), StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE), BUFFER_SIZE);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            out.println(header);
            for (int i = 0; i < lists[0].size(); i++) {
                for (List arr : lists) {
                    out.print(arr.get(i));
                    out.print(", ");
                }
                out.println();
            }
            out.flush();

        } catch (FileNotFoundException | InvalidPathException e) {
            throw new IOException("Failed to open ", e);
        }

        System.out.println(fileName + ".csv written successfully");
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

    /**
     * Human readable execution time information
     *
     * @param startNano start of the task
     * @return human readable message
     */
    static String execution(long startNano) {
        return DurationFormatUtils.formatDuration(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startNano, TimeUnit.NANOSECONDS), "HH:mm:ss");
    }

    static String getUniqueFileName(Params params) {
        return params.getRunNum() + "-" + params.getDataset() + "." + params.getNumNodes() + "." + params.getNumHubs() + "." +
                params.getNumVehicles() + "-" + params.getInitSol() + "-SA" + "-" +
                UUID.randomUUID().toString().replaceAll("-", "");
    }
}
