package com.abdul;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

class Gurobi {

    private final static String pyGorubiBasePath = "python" + File.separator + "hubRoutingCenter.py";
    private final static String jsonPath = "results" + File.separator;
    private static final String python = System.getProperty("os.name").contains("Mac OS X") ? "python3" : "python";
    private final PHCRP PHCRP;
    private final DS dataset;
    private final int numNodes, numHubs, numVehicles;
    private final float alpha;

    Gurobi(PHCRP PHCRP, DS dataset, int numNodes, int numHubs, int numVehicles, float alpha) {
        this.PHCRP = PHCRP;
        this.dataset = dataset;
        this.numNodes = numNodes;
        this.numHubs = numHubs;
        this.numVehicles = numVehicles;
        this.alpha = alpha;
    }

    int[] optimizeRoute(int[] inRoute) {
        StringBuilder inRouteStr = new StringBuilder();
        for (int node : inRoute)
            inRouteStr.append(node).append(",");
        try {
            Process p = Runtime.getRuntime().exec(
                    python + " " +
                            pyGorubiBasePath + " " +
                            dataset + " " +
                            inRoute.length + " " +
                            "1 1 " + alpha +
                            " 1 2 7 r " +
                            inRouteStr.substring(0, inRouteStr.length() - 1)
            );
            readPyOutput(p);
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }

        int[] outRoute = new int[inRoute.length];
        JSONParser parser = new JSONParser();
        try {
//            TODO: add file name to the path
            JSONObject a = (JSONObject) parser.parse(new FileReader(jsonPath));
            JSONArray routesJson = (JSONArray) a.get("routes");
            for (Object routeObj : routesJson) {
                // only one route for now
                int j = 0;
                JSONArray route = (JSONArray) routeObj;
                ArrayList<Integer> r = new ArrayList<>();
                for (Object nodeObj : route) {
                    // because the last node of each route is repeated
                    if (j < route.size() - 1) {
                        int node = Math.toIntExact((long) nodeObj);
                        outRoute[j] = node;
                    }
                    j++;
                }
            }

            System.out.println("new route:");
            for (int node :
                    outRoute) {
                System.out.print(node + ", ");
            }

            return outRoute;
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void readPyOutput(Process p) throws IOException {
        String s;
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }
}
