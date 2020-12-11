package com.abdul;

import com.lexicalscope.jewel.cli.Option;

public interface Params {

    @Option(description = "Path", shortName = "p", longName = "path", defaultValue = "results")
    String getResultPath();

    @Option(description = "Dataset", shortName = "d", longName = "dataset", defaultValue = "TR")
    String getDataset();

    @Option(description = "number of nodes", shortName = "n", longName = "node", defaultValue = "50")
    int getNumNodes();

    @Option(description = "number of hubs", shortName = "h", longName = "hubs", defaultValue = "5")
    int getNumHubs();

    @Option(description = "number of vehicles per hub", shortName = "v", longName = "vehicles", defaultValue = "2")
    int getNumVehicles();

    @Option(description = "number of links", shortName = "q", longName = "links", defaultValue = "8")
    int getNumLinks();

    @Option(description = "collection calculateCost coefficient factor", shortName = "a", longName = "collectionCost",
            defaultValue = "1.0")
    float getCollectionCostCFactor();

    @Option(description = "distribution calculateCost coefficient factor", shortName = "b", longName = "distributionCost",
            defaultValue = "1.0")
    float getDistributionCostCFactor();

    @Option(description = "hub-to-hub coefficient factor", shortName = "c", longName = "hobToHubCost",
            defaultValue = "1.0")
    float getHubToHubCFactor();

    @Option(description = "percentage of nodes removal", shortName = "f", longName = "removalPercentage",
            defaultValue = "0.2")
    float getRemovalPercentage();

    @Option(description = "use city names when displaying/printing", shortName = "g", longName = "verbose")
    boolean getVerbose();

    @Option(description = "InitSol", shortName = "i", longName = "InitSol", defaultValue = "random")
    String getInitSol();

    @Option(description = "number of runs", shortName = "r", longName = "runs", defaultValue = "100")
    int getNumRuns();

    @Option(description = "replica per combination", shortName = "e", longName = "runs", defaultValue = "10")
    int getNumReplicasPerCombination();
}
