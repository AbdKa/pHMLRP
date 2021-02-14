package com.abdul;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.abdul.DS.AP100;
import static com.abdul.DS.TR;

class Dataset {

    private static final List<List<Double>> TR16distances = new ArrayList<>();
    private static final List<List<Double>> TRdistances = new ArrayList<>();
    private static final List<List<Double>> AP100distances = new ArrayList<>();
    private static final List<List<Double>> AP200distances = new ArrayList<>();
    private static final List<List<Double>> CABdistances = new ArrayList<>();

    private static void load(String dataset, List<List<Double>> distances) {
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
            throw new RuntimeException(e.getMessage());
        }
    }

    static {

        for (DS ds : new DS[]{DS.TR16, TR, AP100, DS.AP200, DS.CAB}) {

            switch (ds) {
                case TR16:
                    load("Turkish16NetworkDist", TR16distances);
                    break;
                case TR:
                    load("TurkishNetworkDist", TRdistances);
                    break;
                case AP100:
                    load("APNetworkDist100", AP100distances);
                    break;
                case AP200:
                    load("APNetworkDist200", AP200distances);
                    break;
                case CAB:
                    load("CABNetworkDist", CABdistances);
                    break;
                default:
                    throw new AssertionError("unknown dataset :" + ds);
            }
        }

    }

    public static double getDistance(DS dataset, int node1, int node2) {
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
}
