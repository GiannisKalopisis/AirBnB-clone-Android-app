package com.dit.airbnb.util.vector_init;

import org.apache.commons.math3.util.Pair;

public interface VectorInitializer {

    Pair<double[][], double[][]> init(int numberOfUsers, int numberOfItems, int K);

}
