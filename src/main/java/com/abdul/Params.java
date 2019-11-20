package com.abdul;

import com.lexicalscope.jewel.cli.Option;

public interface Params {

    @Option(description = "number of nodes", shortName = "n", longName = "node", defaultValue = "10")
    int getNumNodes();

    @Option(description = "number of hubs", shortName = "h", longName = "hubs", defaultValue = "2")
    int getNumHubs();

    @Option(description = "number of vehicles per hub", shortName = "v", longName = "vehicles", defaultValue = "1")
    int getNumVehicles();

    @Option(description = "collection calculateCost coefficient factor", shortName = "cc", longName = "collectionCost",
            defaultValue = "1.0")
    float getCollectionCostCFactor();

    @Option(description = "distribution calculateCost coefficient factor", shortName = "dc", longName = "distributionCost",
            defaultValue = "1.0")
    float getDistributionCostCFactor();

    @Option(description = "hub-to-hub coefficient factor", shortName = "hh", longName = "hobToHubCost",
            defaultValue = "1.0")
    float getHubToHubCFactor();

    @Option(description = "use city names when displaying/printing", shortName = "ve", longName = "verbose")
    boolean getVerbose();
}
