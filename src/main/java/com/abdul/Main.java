package com.abdul;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

public class Main {
    public static void main(String[] args) {
        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

//            run algorithm
        switch (params.getAlgorithm()) {
            case SA:
                SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(params);
                simulatedAnnealing.runSA();
                break;
            case VNS:
                VNS vns = new VNS(params);
                vns.runVNS();
                break;
            case GVNS:
                GVNS gVNS = new GVNS(params);
                gVNS.runGVNS();
                break;
        }
    }
}
