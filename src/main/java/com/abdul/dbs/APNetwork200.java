package com.abdul.dbs;

public class APNetwork200 {

    public double getDistance(int node1, int node2) {
        if (node1 < 50) return distance50[node1][node2];
        if (node1 < 100) return distance100[node1-50][node2];
        if (node1 < 150) return distance150[node1-100][node2];
        return distance200[node1-150][node2];
    }

    private final double[][] distance50 = new double[][]{

    };

    private final double[][] distance100 = new double[][]{

    };

    private final double[][] distance150 = new double[][]{

    };

    private final double[][] distance200 = new double[][]{

    };
}
