package com.kanawish.glepisodes.ep00;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kanawish.glepisodes.R;
import com.kanawish.glepisodes.module.ScopeBuilder;
import com.kanawish.glepisodes.tools.GLHelper;

import javax.inject.Inject;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

/**
 * Episode 01 Activity
 */
public class GLEp00Activity extends Activity {

    @Inject GLHelper glHelper;

    private RelativeLayout rootLayout;
    private GLSurfaceView glSurfaceView;

    private TextView fpsTextView;
    private TextView msTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scope scope = ScopeBuilder.INSTANCE.buildActivityScope(this);
        Toothpick.inject(this, scope);

        setContentView(R.layout.activity_episodes_00);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        // Bail if device doesn't support OpenGL ES 2.0.
        if (!glHelper.isEsVersionSupported(2, 0)) {
            Snackbar.make(rootLayout, R.string.err_version_not_supported, Snackbar.LENGTH_INDEFINITE)
                    .show();
            return;
        }

        glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new Ep00Renderer());

        fpsTextView = (TextView) findViewById(R.id.fpsTextView);
        msTextView = (TextView) findViewById(R.id.msTextView);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            rootLayout.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private class Ep00Renderer implements GLSurfaceView.Renderer {

        Handler mainThread = new Handler(Looper.getMainLooper());

        FpsCounter fpsCounter = new FpsCounter();

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Timber.i("Ep00Renderer.onSurfaceCreated()");

            // Set the background clear color of your choice.
            GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        }

        @Override
        @SuppressLint("DefaultLocale")
        public void onSurfaceChanged(GL10 gl, final int width, final int height) {
            Timber.i("Ep00Renderer.onSurfaceChanged(%d, %d)", width, height);

            // We want the OpenGL viewport to match our screen's geometry.
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            fpsCounter.log();

            // We clear the screen.
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        }
    }

    private class FpsCounter {

        static final int FRAME_SAMPLE_SIZE = 60;

        Handler mainThread = new Handler(Looper.getMainLooper());

        long measureStart;
        int frameCount = FRAME_SAMPLE_SIZE;

        public void log() {
            if (frameCount < FRAME_SAMPLE_SIZE) {
                frameCount++;
            } else {
                report(System.nanoTime());
                frameCount = 0;
                measureStart = System.nanoTime();
            }
        }

        private void report(final long measureEnd) {
            final double ms;
            long elapsed = measureEnd - measureStart;

            if (elapsed > 0) {
                ms = (elapsed / (double) FRAME_SAMPLE_SIZE) / 1000000d;
            } else {
                ms = -1;
            }

            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    fpsTextView.setText(String.format("%4.1f fps", 1000d / ms));
                    msTextView.setText(String.format("%4.2f ms", ms));
                }
            });
        }
    }
}
