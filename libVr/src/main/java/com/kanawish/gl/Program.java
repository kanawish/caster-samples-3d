package com.kanawish.gl;

import android.opengl.GLES20;

import timber.log.Timber;

/**
 * An OpenGL program is a linked collection of shaders.
 * <p>
 * Before linking a program, we bind attributes to it. Binding
 * attributes is the first step in passing our vertex information
 * into the OpenGL programmable pipeline.
 */
public class Program {

    private final int[] shaderHandles;
    private final String[] vertexAttributes;

    private int programHandle;
    private int[] attribLocations;

    public Program(int[] shaderHandles, String[] vertexAttributes) {
        this.shaderHandles = shaderHandles;
        this.vertexAttributes = vertexAttributes;
    }

    /**
     * Attach shaders to a program,
     * Bind vertex attributes to program,
     * Link the program,
     * Clean up on errors.
     *
     * @param shaderHandles to be attached to program.
     * @param vertexAttributes to bind to the program, order is important.
     * @return programHandle on success, 0 on failure.
     */
    public static int linkProgram(int[] shaderHandles, String... vertexAttributes) {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            // Attach shaders to program.
            for (int shaderHandle : shaderHandles) {
                GLES20.glAttachShader(programHandle, shaderHandle);
            }

            // Bind vertex attributes.
            for (int i = 0; i < vertexAttributes.length; i++) {
                // We assign the index. It's what returned by glGetAttribLocation.
                GLES20.glBindAttribLocation(programHandle, i, vertexAttributes[i]);
            }

            // Link the shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            Timber.e("Error creating program.");
        }

        return programHandle;
    }

    /**
     *
     * @param programHandle
     * @param vertexAttributes
     * @return
     */
    public static int[] attribLocations(int programHandle, String[] vertexAttributes) {
        int[] locations = new int[vertexAttributes.length];
        for (int i = 0; i < vertexAttributes.length; i++) {
            locations[i] = GLES20.glGetAttribLocation(programHandle, vertexAttributes[i]);
        }
        return locations;
    }

    /**
     * This method first links the program, then gets all the attrib locations,
     * and assigns them to the class instance's attribLocations.
     *
     * @return a handle to the linked program.
     */
    public int initialize() {
        programHandle = Program.linkProgram(shaderHandles, vertexAttributes);
        if (programHandle != 0) {
            attribLocations = Program.attribLocations(programHandle, vertexAttributes);
        }

        return programHandle;
    }
}
