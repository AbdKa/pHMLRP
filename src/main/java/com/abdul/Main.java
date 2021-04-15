package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        // creating results paths
        String currentPath = System.getProperty("user.dir");
        String path = currentPath + File.separator + params.getResultPath();

        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        // call this to create greedy hubs for each problem instance
//        Extra.getGreedyHubs(params);

        PHMLRP phmlrp = new PHMLRP(params.getDataset(), params.getNumNodes(), params.getNumHubs(), params.getNumVehicles(),
                params.getCollectionCostCFactor(), params.getDistributionCostCFactor(), params.getHubToHubCFactor(),
                params.getRemovalPercentage());
        phmlrp.setSilent(params.getSilent());
        InitialSolutions initialSolutions = new InitialSolutions(phmlrp, params.getDataset(),
                params.getCollectionCostCFactor());

        switch (params.getInitSol()) {
            case RND:
                initialSolutions.randomSolution();
                break;
            case GREEDY:
                initialSolutions.greedySolution();
                break;
            case GREEDY_RND:
                initialSolutions.greedyRandomSolution();
                break;
            case RND_GREEDY:
                initialSolutions.randomGreedySolution();
                break;
            case PROB:
                initialSolutions.probabilisticInitSol();
                break;
            case GREEDY_GRB:
                initialSolutions.greedyGurobiSolution();
                break;
            case GRB:
                Gurobi gurobi = new Gurobi(phmlrp, params.getDataset(), params.getNumNodes(), params.getNumHubs(),
                        params.getNumVehicles(), params.getCollectionCostCFactor());
                gurobi.getInitSol();
                break;
        }

        phmlrp.calculateCost(PHMLRP.CostType.NORMAL);

        switch (params.getAlgorithm()) {
            case SA:
                SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(phmlrp, params);
                simulatedAnnealing.applySA();
                break;
            case VNS:
                VND vnd = new VND(params);
                vnd.runVND();
                break;
        }
    }
}
