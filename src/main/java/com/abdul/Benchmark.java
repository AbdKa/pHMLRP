package com.abdul;

import java.util.stream.IntStream;

public class Benchmark {

    private final static int numOfRuns = 10;

    public static void main(String[] args) {
        new GeneralResults(numOfRuns);

        IntStream.range(0, numOfRuns).boxed().parallel().forEach(v ->
        {
            String[] instances = Consts.instances;
            for (int i = 0; i < instances.length; i++) {
                String s = instances[i];

                String[] parts = s.split("\\.");

                for (IS is : IS.values())
                    for (ALGO algo : ALGO.values()) {
                        System.out.println(v + " " + s + " " + is + " " + algo);
                        Main.main(new String[]{
                                "--run", v.toString(),
                                "--instance", String.valueOf(i),
                                "--dataset", parts[0],
                                "--nodes", parts[1],
                                "--hubs", parts[2],
                                "--vehicles", parts[3],
                                "--initial", is.toString(),
                                "--algorithm", algo.toString(),
                                "--silent"});
                    }
            }
        });

        GeneralResults.printFinalResults();
    }
}
