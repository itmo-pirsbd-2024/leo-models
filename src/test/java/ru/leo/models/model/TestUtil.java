package ru.leo.models.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestUtil {
    public static float[][] readNumeric(Path file) throws IOException {
        List<String> rows = Files.readAllLines(file);
        int featuresInRowN = rows.getFirst().split(",").length;
        float[][] result = new float[rows.size()][featuresInRowN];
        for (int i = 0; i < rows.size(); i++) {
            String[] vals = rows.get(i).split(",");
            for (int j = 0; j < vals.length; j++) {
                result[i][j] = Float.parseFloat(vals[j]);
            }
        }

        return result;
    }

    public static double[] readPredictions(Path file) throws IOException {
        List<String> rows = Files.readAllLines(file);
        double[] result = new double[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            result[i] = Double.parseDouble(rows.get(i));
        }

        return result;
    }
}
