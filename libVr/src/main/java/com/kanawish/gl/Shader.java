package com.kanawish.gl;

import android.content.Context;
import android.opengl.GLES20;

import com.kanawish.gl.utils.FileUtils;

import java.io.IOException;

import timber.log.Timber;

/**
 * A Shader is writen in GLSL, or 'OpenGL Shading Language'
 * <p>
 * Shader source code is passed to OpenGL for compilation. If compilation is
 * successful, we get back a handle to refer to that specific shader going forward.
 * <p>
 * That handle is later on used, to link shaders together and create an OpenGL 'Program'.
 */
public class Shader {

    private final int type;
    private final String shaderSourceCode;
    private int handle;

    private Shader(int type, String shaderSourceCode) {
        this.type = type;
        this.shaderSourceCode = shaderSourceCode;
    }

    public static int[] compileShadersEp01(Context context) {
        Shader vertexShader = Shader.buildVertexShader(context, "shaders/gles2.ep01.vertshader");
        Shader fragmentShader = Shader.buildFragmentShader(context, "shaders/gles2.ep01.fragshader");
        // Compile the default shaders
        return Shader.compileShaders(vertexShader, fragmentShader);
    }

    public static int[] compileShadersEp02(Context context) {
        Shader vertexShader = Shader.buildVertexShader(context, "shaders/gles2.ep02.vertshader");
        Shader fragmentShader = Shader.buildFragmentShader(context, "shaders/gles2.ep02.fragshader");
        // Compile the default shaders
        return Shader.compileShaders(vertexShader, fragmentShader);
    }

    public static Shader buildVertexShader(Context context, String shaderFilename) {
        try {
            return new Shader(
                    GLES20.GL_VERTEX_SHADER,
                    FileUtils.loadStringFromAsset(context, shaderFilename)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Shader buildFragmentShader(Context context, String shaderFilename) {
        try {
            return new Shader(
                    GLES20.GL_FRAGMENT_SHADER,
                    FileUtils.loadStringFromAsset(context, shaderFilename)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Utility method I created to add a small abstraction layer on top
     * of shader compilation, allows us to wait until episode 2 to dig
     * into that subject.
     *
     * @param shaders
     * @return
     */
    public static int[] compileShaders(Shader... shaders) {
        int[] shaderHandles = new int[shaders.length];

        for (int i = 0; i < shaders.length; i++) {
            shaderHandles[i]=shaders[i].compile();
        }

        return shaderHandles;
    }

    /**
     * This utility method attempts to create and compile an OpenGL shader meeting specifications passed earlier.
     *
     * @return an OpenGL handle to our shader, or 0 if shader creation failed.
     */
    public static int compile(int type, String shaderSourceCode) {
        // Get a handle to an empty shader of our desired type.
        int handle = GLES20.glCreateShader(type);

        if (handle != 0) {
            // Assign source code to the empty shader.
            GLES20.glShaderSource(handle, shaderSourceCode);

            // Attempt compilation of the source code.
            GLES20.glCompileShader(handle);

            // Check if compilation was successful.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If compilation failed, remove the invalid shader from the OpenGL pipeline.
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(handle);
                handle = 0;
            }
        } else {
            Timber.e("Error creating shader.");
        }

        // Log an error + shader source code if compilation
        if (handle == 0) {
            Timber.e("Error compiling shader. \n%s", shaderSourceCode);
        }

        return handle;
    }

    /**
     * Wrapper around static compile() method.
     *
     * @return an OpenGL handle to our shader, or 0 if shader creation failed.
     */
    public int compile() {
        handle = Shader.compile(type, shaderSourceCode);
        return handle;
    }

    public int getHandle() {
        return handle;
    }

    public String getShaderSourceCode() {
        return shaderSourceCode;
    }

    public int getType() {
        return type;
    }
}
