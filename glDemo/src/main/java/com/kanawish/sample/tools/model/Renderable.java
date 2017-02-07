package com.kanawish.sample.tools.model;

import com.kanawish.sample.tools.utils.ShaderCompileException;

/**
 * Created by kanawish on 2015-07-21.
 */
public interface Renderable {
    void initBuffers();

    void initGlProgram() throws ShaderCompileException;

    void initHandles();

    void update(float[] perspectiveMatrix, float[] viewMatrix);

    void draw();
}
