package com.haru.SwipeStyle.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsineSimilarity {
    public static double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) return -1;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            double a = vec1.get(i);
            double b = vec2.get(i);
            dotProduct += a * b;
            normA += Math.pow(a, 2);
            normB += Math.pow(b, 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static List<Double> averageVectors(List<List<Double>> vectors) {
        int size = vectors.get(0).size();
        List<Double> result = new ArrayList<>(Collections.nCopies(size, 0.0));

        for (List<Double> vec : vectors) {
            for (int i = 0; i < size; i++) {
                result.set(i, result.get(i) + vec.get(i));
            }
        }

        int count = vectors.size();
        for (int i = 0; i < size; i++) {
            result.set(i, result.get(i) / count);
        }

        return result;
    }


}
