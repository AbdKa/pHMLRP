package com.abdul;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.abdul.DS.*;

class Dataset {

    private static final List<List<Double>> TR16distances = new ArrayList<>();
    private static final List<List<Double>> TRdistances = new ArrayList<>();
    private static final List<List<Double>> AP10distances = new ArrayList<>();
    private static final List<List<Double>> AP15distances = new ArrayList<>();
    private static final List<List<Double>> AP100distances = new ArrayList<>();
    private static final List<List<Double>> AP200distances = new ArrayList<>();
    private static final List<List<Double>> CABdistances = new ArrayList<>();

    private static void load(String dataset, List<List<Double>> distances) {
        try (BufferedReader CSVFile = Files.newBufferedReader(Paths.get("db/" + dataset + ".csv"))) {
            String dataRow = CSVFile.readLine();
//            dataRow = CSVFile.readLine();
//            dataRow = CSVFile.readLine();
            while (dataRow != null && !dataRow.equals("")) {
                // step 1 : converting comma separate String to array of nodes
                String[] nodesStrArr = dataRow.split(",");
//                nodesStrArr = Arrays.copyOfRange(nodesStrArr, 2, nodesStrArr.length);
//                System.out.println(Arrays.toString(nodesStrArr));
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

        for (DS ds : new DS[]{TR16, TR, AP10, AP15, AP100, AP200, CAB}) {

            switch (ds) {
                case TR16:
                    load("TurkishNetworkDist16", TR16distances);
                    break;
                case TR:
                    load("TurkishNetworkDist", TRdistances);
                    break;
                case AP10:
                    load("APNetworkDist10", AP10distances);
                    break;
                case AP15:
                    load("APNetworkDist15", AP15distances);
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
            case AP10:
                return AP10distances.get(node1).get(node2);
            case AP15:
                return AP15distances.get(node1).get(node2);
            case AP100:
                return AP100distances.get(node1).get(node2);
            case AP200:
                return AP200distances.get(node1).get(node2);
            case CAB:
                return CABdistances.get(node1).get(node2);
            default:
                throw new AssertionError("unknown dataset :" + dataset);
        }
    }
}
