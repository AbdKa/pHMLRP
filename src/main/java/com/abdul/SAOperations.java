package com.abdul;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SAOperations {

    private PHMLRP phmlrp;
    private ArrayList<List<Integer>> bestSol;

    SAOperations(PHMLRP phmlrp) {
        this.phmlrp = phmlrp;
        setBestVehiclesList(phmlrp.getVehiclesList());
    }

    private void setBestVehiclesList(ArrayList<List<Integer>> vehiclesList) {
        this.bestSol = new ArrayList<List<Integer>>();
        for (List<Integer> list : vehiclesList) {
            List<Integer> innerList = new ArrayList<Integer>(list);
            this.bestSol.add(innerList);
        }
    }

    void simulatedAnnealing() {
        // Initial temperature
        double T = 1000000;

        // Simulated Annealing parameters

        // Temperature at which iteration terminates
        final double minT = .0000001;
        // Decrease in temperature
        final double alpha = 0.99;
        // Number of iterations of annealing before decreasing temperature
        final int numIterations = 1;
        // Global minimum
        int min = phmlrp.getMaxCost();
        // new solution initialization
        ArrayList<List<Integer>> newSol;

        int counter = 0;
        phmlrp.setSimulatedAnnealing(true);
        // Continues annealing until reaching minimum
        // temperature
        while (T > minT) {
            for (int i = 0; i < numIterations; i++) {
                doRandomOperation();
                newSol = phmlrp.getVehiclesList();
                int newCost = phmlrp.getSaOperationCost();
                int difference = min - newCost;

                // Reassigns global minimum accordingly
                if (difference > 0) {
                    setBestVehiclesList(newSol);
                    min = newCost;
                    continue;
                }

                double probability = Math.pow(Math.E, difference / T);
                if (probability > Math.random()) {
//                    System.out.println("temp: " + T + "\tdifference: " + difference);
                    counter++;
                    setBestVehiclesList(newSol);
                }
            }

            T *= alpha; // Decreases T, cooling phase
        }

        phmlrp.setSimulatedAnnealing(false);

        phmlrp.resetVehiclesList(bestSol);
        phmlrp.print(false);
//        System.out.println(counter);
    }

    private void doRandomOperation() {
        Random random = new Random();
        int randOpr = random.nextInt(5);

        Operations operations = new Operations(phmlrp);

        switch (randOpr) {
            case 0:
                operations.insertNodeInRoute(true, -1, -1, -1);
                break;
            case 1:
                operations.insertNodeBetweenRoutes(true, -1, -1, -1, -1);
                break;
            case 2:
                operations.swapNodeInRoute(true, -1, -1, -1);
                break;
            case 3:
                operations.swapNodeWithinRoutes(true, -1, -1, -1, -1);
                break;
            case 4:
                operations.edgeOpt(true);
                break;
        }
    }
}
