package com.abdul;

import java.util.stream.IntStream;

public class Benchmark {

    public static void main(String[] args) {

        final String[] instances = new String[]{
                "TR.10.2.1", "TR.10.2.2", "TR.10.3.1",
                "TR.15.2.1", "TR.15.2.2",
                "TR.25.2.1", "TR.25.2.5", "TR.25.5.1", "TR.25.5.2",
                "TR.50.2.1", "TR.50.2.5", "TR.50.5.1", "TR.50.5.2",
                "TR.81.2.1", "TR.81.2.5", "TR.81.5.1", "TR.81.5.2",
                "TR.81.9.1", "TR.81.9.2", "TR.81.9.3", "TR.81.9.4", "TR.81.9.5",
                "AP100.100.5.1", "AP100.100.5.2", "AP100.100.5.5",
                "AP200.200.10.1", "AP200.200.10.2", "AP200.200.10.5",
                "CAB.10.2.1", "CAB.10.2.2", "CAB.10.3.1",
                "CAB.15.2.1", "CAB.15.2.2",
                "CAB.25.2.1", "CAB.25.2.5", "CAB.25.5.1", "CAB.25.5.2",
        };

        IntStream.range(0, 10).boxed().parallel().forEach(v ->
        {
            for (String s : instances) {

                String[] parts = s.split("\\.");

                for (IS is : IS.values())
                    for (ALGO algo : ALGO.values()) {
                        Main.main(new String[]{
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
    }
}
