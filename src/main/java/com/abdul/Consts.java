package com.abdul;

import java.util.HashMap;
import java.util.Map;

class Consts {

    static final String[] instances = new String[]{
//            "TR16.16.3.1", "TR16.16.3.2", "TR16.16.3.3",
//            "TR16.16.4.1", "TR16.16.4.2", "TR16.16.4.3",
//            "TR16.16.5.1", "TR16.16.5.2",
            "TR.10.2.1", "TR.10.2.2", "TR.10.3.1",
            "TR.15.2.1", "TR.15.2.2",
            "TR.25.2.1", "TR.25.2.5", "TR.25.5.1", "TR.25.5.2",
//            "TR.50.2.1", "TR.50.2.5", "TR.50.5.1", "TR.50.5.2",
//            "TR.81.2.1", "TR.81.2.5", "TR.81.5.1", "TR.81.5.2",
//            "TR.81.9.1", "TR.81.9.2", "TR.81.9.3", "TR.81.9.4", "TR.81.9.5",
//            "CAB.10.2.1", "CAB.10.2.2", "CAB.10.3.1",
//            "CAB.15.2.1", "CAB.15.2.2",
//            "CAB.25.2.1", "CAB.25.2.5", "CAB.25.5.1", "CAB.25.5.2",
//            "AP100.100.5.1", "AP100.100.5.2", "AP100.100.5.5",
//            "AP200.200.10.1", "AP200.200.10.2", "AP200.200.10.5"
    };

    static final Map<String, Integer> neighborhoods = new HashMap<>() {
//        change the order of combination here, if needed
        {
            put("insertNodeBetweenRoutes", 0);
            put("edgeOptWithinRoutes", 1);
            put("insertTwoNodes", 2);
            put("twoOptAlgorithm", 3);
            put("insertNodeInRoute", 4);
            put("swapNodeInRoute", 5);
            put("swapNodeWithinRoutes", 6);
            put("nodesRemoveAndGreedyInsert", 7);
            put("swapHubWithNode", 8);
        }
    };

    static final Map<Integer, String> neighborhoodsStr = new HashMap<>() {
        {
            put(0, "insertNodeBetweenRoutes");
            put(1, "edgeOptWithinRoutes");
            put(2, "insertTwoNodes");
            put(3, "twoOptAlgorithm");
            put(4, "insertNodeInRoute");
            put(5, "swapNodeInRoute");
            put(6, "swapNodeWithinRoutes");
            put(7, "nodesRemoveAndGreedyInsert");
            put(8, "swapHubWithNode");
        }
    };

    static final Map<String, Integer> localSearchMap = new HashMap<>() {
        {
            put("Insertion", 0);
            put("Swap", 1);
            put("HubMove", 2);
            put("EdgeOpt", 3);
        }
    };

    static final Map<Integer, String> localSearchesStr = new HashMap<>() {
        {
            put(0, "Insertion");
            put(1, "Swap");
            put(2, "HubMove");
            put(3, "EdgeOpt");
        }
    };
}
