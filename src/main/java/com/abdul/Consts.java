package com.abdul;

import java.util.HashMap;
import java.util.Map;

class Consts {

    static Map<String, Integer> neighborhoods = new HashMap<>() {
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

    static Map<Integer, String> neighborhoodsStr = new HashMap<>() {
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

    static Map<String, Integer> localSearchMap = new HashMap<>() {
        {
            put("Insertion", 0);
            put("Swap", 1);
            put("HubMove", 2);
            put("EdgeOpt", 3);
        }
    };

    static Map<Integer, String> localSearchesStr = new HashMap<>() {
        {
            put(0, "Insertion");
            put(1, "Swap");
            put(2, "HubMove");
            put(3, "EdgeOpt");
        }
    };
}
