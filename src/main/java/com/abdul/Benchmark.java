package com.abdul;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static com.abdul.Consts.instances;
import static com.abdul.Utils.execution;

public class Benchmark {

    final static int numOfRuns = 10;

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

        IntStream.range(0, numOfRuns).boxed().parallel().forEach(v ->
        {
            for (String s : instances) {
                String[] parts = s.split("\\.");

//                for (IS is : IS.values())
                for (IS is : new IS[]{IS.RND})
                //  for (ALGO algo : ALGO.values())
                {
                    ALGO algo = ALGO.HC;
                    String[] params = new String[]{
                            "--dataset", parts[0],
                            "--nodes", parts[1],
                            "--hubs", parts[2],
                            "--vehicles", parts[3],
                            "--initial", is.toString(),
                            "--algorithm", algo.toString(),
                            "--silent",
                            "--force"
                    };
                    Main.main(params);
                }
            }
        });

        System.out.println("Total " + numOfRuns * instances.length * IS.values().length * ALGO.values().length +
                " problem instances are processed in " + execution(start));
    }
}
