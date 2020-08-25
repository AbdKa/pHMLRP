package com.abdul;

import java.util.HashMap;
import java.util.Map;

class Consts {

    static Map<String, Integer> neighborhoods = new HashMap<String, Integer>() {
        {
            put("insertNodeBetweenRoutes", 0);
            put("edgeOpt", 1);
            put("insertTwoNodes", 2);
            put("twoOptAlgorithm", 3);
            put("insertNodeInRoute", 4);
            put("swapNodeInRoute", 5);
            put("swapNodeWithinRoutes", 6);
            put("nodesRemoveAndGreedyInsert", 7);
            put("swapHubWithNode", 8);
        }
    };

    static Map<Integer, String> neighborhoodsStr = new HashMap<Integer, String>() {
        {
            put(0, "insertNodeBetweenRoutes");
            put(1, "edgeOpt");
            put(2, "insertTwoNodes");
            put(3, "twoOptAlgorithm");
            put(4, "insertNodeInRoute");
            put(5, "swapNodeInRoute");
            put(6, "swapNodeWithinRoutes");
            put(7, "nodesRemoveAndGreedyInsert");
            put(8, "swapHubWithNode");
        }
    };
}
