package com.kanawish.gl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Some hardcoded models and model generation methods.
 */
public class ModelUtils {

    // OpenGL Default
    public static final int BYTES_PER_FLOAT = 4;


    // Number of coordinates per vertex.
    public static final int COORDS_PER_VERTEX = 3;

    // NOTE: Vertices (X,Y,Z) in counter-clockwise order.
    public static float TRIANGLE_VERTICES[] = {
            0.0f,  0.4330f, -2.0f,   // top
           -0.5f, -0.4330f, -2.0f,   // bottom left
            0.5f, -0.4330f, -2.0f    // bottom right
    };


    // Number of colors per vertex.
    public static final int COLOR_PER_VERTEX = 4;

    // NOTE: Red, Green, Blue and Alpha.
    public static float TRIANGLE_COLORS[] = {
            1f, 0f, 0f, 1f, // red
            0f, 1f, 0f, 1f, // green
            0f, 0f, 1f, 1f  // blue
    };


    // Number of combined elements, per vertex.
    public static final int ELEMENTS_PER_VERTEX = COORDS_PER_VERTEX + COLOR_PER_VERTEX ;

    // NOTE: An OpenGL Vertex can hold more than coordinate data. Here, we add the color to the vertices.
    public static float TRIANGLE[] = {
            0.0f,  0.4330f, 0.0f,   // top
            1f, 0f, 0f, 1f,         // red
            -0.5f, -0.4330f, 0.0f,  // bottom left
            0f, 1f, 0f, 1f,         // green
            0.5f, -0.4330f, 0.0f,   // bottom right
            0f, 0f, 1f, 1f          // blue
    };

    private ModelUtils() {
    }

    /**
     * This method is used to build a FloatBuffer from one of the model float arrays
     * defined above.
     *
     * @param modelData an array that contains the model data.
     * @return a buffer that contains a copy of the model data.
     */
    public static FloatBuffer buildFloatBuffer( float[] modelData ) {
        return ByteBuffer
                .allocateDirect(modelData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(modelData);
    }

}
