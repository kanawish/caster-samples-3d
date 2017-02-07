package com.kanawish.glepisodes.ep02;

import android.opengl.Matrix;

import com.kanawish.gl.utils.MatrixUtils;

/**
 * The purpose of a 'Camera' is to transform 'world space' coordinates to 'eye space' coordinates.
 */
public class Camera {

    // Matrix containing the world-to-eye transform.
    private float[] viewMatrix = new float[16];

    public Camera() {
        viewMatrix = buildViewMatrix();
    }

    public static float[] buildViewMatrix() {
        float[] viewMatrix = new float[16];

        // This call sets up the viewMatrix.
        Matrix.setLookAtM(
                viewMatrix, 0,  // result array, offset
                0, 0, -3,       // coordinates for our 'eye'
                0f, 0f, 0f,     // center of view
                0f, 1.0f, 0.0f  // 'up' vector
        );

        return viewMatrix;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    @Override
    public String toString() {
        return MatrixUtils.matrixToString(viewMatrix);
    }

}