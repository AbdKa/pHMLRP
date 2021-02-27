package com.abdul;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Gurobi {

    private final String pyGorubiBasePath = "python\\hubRoutingCenter.py";
    private final String outputJson = "python\\results\\gorubi-routes.json";
    private final PHMLRP phmlrp;
    private final DS dataset;
    private final int numNodes, numHubs, numVehicles;
    private final float alpha;

    Gurobi(PHMLRP phmlrp, DS dataset, int numNodes, int numHubs, int numVehicles, float alpha) {
        this.phmlrp = phmlrp;
        this.dataset = dataset;
        this.numNodes = numNodes;
        this.numHubs = numHubs;
        this.numVehicles = numVehicles;
        this.alpha = alpha;
    }

    void getInitSol() {
        try {
            Process p = Runtime.getRuntime().exec(
                    "python " + pyGorubiBasePath + " " +
                            dataset + " " +
                            numNodes + " " +
                            numHubs + " " +
                            numVehicles + " " +
                            alpha + " 1 2 7");
            readPyOutput(p);
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }

        int[] hubsArr = new int[numHubs];
        JSONParser parser = new JSONParser();
        try {
            JSONObject a = (JSONObject) parser.parse(new FileReader(outputJson));
            JSONArray routesJson = (JSONArray) a.get("routes");
            int i = 0;
            int h = 0;
            for (Object routeObj : routesJson) {
                int j = 0;
                JSONArray route = (JSONArray) routeObj;
                ArrayList<Integer> r = new ArrayList<>();
                for (Object nodeObj : route) {
                    if (j < route.size() - 1) {
                        int node = Math.toIntExact((long) nodeObj);
                        if (j == 0 && i % numVehicles == 0) {
                            hubsArr[h] = node;
                            h++;
                        } else if (j != 0) {
                            r.add(node);
                        }
                    }
                    j++;
                }
                phmlrp.setRouteInVehiclesList(i, r);
                i++;
            }
            phmlrp.setHubsArr(hubsArr);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("hubs:");
        System.out.print(Arrays.toString(phmlrp.getHubsArr()));

        for (List<Integer> route :
                phmlrp.getVehiclesList()) {
            System.out.print(route);
            System.out.println();
        }
    }

    void getSolGivenHubs() {
        StringBuilder inRouteStr = new StringBuilder();
        for (int hub : phmlrp.getHubsArr())
            inRouteStr.append(hub).append(",");
        try {
            Process p = Runtime.getRuntime().exec(
                    "python " + pyGorubiBasePath + " " +
                            dataset + " " +
                            numNodes + " " +
                            phmlrp.getHubsArr().length + " " +
                            numVehicles + " " +
                            alpha + " 1 2 7 h " +
                            inRouteStr.substring(0, inRouteStr.length() - 1)
            );
            readPyOutput(p);
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        try {
            JSONObject a = (JSONObject) parser.parse(new FileReader(outputJson));
            JSONArray routesJson = (JSONArray) a.get("routes");
            int i = 0;
            for (Object routeObj : routesJson) {
                int j = 0;
                JSONArray route = (JSONArray) routeObj;
                ArrayList<Integer> r = new ArrayList<>();
                for (Object nodeObj : route) {
                    if (j > 0 && j < route.size() - 1) {
                        int node = Math.toIntExact((long) nodeObj);
                        r.add(node);
                    }
                    j++;
                }
                phmlrp.setRouteInVehiclesList(i, r);
                i++;
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("hubs:");
        for (int h :
                phmlrp.getHubsArr()) {
            System.out.print(h + ", ");
        }
        System.out.println();
        for (List<Integer> route :
                phmlrp.getVehiclesList()) {
            route.forEach(node -> System.out.print(node + ", "));
            System.out.println();
        }
    }

    int[] optimizeRoute(int[] inRoute) {
        StringBuilder inRouteStr = new StringBuilder();
        for (int node : inRoute)
            inRouteStr.append(node).append(",");
        try {
            Process p = Runtime.getRuntime().exec(
                    "python " + pyGorubiBasePath + " " +
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
            JSONObject a = (JSONObject) parser.parse(new FileReader(outputJson));
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
