package com.abdul;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import static com.abdul.Utils.execution;

public class CompareSA {

    /**
     * Just for comparing four different SA variants.
     */
    public static void main(String[] args) {

        final long start = System.nanoTime();

        // creating results paths
        String currentPath = System.getProperty("user.dir");
        String resultsPath = currentPath + File.separator + "results";

        try {
            Files.createDirectories(Paths.get(resultsPath));
            for (ALGO algo : ALGO.values()) {
                Files.createDirectories(Paths.get(resultsPath + File.separator + algo.toString()));
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        IntStream.range(0, 50).boxed().parallel().forEach(v ->
        {
            for (String s : new String[]{"TR.25.2.1", "TR.25.2.5", "TR.25.5.1", "TR.25.5.2", "TR.50.2.1", "TR.50.2.5", "TR.50.5.1", "TR.50.5.2"}) {

                String[] parts = s.split("\\.");

                for (IS is : new IS[]{IS.RND, IS.GREEDY_GRB}) {

                    for (boolean force : new boolean[]{true, false})
                        for (boolean best : new boolean[]{true, false}) {

                            ArrayList<String> params = new ArrayList<>(Arrays.asList(
                                    "--dataset", parts[0],
                                    "--nodes", parts[1],
                                    "--hubs", parts[2],
                                    "--vehicles", parts[3],
                                    "--initial", is.toString(),
                                    "--algorithm", ALGO.SA.toString(),
                                    "--silent"));

                            if (best) params.add("--best");
                            if (force) params.add("--force");

                            Main.main(params.toArray(new String[0]));
                        }
                }
            }
        });

        System.out.println("Total " + 50 * 8 * IS.values().length * ALGO.values().length +
                " problem instances are processed in " + execution(start));
    }
}
