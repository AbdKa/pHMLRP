package com.abdul;

/**
 * initial solution construction strategy
 **/

public enum IS {
    RND, // random,
    GREEDY,
    GREEDY_RND, // greedily pick hubs & randomly assign nodes
    RND_GREEDY, // randomly pick hubs & greedily assign nodes
    GREEDY_GRB, // greedily pick hubs & Gurobi assign nodes
    GRB // Gurobi
}
