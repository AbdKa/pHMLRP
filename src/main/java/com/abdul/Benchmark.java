package com.abdul;

import java.util.stream.IntStream;

public class Benchmark {

    public static void main(String[] args) {
        IntStream.range(0, 100).boxed().parallel().forEach(v ->
        {
            for (DS ds : DS.values()) {
                Main.main(new String[]{"--dataset", ds.toString(), "--nodes", "10", "--hubs", "2", "--vehicles", "1"});
            }

        });
    }

}
