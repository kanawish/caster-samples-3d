package com.kanawish.glepisodes.ep00

import android.annotation.SuppressLint
import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.Snackbar
import android.view.View
import com.kanawish.glepisodes.R
import com.kanawish.glepisodes.tools.GLHelper
import kotlinx.android.synthetic.main.activity_episodes_00.*
import timber.log.Timber
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Episode 01 Activity
 */
class GLEp00Activity : Activity() {

    @Inject lateinit var glHelper: GLHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_episodes_00)

        // Bail if device doesn't support OpenGL ES 2.0.
        if (!glHelper.isEsVersionSupported(2, 0)) {
            Snackbar.make(rootLayout, R.string.err_version_not_supported, Snackbar.LENGTH_INDEFINITE)
                    .show()
            return
        }

        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(Ep00Renderer())

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

    private inner class Ep00Renderer : GLSurfaceView.Renderer {

        internal var fpsCounter = FpsCounter()

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            Timber.i("Ep00Renderer.onSurfaceCreated()")

            // Set the background clear color of your choice.
            GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f)
        }

        @SuppressLint("DefaultLocale")
        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            Timber.i("Ep00Renderer.onSurfaceChanged(%d, %d)", width, height)

            // We want the OpenGL viewport to match our screen's geometry.
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10) {
            fpsCounter.log()

            // We clear the screen.
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        }
    }

    private inner class FpsCounter {

        var mainThread = Handler(Looper.getMainLooper())

        var measureStart: Long = 0
        var frameCount = FRAME_SAMPLE_SIZE

        fun log() {
            if (frameCount < FRAME_SAMPLE_SIZE) {
                frameCount++
            } else {
                report(System.nanoTime())
                frameCount = 0
                measureStart = System.nanoTime()
            }
        }

        private fun report(measureEnd: Long) {
            val ms: Double
            val elapsed = measureEnd - measureStart

            if (elapsed > 0) {
                ms = elapsed / FRAME_SAMPLE_SIZE.toDouble() / 1000000.0
            } else {
                ms = -1.0
            }

            mainThread.post {
                fpsTextView.text = String.format("%4.1f fps", 1000.0 / ms)
                msTextView.text = String.format("%4.2f ms", ms)
            }
        }

    }

    companion object {
        const val FRAME_SAMPLE_SIZE = 60
    }
}
