package com.abdul;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Dataset {

    private static final List<List<Double>> TR16distances = new ArrayList<>();
    private static final List<List<Double>> TRdistances = new ArrayList<>();
    private static final List<List<Double>> AP100distances = new ArrayList<>();
    private static final List<List<Double>> AP200distances = new ArrayList<>();
    private static final List<List<Double>> CABdistances = new ArrayList<>();

    // just an initialization of a general distances list
    private static List<List<Double>> distances = TRdistances;

    private void loadCSV(DS ds) {

        String dataset;

        switch (ds) {
            case TR16:
                dataset = "Turkish16NetworkDist";
                distances = TR16distances;
                break;
            case TR:
                dataset = "TurkishNetworkDist";
                distances = TRdistances;
                break;
            case AP100:
                dataset = "APNetworkDist100";
                distances = AP100distances;
                break;
            case AP200:
                dataset = "APNetworkDist200";
                distances = AP200distances;
                break;
            case CAB:
                dataset = "CABNetworkDist";
                distances = CABdistances;
                break;
            default:
                throw new AssertionError("unknown dataset :" + ds);
        }

        if (distances.size() > 0) return;

        try {
            BufferedReader CSVFile = new BufferedReader(new FileReader("db/" + dataset + ".csv"));
            String dataRow = CSVFile.readLine();
            // TODO: edit AP10 and AP15 (add two rows and cols)
            dataRow = CSVFile.readLine();
            dataRow = CSVFile.readLine();
            while (dataRow != null && !dataRow.equals("")) {
                // step 1 : converting comma separate String to array of nodes
                String[] nodesStrArr = dataRow.split(",");
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

    double getDistance(DS dataset, int node1, int node2) {
        loadCSV(dataset);
        switch (dataset) {
            case TR16:
                return TR16distances.get(node1).get(node2);
            case TR:
                return TRdistances.get(node1).get(node2);
            case AP100:
                return AP100distances.get(node1).get(node2);
            case AP200:
                return AP200distances.get(node1).get(node2);
            case CAB:
                return CABdistances.get(node1).get(node2);
        }

        return 0.0;
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
