package com.abdul;

import java.util.stream.IntStream;

public class Benchmark {
    public static void main(String[] args) {


        final String t = "215";
        IntStream.range(0, 10).boxed().parallel().forEach(v ->
        {

            for (DS ds : DS.values()) {

                Main.main(new String[]{"-dataset", ds.toString(), "-t", t});

                Main.main(new String[]{"", "HC", "-t", t});
                Main.main(new String[]{"-a", "SA", "-t", t});
                Main.main(new String[]{"-a", "SA", "-t", t, "-f"});
                Main.main(new String[]{"-a", "SA", "-t", t, "-b"});
                Main.main(new String[]{"-a", "SA", "-t", t, "-f", "-b"});

            }

        });
    }

}
