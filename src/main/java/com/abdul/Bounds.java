package com.abdul;

import java.util.HashMap;
import java.util.Map;

public class Bounds {
    private Map<String, Integer> bounds = new HashMap<String, Integer>();

    public Bounds() {
        bounds.put("10.2.1", 3597);
        bounds.put("10.2.2", 2331);
        bounds.put("10.3.1", 2651);
    }

    public int getBound(String constraints) {
        return bounds.get(constraints);
    }
}
