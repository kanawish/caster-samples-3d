package com.kanawish.gl.utils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Utility methods relating to Matrix and Vector math.
 */
public class MatrixUtils {

    private MatrixUtils() {
    }

    /**
     * It's helpful to have a visual when trying to debug matrix calculations.
     *
     * See {@link android.opengl.Matrix} for more info on column ordering, etc.
     *
     * @param matrix a 4x4 matrix, as per
     * @return a log-friendly text representation of the matrix.
     */
    public static String matrixToString(float[] matrix) {
        if (matrix.length != 16) {
            return "Invalid float[] size, expecting 4x4 matrix.";
        }

        // Format of our matrix, column major style.
        // see the android.opengl.Matrix class for more details.
        String format =
                "|%0$+6.2f,%4$+6.2f,%8$+6.2f,%12$+6.2f|\n" +
                        "|%1$+6.2f,%5$+6.2f,%9$+6.2f,%13$+6.2f|\n" +
                        "|%2$+6.2f,%6$+6.2f,%10$+6.2f,%14$+6.2f|\n" +
                        "|%3$+6.2f,%7$+6.2f,%11$+6.2f,%15$+6.2f|\n";

        return String.format(format, (Object[])ArrayUtils.toObject(matrix));
    }

}
