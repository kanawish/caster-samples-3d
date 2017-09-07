package com.kanawish.gl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Some hardcoded models and model generation methods.
 */
public class ModelUtils {

    // OpenGL Default
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_INT = 4;
    public static final int BYTES_PER_SHORT = 2;
    public static final int BYTES_PER_BYTE = 1;

    // Number of coordinates values per vertex. (x,y,z)
    public static final int VALUES_PER_COORD = 3;

    // Number of values per normal
    public static final int VALUES_PER_NORMAL = 3;

    // Number of colors per vertex.
    public static final int VALUES_PER_COLOR = 4;

    // NOTE: Vertices (X,Y,Z) in counter-clockwise order.
    public static float TRIANGLE_VERTICES[] = {
            0.0f,  0.4330f, -2.0f,   // top
           -0.5f, -0.4330f, -2.0f,   // bottom left
            0.5f, -0.4330f, -2.0f    // bottom right
    };

    // NOTE: Red, Green, Blue and Alpha.
    public static float TRIANGLE_COLORS[] = {
            1f, 0f, 0f, 1f, // red
            0f, 1f, 0f, 1f, // green
            0f, 0f, 1f, 1f  // blue
    };

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
     * FloatBuffer builder.
     *
     * @param modelData an array that contains the model data.
     * @return a buffer that contains a copy of the model data.
     */
    public static FloatBuffer buildFloatBuffer( float[] modelData ) {
        return (FloatBuffer) ByteBuffer
                .allocateDirect(modelData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(modelData)
                .position(0);
    }

    /**
     * IntBuffer builder.
     */
    public static IntBuffer buildIntBuffer( int[] modelData ) {
        return (IntBuffer) ByteBuffer
                .allocateDirect(modelData.length * BYTES_PER_INT)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(modelData)
                .position(0);
    }

    /**
     * IntBuffer builder.
     */
    public static ShortBuffer buildShortBuffer( short[] modelData ) {
        return (ShortBuffer) ByteBuffer
                .allocateDirect(modelData.length * BYTES_PER_INT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(modelData)
                .position(0);
    }

    /**
     * Cube builder.
     *
     * @param size The size we want our cube to be.
     * @return an instance of Ep02Model that stores a cube model.
     */
    public static Ep02Model buildCube(float size) {
        final float n = 1;
        final float halfSize = size * .5f;

        final float[] coordinates = {
                halfSize, halfSize, halfSize, 			-halfSize, halfSize, halfSize,
                -halfSize, -halfSize, halfSize,			halfSize, -halfSize, halfSize,      // 0-1-2-3 front

                halfSize, halfSize, halfSize, 			halfSize, -halfSize, halfSize,
                halfSize, -halfSize, -halfSize, 		halfSize, halfSize, -halfSize,      // 0-3-4-5 right
                // -- front
                halfSize, -halfSize, -halfSize, 		-halfSize, -halfSize, -halfSize,
                -halfSize, halfSize, -halfSize,			halfSize, halfSize, -halfSize,      // 4-7-6-5 back

                -halfSize, halfSize, halfSize, 			-halfSize, halfSize, -halfSize,
                -halfSize, -halfSize, -halfSize,		-halfSize,	-halfSize, halfSize,    // 1-6-7-2 left

                halfSize, halfSize, halfSize, 			halfSize, halfSize, -halfSize,
                -halfSize, halfSize, -halfSize, 		-halfSize, halfSize, halfSize,      // top

                halfSize, -halfSize, halfSize, 			-halfSize, -halfSize, halfSize,
                -halfSize, -halfSize, -halfSize,		halfSize, -halfSize, -halfSize,     // bottom
        };

        final float[] normals = {
                0, 0, n, 0, 0, n, 0, 0, n, 0, 0, n,     // front
                n, 0, 0, n, 0, 0, n, 0, 0, n, 0, 0,     // right
                0, 0, -n, 0, 0, -n, 0, 0, -n, 0, 0, -n, // back
                -n, 0, 0, -n, 0, 0, -n, 0, 0, -n, 0, 0, // left
                0, n, 0, 0, n, 0, 0, n, 0, 0, n, 0,     // top
                0, -n, 0, 0, -n, 0, 0, -n, 0, 0, -n, 0, // bottom
        };

        final int[] indices = {
                0, 1, 2, 0, 2, 3,
                4, 5, 6, 4, 6, 7,
                8, 9, 10, 8, 10, 11,
                12, 13, 14, 12, 14, 15,
                16, 17, 18, 16, 18, 19,
                20, 21, 22, 20, 22, 23
        };

        return new Ep02Model(
                buildFloatBuffer(coordinates),
                buildFloatBuffer(normals),
                buildIntBuffer(indices),
                indices.length);
    }

    /**
     * A generic class to hold model data buffers.
     */
    public static class Ep02Model {
        final FloatBuffer coordinates ;
        final FloatBuffer normals ;
        final IntBuffer indices ;
        final int count;

        public Ep02Model(FloatBuffer coordinates, FloatBuffer normals, IntBuffer indices, int count) {
            this.coordinates = coordinates;
            this.normals = normals;
            this.indices = indices;
            this.count = count;
        }

        public FloatBuffer getCoordinates() {
            return coordinates;
        }

        public FloatBuffer getNormals() {
            return normals;
        }

        public IntBuffer getIndices() {
            return indices;
        }

        public int getCount() {
            return count;
        }
    }

}
