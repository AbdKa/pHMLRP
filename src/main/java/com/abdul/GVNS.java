package com.abdul;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.abdul.Utils.outputStream;
import static com.abdul.Utils.printLine;

class GVNS {
    private double MAX_RUN_TIME;
    private final Params params;

    //    0, Insertion
    //    1, Swap
    //    2, HubMove
    //    3, EdgeOpt
    private final List<List<Integer>> combinations;

    private double initObj = Integer.MAX_VALUE;
    private double minObj = Integer.MAX_VALUE;
    private double initCPU = 0;
    private int bestIteration = 0;

    GVNS(Params params) {
        this.params = params;
        combinations = Utils.getCombinations("ls_combinations");
        MAX_RUN_TIME = Utils.getMaxRunTime(params.getNumNodes());
    }

    void runGVNS() {
        String uniqueFileName = Utils.getUniqueFileName(params);

        long start = System.nanoTime();
        doGVNS(uniqueFileName, start);
        double solCPU = Utils.getSolCPU(start);

        System.err.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%d\n",
                uniqueFileName, initObj, initCPU, minObj, solCPU, bestIteration);
    }

    private void doGVNS(String uniqueFileName, long start) {
        OutputStream stream = outputStream(params, uniqueFileName);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.US_ASCII));

        out.println("iteration, cost, hubs, routes");

        int iteration = 0;
        while (System.nanoTime() - start < MAX_RUN_TIME) {
            for (int combIdx = 0; combIdx < combinations.size(); combIdx++) {
                // run on every combination

                PHCRP pHCRP = Utils.newPHCRPInstance(this.params);
                double currentInitObj = pHCRP.getMaxCost();
                Operations operations = new Operations(pHCRP);

                int k = 0;
                while (k < combinations.get(combIdx).size() && System.nanoTime() - start < MAX_RUN_TIME) {
                    // for each neighborhood

                    if (!params.getSilent())
                        System.out.println(combIdx + " " + k);

                    operations.doLocalSearch(k);

                    iteration++;
                    k++;
                }

                double newObj = pHCRP.getMaxCost();

                if (newObj < minObj) {
                    initObj = currentInitObj;
                    minObj = newObj;
                    initCPU = pHCRP.getInitCPU();
                    bestIteration = iteration;

                    printLine(pHCRP, out, iteration, minObj);
                }
            }
        }
    }
}
