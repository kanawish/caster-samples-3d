package com.kanawish.androidvrtalk.ui

import android.os.Bundle
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.vr.sdk.base.GvrActivity
import com.kanawish.gl.utils.FileUtils
import com.kanawish.glepisodes.R
import com.kanawish.glepisodes.module.domain.GeoScriptEventListener
import com.kanawish.glepisodes.module.domain.ScriptManager
import com.kanawish.glepisodes.module.domain.VertexShaderEventListener
import com.kanawish.sample.mvi.di.ActivityModule
import com.kanawish.sample.tools.domain.CameraManager
import com.kanawish.sample.tools.domain.DebugData
import com.kanawish.sample.tools.domain.GeometryManager
import com.kanawish.sample.tools.domain.PipelineProgramBus
import com.kanawish.sample.tools.gl.LiveStereoRenderer
import com.kanawish.sample.tools.model.GeometryData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.vr_talk_ui.*
import timber.log.Timber
import toothpick.Toothpick
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/*

NOTE: The following diagrams can be viewed in Android Studio using the PlantUML plugin.

VANILLA OPENGL CLASS DIAGRAM

@startuml
View <|-- GLSurfaceView
GLSurfaceView *-right- GLSurfaceView.Renderer
hide View members
hide GLSurfaceView members
hide GLSurfaceView.Renderer attributes

class View

class GLSurfaceView {
 - GLSurfaceView.Renderer renderer
 + setRenderer()
}

class GLSurfaceView.Renderer {
    +onSurfaceCreated()
    +onSurfaceChanged()
    +onDrawFrame()
}
@enduml


VANILLA THREADING / RENDER LOOP

@startuml
    control UI_Thread
    control GL_Render_Thread

    GL_Render_Thread -> Renderer: draw( )
    activate Renderer
    UI_Thread --> GL_Render_Thread: queueEvent(Runnable)
    GL_Render_Thread <-- Renderer:
    deactivate Renderer

    GL_Render_Thread <-- GL_Render_Thread: runQueued()

GL_Render_Thread -> Renderer: draw( )
activate Renderer
GL_Render_Thread <-- Renderer:
deactivate Renderer

GL_Render_Thread -> Renderer: draw( )
activate Renderer
GL_Render_Thread <-- Renderer:
deactivate Renderer

 ...etc......
 @enduml


CARDBOARD CLASS DIAGRAM

@startuml
hide attributes
hide CardboardView methods
hide GLSurfaceView methods

class CardboardActivity {
    +void onCardboardTrigger ()
}
class GLSurfaceView
class CardboardView
class CardboardView.StereoRenderer {
    +onRendererShutdown()
    +onSurfaceChanged(width, height)
    +onSurfaceCreated(config)
    +onNewFrame(headTransform)
    +onDrawEye(eye)
    +onFinishFrame(viewport)
}

GLSurfaceView <|---CardboardView
CardboardActivity *-- CardboardView
CardboardView*--CardboardView.StereoRenderer

@enduml

 */
class VrTalkActivity : GvrActivity() {

    private val doubleTapListener = object : GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            //            InputData data = new InputDataBuilder().setType(InputData.SINGLE_TAP_CONFIRMED).setEvent1(e).createInputData();

            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // ?
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            //            InputData data = new InputDataBuilder().setType(InputData.DOUBLE_TAP).setEvent1(e).createInputData();
            return true
        }
    }

    private val gestureListener = object : GestureDetector.OnGestureListener {
        // Added attribute to avoid new CameraManager.CameraData() perf overhead.
        internal var cd = CameraManager.CameraData()

        override fun onDown(e: MotionEvent): Boolean {
            //            InputData data = new InputDataBuilder().setType(InputData.DOWN).setEvent1(e).createInputData();

            return true
        }

        override fun onShowPress(e: MotionEvent) {}

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            //            InputData data = new InputDataBuilder().setType(InputData.SINGLE_TAP_UP).setEvent1(e).createInputData();

            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            //            InputData data = new InputDataBuilder()
            //                    .setType(InputData.SCROLL)
            //                    .setEvent1(e1)
            //                    .setEvent2(e2)
            //                    .setParamX(distanceX)
            //                    .setParamY(distanceY)
            //                    .createInputData();

            Timber.d("onScroll - %1.2f,%1.2f", distanceX, distanceY)
            cd.cameraRotation = floatArrayOf(distanceX, distanceY, 0f)
            cd.cameraTranslation = floatArrayOf(0f, 0f, 0f)

            cameraManager!!.applyDelta(cd)
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            //            InputData data = new InputDataBuilder().setType(InputData.LONG_PRESS).setEvent1(e).createInputData();
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            //            InputData data = new InputDataBuilder()
            //                    .setType(InputData.FLING)
            //                    .setEvent1(e1)
            //                    .setEvent2(e2)
            //                    .setParamX(velocityX)
            //                    .setParamY(velocityY)
            //                    .createInputData();

            return true
        }
    }

/*
    @BindView(R.id.lline1)
    internal var lline1: TextView? = null
    @BindView(R.id.lline2)
    internal var lline2: TextView? = null
    @BindView(R.id.lline3)
    internal var lline3: TextView? = null
    @BindView(R.id.lline4)
    internal var lline4: TextView? = null
    @BindView(R.id.debugTextLeft)
    internal var debugTextLeft: LinearLayout? = null

    @BindView(R.id.rline1)
    internal var rline1: TextView? = null
    @BindView(R.id.rline2)
    internal var rline2: TextView? = null
    @BindView(R.id.rline3)
    internal var rline3: TextView? = null
    @BindView(R.id.rline4)
    internal var rline4: TextView? = null
    @BindView(R.id.debugTextRight)
    internal var debugTextRight: LinearLayout? = null

    @BindView(R.id.fpsTextView)
    internal var fpsTextView: TextView? = null

    @BindView(R.id.ui_layout)
    internal var uiLayout: RelativeLayout? = null

    @BindView(R.id.gvr_view)
    internal var cardboardView: GvrView? = null
*/

    private val vertexShaderEventListener: VertexShaderEventListener? = null
    private val fragmentShaderEventListener: VertexShaderEventListener? = null
    private val geoEventListener: GeoScriptEventListener? = null

    @Inject lateinit var scriptManager: ScriptManager

    // The camera manager will be used to help us move the viewpoint in our scene, etc.
    @Inject lateinit var cameraManager: CameraManager
    @Inject lateinit var programBus: PipelineProgramBus
    @Inject lateinit var geometryManager: GeometryManager

    // TODO: Inject
    lateinit var renderer: LiveStereoRenderer

    private var geoWrapper: String? = null

    // Subscriptions to the code updaters
    private val disposables = CompositeDisposable()


    // UI / CONTROLS
    private var gestureDetector: GestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.vr_talk_ui)

        /*
        // Configure the cardboardView. There's a lot of available options, here's two.
        cardboardView.setVRModeEnabled(true);
        // FIXME: Currently broken, due to pipeline setup.
        cardboardView.setDistortionCorrectionEnabled(false);
*/


        // Assign the view to this CardboardActivity
        gvrView = gvr_view

        // Create & assign the renderer that does the actual drawing.
        renderer = LiveStereoRenderer(this, cameraManager)
        gvrView.setRenderer(renderer)

        // This will subscribe to a head tracking info stream, we overlay it to help debug scenes.
        subscribeToDebugPublisher(renderer.debugOutputPublishSubject)

        // Load geoWrapper from local storage.
        try {
            geoWrapper = FileUtils.loadStringFromAsset(this, "js/wrapper.js")
        } catch (e: IOException) {
            Timber.e(e, "Error to load 'js/wrapper.js'")
            throw RuntimeException("Critical failure, app is missing 'wrapper.js' asset.")
        }

        // TODO: An explanatory overlay would be nice, like in some examples.

        // Setup touch input
        gestureDetector = GestureDetector(this@VrTalkActivity, gestureListener)
        gestureDetector!!.setOnDoubleTapListener(doubleTapListener)

        gvrView.setOnTouchListener { v, event -> gestureDetector!!.onTouchEvent(event) }
    }

    override fun onResume() {
        super.onResume()

        subscribeProgramBus()
        renderer.play()
    }

    fun subscribeProgramBus() {
        // FIXME All this is broken now!!!

        disposables += programBus
                .geoScriptBus()
                .doOnNext { script -> Timber.d("Got geoScript, length: %d", script.length) }
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext { script -> Timber.d("Debounced geoScript, length: %d", script.length) }
                .map { script -> String.format(geoWrapper!!, script) } // TODO: Came from original rhino setup, might be removeable
                .doOnNext { script -> Timber.d("Wrapped geoScript, length: %d", script.length) }
                .observeOn(Schedulers.computation())
                //                .map(script -> GeometryManager.rhinoGeometryData(script))
                //                .map(script -> GeometryManager.duktapeGeometryData(script))
                .map<GeometryData>({ geometryManager.webviewGeometryData(it) })
                .doOnError { throwable -> Timber.e(throwable, "GeometryScript failed to execute.") }
                .retryWhen { e -> e.flatMap { i -> Observable.timer(5000, TimeUnit.MILLISECONDS) } }
                .subscribe { data -> renderer.updateGeometryData(data) }

        disposables += programBus
                .vertexShaderBus()
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext { shader -> Timber.d("Vector Shader code changed.") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { shader -> gvrView.queueEvent { renderer.updateVertexShader(shader) } }

        disposables += programBus
                .fragmentShaderBus()
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext { shader -> Timber.d("Fragment Shader code changed.") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { shader -> gvrView.queueEvent { renderer.updateFragmentShader(shader) } }
    }


    override fun onPause() {
        super.onPause()
        renderer.pause()

        disposables.clear()
    }

    override fun onDestroy() {
        Toothpick.closeScope(this)
        super.onDestroy()
    }

    // Moved this out of the onCreate() method to avoid confusing people new to Rx.
    private fun subscribeToDebugPublisher(publisher: PublishSubject<DebugData>) {
        publisher
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<DebugData> {
                    internal var redColor = resources.getColor(R.color.red)
                    internal var greenColor = resources.getColor(R.color.green)

                    override fun accept(debugData: DebugData) {
                        if (debugData.type == DebugData.Type.RIGHT) {
                            rline1!!.text = debugData.line1
                            rline2!!.text = debugData.line2
                            rline3!!.text = debugData.line3
                            rline4!!.text = debugData.line4
                        } else {
                            lline1!!.text = debugData.line1
                            lline2!!.text = debugData.line2
                            lline3!!.text = debugData.line3
                            lline4!!.text = debugData.line4
                            fpsTextView!!.text = String.format("%.0fms / %.0ffps", debugData.fps, 1000.0 / debugData.fps)
                            fpsTextView!!.setTextColor(if (debugData.isCompileError) redColor else greenColor)
                        }
                    }
                })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var handled = true
        when (keyCode) {
            KeyEvent.KEYCODE_A -> cameraManager.applyTranslationDelta(D, 0f, 0f)
            KeyEvent.KEYCODE_S -> cameraManager.applyTranslationDelta(0f, 0f, -D) // -z is forward
            KeyEvent.KEYCODE_D -> cameraManager.applyTranslationDelta(-D, 0f, 0f)
            KeyEvent.KEYCODE_W -> cameraManager.applyTranslationDelta(0f, 0f, D)
            KeyEvent.KEYCODE_Q -> cameraManager.applyTranslationDelta(0f, -D, 0f)
            KeyEvent.KEYCODE_E -> cameraManager.applyTranslationDelta(0f, D, 0f)
            else -> handled = false
        }

        return handled
    }

    companion object {
        internal val D = 0.1f
    }

}
