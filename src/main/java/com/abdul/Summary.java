package com.abdul;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static com.abdul.Benchmark.numOfRuns;
import static com.abdul.Consts.instances;
import static com.abdul.Utils.BUFFER_SIZE;

public class Summary {

    private final Path resultsPath;

    public Summary() {
        resultsPath = Paths.get(System.getProperty("user.dir") + File.separator + "results");
        if (Files.notExists(resultsPath))
            throw new RuntimeException(resultsPath.toAbsolutePath() + "does not exist!");
    }

    public static void main(String[] args) {

        Summary summary = new Summary();

        // traverse per configuration
        List<IterSolution> list = summary.traverse("AP200.200.10.5", IS.GREEDY_GRB, ALGO.SA);
        IterSolution best = Collections.min(list);
        System.out.println(best);

        // min, max, variance example
        try (Stream<IterSolution> stream = list.stream()) {

            DoubleSummaryStatistics summaryStatistics = stream
                    .mapToDouble(IterSolution::cost)
                    .summaryStatistics();

            System.out.println(summaryStatistics);
        }

        // traverse and find the best solution for an instance
        list = summary.traverse("AP200.200.10.5", ALGO.SA);
        best = Collections.min(list);
        System.out.println(best);

    }

    public void generalResults() {
        for (String instance : instances) {
            for (IS is : IS.values()) {
                // this is the best of 10 runs
                List<IterSolution> list = traverse(instance, is, ALGO.SA);
                for (IterSolution s : list)
                    System.out.println(s);
            }
        }
    }

    public List<IterSolution> traverse(String prefix, ALGO algorithm) {

        Path docsPath = resultsPath.resolve(algorithm.toString());

        if (Files.notExists(docsPath))
            throw new RuntimeException(resultsPath.toAbsolutePath() + "does not exist!");

        final List<IterSolution> returnList = new ArrayList<>();
        try (Stream<Path> stream = Files.find(docsPath, 1, new CSVMatcher(prefix))) {
            stream.forEach(p -> {
                List<IterSolution> list = singleCSV(p);
                IterSolution best = Collections.min(list);
                returnList.add(best);
            });
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        if (returnList.size() != numOfRuns) {
            System.out.println("found " + returnList.size() + " many instances for " + prefix);
        }

        return returnList;

    }

    List<IterSolution> traverse(String instance, IS initial, ALGO algorithm) {
        return traverse(instance + "-" + initial + "-" + algorithm + "-", algorithm);
    }

    public List<IterSolution> singleCSV(Path p) {

        List<IterSolution> list = new ArrayList<>();
        try (
                InputStream stream = new GZIPInputStream(Files.newInputStream(p, StandardOpenOption.READ), BUFFER_SIZE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.US_ASCII))) {

            boolean first = true;

            for (; ; ) {
                String line = reader.readLine();
                if (line == null)
                    break;

                // skip the first line:
                if (first) {
                    first = false;
                    continue;
                }
                line = line.trim();

                IterSolution solution = new IterSolution(line);
                solution.setFileName(p.getFileName().toString());
                list.add(solution);


            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);

        }

        return list;
    }

    static final class CSVMatcher implements BiPredicate<Path, BasicFileAttributes> {

        private final String prefix;

        CSVMatcher(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean test(Path path, BasicFileAttributes basicFileAttributes) {

            if (!basicFileAttributes.isRegularFile()) return false;

            Path name = path.getFileName();

            return (name != null && name.toString().startsWith(prefix) && name.toString().endsWith("csv.gz"));

        }
    }


    static class IterSolution implements Comparable<IterSolution> {
        String fileName;
        private final int iteration;
        private final double cost;
        private final String hubs;
        private final String routes;

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        double cost() {
            return cost;
        }

        public IterSolution(String line) {
            String[] parts = line.split("\\s*,\\s*");
            iteration = Integer.parseInt(parts[0]);
            cost = Double.parseDouble(parts[1]);
            hubs = parts[2];
            routes = parts[3];
        }

        @Override
        public String toString() {
            return "IterSolution{" +
                    "fileName='" + fileName + '\'' +
                    ", iteration=" + iteration +
                    ", cost=" + cost +
                    ", hubs='" + hubs + '\'' +
                    ", routes='" + routes + '\'' +
                    '}';
        }

        @Override
        public int compareTo(IterSolution o) {
            return Double.compare(this.cost, o.cost);
        }
    }
}
