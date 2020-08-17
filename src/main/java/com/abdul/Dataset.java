package com.abdul;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Dataset {
    /*public static void main(String[] args) throws IOException {
        BufferedReader CSVFile = new BufferedReader(new FileReader("db/APNetworkDist200.csv"));
        String dataRow = CSVFile.readLine();
        dataRow = CSVFile.readLine();
        dataRow = CSVFile.readLine();
        while (dataRow != null && !dataRow.equals("")) {
            print(dataRow);
            dataRow = CSVFile.readLine();
        }
    }*/

    private static List<List<Double>> TRdistances = new ArrayList<>();
    private static List<List<Double>> AP100distances = new ArrayList<>();
    private static List<List<Double>> AP200distances = new ArrayList<>();
    private static List<List<Double>> CABdistances = new ArrayList<>();

    // just an initialization of a general distances list
    private static List<List<Double>> distances = TRdistances;

    private void loadCSV(String dataset) {
        switch (dataset) {
            case "TR":
                dataset = "TurkishNetworkDist";
                distances = TRdistances;
                break;
            case "AP100":
                dataset = "APNetworkDist100";
                distances = AP100distances;
                break;
            case "AP200":
                dataset = "APNetworkDist200";
                distances = AP200distances;
                break;
            case "CAB":
                dataset = "CABNetworkDist";
                distances = CABdistances;
                break;
        }

        if (distances.size() > 0) return;

        try {
            BufferedReader CSVFile = new BufferedReader(new FileReader("db/" + dataset + ".csv"));
            String dataRow = CSVFile.readLine();
            if (!dataset.equals("CABNetworkDist")) {
                dataRow = CSVFile.readLine();
                dataRow = CSVFile.readLine();
            }
            while (dataRow != null && !dataRow.equals("")) {
                // step 1 : converting comma separate String to array of nodes
                String[] nodesStrArr = dataRow.split(",");
                if (!dataset.equals("CABNetworkDist")) // if the dataset is not CAB remove first two strings
                    nodesStrArr = Arrays.copyOfRange(nodesStrArr, 2, nodesStrArr.length);
                // step 2 : convert String array to array of Doubles
                Double[] doubleValues = Arrays.stream(nodesStrArr)
                        .map(Double::valueOf)
                        .toArray(Double[]::new);
                // step 3 : convert Doubles array to list of Doubles
                List<Double> list = Arrays.asList(doubleValues);
                // step 4 : add list to the list of distances
                distances.add(list);
                // read next line
                dataRow = CSVFile.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    double getDistance(String dataset, int node1, int node2) {
        loadCSV(dataset);
        return distances.get(node1).get(node2);
    }

    private static void print(String dataRow) {
        String[] arr = dataRow.split(",");
        System.out.print("{");
        for (int i = 2; i < arr.length; i++) {
            System.out.print(arr[i] + ", ");
        }
        System.out.println("},");
    }
}
