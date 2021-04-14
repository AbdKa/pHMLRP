package com.abdul;

import java.util.stream.IntStream;

public class Benchmark {

    public static void main(String[] args) {

        IntStream.range(0, 10).boxed().parallel().forEach(v ->
        {
            for (String s : Consts.instances) {

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
