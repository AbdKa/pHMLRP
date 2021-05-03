package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.util.Arrays;

public class GreedyHubs {

    public static void main(String[] args) {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        // create greedy hubs for each problem instance
        getGreedyHubs(params);
    }

    private static void getGreedyHubs(Params params) {
        String[] instances = Consts.instances;
        String[] hubs = new String[instances.length];
        long[] CPUs = new long[instances.length];
        for (int i = 0; i <instances.length; i++) {
            long time = System.currentTimeMillis();
            PHCRP PHCRP = Utils.newPHMLRPInstance(instances[i], params);
            InitialSolutions initialSolutions = new InitialSolutions(PHCRP, params.getDataset(),
                    params.getCollectionCostCFactor());
            initialSolutions.greedyPickHubs();
            StringBuilder hubsSB = new StringBuilder();
            Arrays.sort(PHCRP.getHubsArr());
            for (int hub : PHCRP.getHubsArr()) {
                hubsSB.append(hub).append("; ");
            }
            hubs[i] = hubsSB.toString();
            CPUs[i] = System.currentTimeMillis() - time;
        }

        printCSV(params, instances, hubs, CPUs);
    }

    private static void printCSV(Params params, String[] instances, String[] hubs, long[] CPUs) {
        String fileName = params.getResultPath() + File.separator + "Greedy_Hubs.csv";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.US_ASCII));

            String header = "Instance, Hubs, CPU (millisecond)";
            printWriter.println(header);

            for (int i = 0; i < instances.length; i++) {
                printWriter.printf("%s, %s, %d\n", instances[i], hubs[i], CPUs[i]);
            }

            printWriter.flush();
            printWriter.close();

        } catch (FileNotFoundException | InvalidPathException e) {
            e.printStackTrace();
        }

        System.out.println(fileName + " written successfully");
    }
}
