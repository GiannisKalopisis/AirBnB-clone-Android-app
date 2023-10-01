package com.dit.airbnb.util.vector_init;

import com.dit.airbnb.service.RecommendationService;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.util.Precision;

import java.util.Random;

public class UniformInitializer implements VectorInitializer {

    public final double left;
    public final double right;

    public static Random random = new Random();

    public UniformInitializer(double left, double right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Pair<double[][], double[][]> init(int numberOfUsers, int numberOfItems, int K) {
        double[][] V = new double[numberOfUsers][K];
        double[][] F = new double[K][numberOfItems];

        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = 0; j < K; j++) {
                V[i][j] = Precision.round(left + (right - left) * random.nextDouble(), RecommendationService.SCALE);
            }
        }
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < numberOfItems; j++) {
                F[i][j] = Precision.round(left + (right - left) * random.nextDouble(), RecommendationService.SCALE);
            }
        }
        return new Pair<>(V, F);
    }
}
