package com.abdul;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

class Utils {

    static double CPU = 0;

    static final int BUFFER_SIZE = 1 << 16; // 64K

    static List<List<Integer>> getCombinations(String fileName) {
        final Map<String, Integer> hashMap;
        if (fileName.equals("ls_combinations")) {
            hashMap = Consts.localSearchMap;
        } else {
            hashMap = Consts.neighborhoods;
        }
        List<List<Integer>> combinationsList = new ArrayList<>();
        try (BufferedReader CSVFile = new BufferedReader(new FileReader(fileName + ".csv"))) {
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
     * @return OutputStream
     */
    static OutputStream outputStream(Params params, String uniqueFileName) {
        final String fileName = params.getResultPath() +
                File.separator + params.getAlgorithm().toString() + File.separator + uniqueFileName;

        OutputStream stream;
        try {
            stream = new GZIPOutputStream(Files.newOutputStream(Paths.get(fileName + ".csv.gz"),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE), BUFFER_SIZE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stream;
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


    static PHCRP newPHCRPInstance(Params params) {

        PHCRP pHCRP = new PHCRP(
                params.getDataset(),
                params.getNumNodes(),
                params.getNumHubs(),
                params.getNumVehicles(),
                params.getCollectionCostCFactor(),
                params.getDistributionCostCFactor(),
                params.getHubToHubCFactor(),
                params.getRemovalPercentage());
        pHCRP.setSilent(params.getSilent());

        new InitialSolutions(pHCRP, params, true);
        pHCRP.calculateCost(PHCRP.CostType.NORMAL);

        return pHCRP;
    }

    static PHCRP newPHCRPInstance(String problemInstance, Params params) {

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

        final String algorithm;

        if (params.getAlgorithm() == ALGO.SA) {
            algorithm = "SA" + (params.isBest() ? "1" : "0") + (params.isForce() ? "1" : "0");
        } else
            algorithm = params.getAlgorithm().toString();

        return params.getDataset() + "." + params.getNumNodes() + "." + params.getNumHubs() + "." +
                params.getNumVehicles() + "-" + params.getInitSol() + "-" + algorithm + "-" +
                UUID.randomUUID().toString().replaceAll("-", "");
    }

    static double getSolCPU(long startTime) {
        return (System.nanoTime() - startTime) / 1e9;
    }

    /**
     * Prints a single line to the csv file.
     */
    static void printLine(PHCRP pHCRP, PrintWriter out, int counter, double newCost) {
        out.print(counter);
        out.print(", ");
        out.print(newCost);
        out.print(", ");
        out.print(pHCRP.getHubsString());
        out.print(", ");
        out.println(pHCRP.getVehiclesListString());
    }

    /**
     * set MAX_RUN_TIME as per https://link.springer.com/article/10.1007/s00291-018-0526-2
     */
    static double getMaxRunTime(int numNodes) {
        int seconds = 0;
        switch (numNodes) {
            case 10:
                seconds = 10;
                break;
            case 15:
            case 16:
                seconds = 30;
                break;
            case 25:
                seconds = 60;
                break;
            case 50:
                seconds = 270;
                break;
            case 81:
                seconds = 1000;
                break;
            case 100:
                seconds = 3600;
                break;
            case 200:
                seconds = 7200;
                break;
        }

        return seconds * 1e+9;
    }
}
