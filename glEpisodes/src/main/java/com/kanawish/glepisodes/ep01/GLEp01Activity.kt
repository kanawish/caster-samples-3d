package com.kanawish.glepisodes.ep01

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.kanawish.functional.PlainConsumer
import com.kanawish.gl.Program
import com.kanawish.gl.Shader
import com.kanawish.gl.utils.FpsCounter
import com.kanawish.gl.utils.ModelUtils
import com.kanawish.glepisodes.R
import com.kanawish.glepisodes.tools.GLHelper
import kotlinx.android.synthetic.main.activity_episodes_01.*
import timber.log.Timber
import java.nio.FloatBuffer
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Episode 01 Activity
 */
class GLEp01Activity : Activity() {

    @Inject lateinit var glHelper: GLHelper

    private val fpsCounter = FpsCounter(PlainConsumer<Double> { this.refreshFps(it) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_episodes_01)

        // Bail if device doesn't support OpenGL ES 2.0.
        if (!glHelper.isEsVersionSupported(2, 0)) {
            Snackbar.make(rootLayout!!, R.string.err_version_not_supported, Snackbar.LENGTH_INDEFINITE)
                    .show()
            return
        }

        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(Ep01Renderer())
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    private fun refreshFps(msAverage: Double?) {
        fpsTextView.text = String.format("%4.1f fps", 1000.0 / msAverage!!)
        msTextView.text = String.format("%4.2f ms", msAverage)
    }

    private inner class Ep01Renderer internal constructor() : GLSurfaceView.Renderer {

        private val triangleVertices: FloatBuffer
        private var projectionMatrix: FloatArray? = null

        private var programHandle: Int = 0
        private var uProjectionMatrixHandle: Int = 0
        private var aPositionHandle: Int = 0

        init {
            triangleVertices = ModelUtils.buildFloatBuffer(ModelUtils.TRIANGLE_VERTICES)
            triangleVertices.position(0)
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            Timber.i("Ep00Renderer.onSurfaceCreated()")

            // Set the background clear color of your choice.
            GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f)

            // Load episode 01 shaders from "assets/", compile them, returns shader handlers.
            val shaderHandles = Shader.compileShadersEp01(this@GLEp01Activity)

            // Link the shaders to form a program, binding attributes
            programHandle = Program.linkProgram(shaderHandles, A_POSITION)

            // TODO: Check handles for 0
            uProjectionMatrixHandle = GLES20.glGetUniformLocation(programHandle, U_VIEW_PROJECTION_MATRIX)
            aPositionHandle = GLES20.glGetAttribLocation(programHandle, A_POSITION)

            GLES20.glUseProgram(programHandle)
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            Timber.i("Ep00Renderer.onSurfaceChanged(%d, %d)", width, height)

            // We want the viewport to match our screen's geometry.
            GLES20.glViewport(0, 0, width, height)

            val ratio = width.toFloat() / height

            // Assign values to the projection matrix.
            projectionMatrix = FloatArray(16)
            Matrix.orthoM(
                    projectionMatrix, 0, // target matrix, offset
                    -ratio, ratio, // left, right
                    -1.0f, 1.0f, // bottom, top
                    0f, 10f         // near, far
            )
        }

        override fun onDrawFrame(gl: GL10) {
            fpsCounter.log()

            // We clear the screen.
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

            // Provide the vertex information to the Vertex Shader
            GLES20.glVertexAttribPointer(
                    aPositionHandle,
                    ModelUtils.VALUES_PER_COORD,
                    GLES20.GL_FLOAT,
                    false,
                    ModelUtils.VALUES_PER_COORD * ModelUtils.BYTES_PER_FLOAT,
                    triangleVertices)
            GLES20.glEnableVertexAttribArray(aPositionHandle)

            // Provide the projection matrix to the Vertex Shader
            GLES20.glUniformMatrix4fv(uProjectionMatrixHandle, 1, false, projectionMatrix, 0)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        }

    }

    companion object {
        private val U_VIEW_PROJECTION_MATRIX = "u_ProjectionMatrix"
        private val A_POSITION = "a_Position"
    }
}