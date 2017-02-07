package com.kanawish.glepisodes.ep01;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.RelativeLayout;

import com.kanawish.gl.utils.ModelUtils;
import com.kanawish.glepisodes.R;
import com.kanawish.glepisodes.module.ScopeBuilder;
import com.kanawish.glepisodes.module.app.GLHelper;

import java.nio.FloatBuffer;

import javax.inject.Inject;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

/**
 * Episode 01 Activity
 */
public class GLEp01Activity extends Activity {

    @Inject GLHelper glHelper;

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scope scope = ScopeBuilder.buildActivityScope(this);
        Toothpick.inject(this, scope);

        setContentView(R.layout.activity_episodes_default);
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        // Bail if device doesn't support OpenGL ES 2.0.
        if (!glHelper.isEsVersionSupported(2, 0)) {
            Snackbar.make(rootLayout, R.string.err_version_not_supported, Snackbar.LENGTH_INDEFINITE)
                    .show();
            return;
        }

        glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        glSurfaceView.setEGLContextClientVersion(2);

        glSurfaceView.setRenderer(new Ep01Renderer());
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toothpick.closeScope(this);
    }

    private class Ep01Renderer implements GLSurfaceView.Renderer {

        private static final String U_VIEW_PROJECTION_MATRIX = "u_ProjectionMatrix";
        private static final String A_POSITION = "a_Position";

        private final FloatBuffer triangleVertices;
        private float[] projectionMatrix;

        private int programHandle;
        private int uProjectionMatrixHandle;
        private int aPositionHandle;

        public Ep01Renderer() {
            triangleVertices = ModelUtils.buildFloatBuffer(ModelUtils.TRIANGLE_VERTICES);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Timber.i("Ep00Renderer.onSurfaceCreated()");

            // Set the background clear color of your choice.
            GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

            // Load episode 01 shaders from "assets/", compile them, returns shader handlers.
            int[] shaderHandles = Shader.compileShadersEp01(GLEp01Activity.this);

            // Link the shaders to form a program, binding attributes
            programHandle = Program.linkProgram(shaderHandles, A_POSITION);

            // TODO: Check handles for 0
            uProjectionMatrixHandle = GLES20.glGetUniformLocation(programHandle, U_VIEW_PROJECTION_MATRIX);
            aPositionHandle = GLES20.glGetAttribLocation(programHandle, A_POSITION);

            GLES20.glUseProgram(programHandle);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Timber.i("Ep00Renderer.onSurfaceChanged(%d, %d)", width, height);

            // We want the viewport to match our screen's geometry.
            GLES20.glViewport(0, 0, width, height);

            final float ratio = (float) width / height;

            // Assign values to the projection matrix.
            projectionMatrix = new float[16];
            Matrix.orthoM(
                    projectionMatrix, 0,    // target matrix, offset
                    -ratio, ratio,  // left, right
                    -1.0f, 1.0f,    // bottom, top
                    0f, 10f         // near, far
            );

        }

        @Override
        public void onDrawFrame(GL10 gl) {

            // We clear the screen.
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            // TODO: Check if we need to reset this every time?
            // TODO: If not, we should set position only once, perhaps right after creation.
            triangleVertices.position(0);

            GLES20.glVertexAttribPointer(
                    aPositionHandle,
                    ModelUtils.COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT,
                    false,
                    ModelUtils.COORDS_PER_VERTEX * ModelUtils.BYTES_PER_FLOAT,
                    triangleVertices );

            GLES20.glEnableVertexAttribArray(aPositionHandle);

            GLES20.glUniformMatrix4fv(uProjectionMatrixHandle, 1, false, projectionMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }
    }
}
