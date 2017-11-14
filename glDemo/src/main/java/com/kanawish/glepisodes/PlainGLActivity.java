package com.kanawish.glepisodes;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.kanawish.gl.utils.FileUtils;
import com.kanawish.glepisodes.module.domain.ScriptManager;
import com.kanawish.sample.tools.domain.CameraManager;
import com.kanawish.sample.tools.domain.GeometryManager;
import com.kanawish.sample.tools.domain.PipelineProgramBus;
import com.kanawish.sample.tools.generation.BasicGenerator;
import com.kanawish.sample.tools.generation.DefaultModels;
import com.kanawish.sample.tools.gl.DebugGLRenderer;
import com.kanawish.sample.tools.model.GeometryData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

/**
 */
public class PlainGLActivity extends Activity {

    // When instantiated, will publish script changes.
    @Inject
    ScriptManager scriptManager;

    // The camera manager will be used to help us move the viewpoint in our scene, etc.
    @Inject
    CameraManager cameraManager;
    @Inject
    PipelineProgramBus programBus;
    @Inject
    GeometryManager geometryManager;
    @Inject
    BasicGenerator basicGenerator;
    GLSurfaceView debugGLSurfaceView;
    private String geoWrapper;
    // Subscriptions to the code updaters
    private CompositeDisposable disposables;
    private DebugGLRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        debugGLSurfaceView = new GLSurfaceView(this);
        debugGLSurfaceView.setEGLContextClientVersion(3);
        renderer = new DebugGLRenderer(this);
        debugGLSurfaceView.setRenderer(renderer);
        debugGLSurfaceView.setOnClickListener(v -> programBus.publishGeoData(basicGenerator.generateScene()));

        setContentView(debugGLSurfaceView);
//      ButterKnife.bind(this);

        // Load geoWrapper from local storage.
        try {
            geoWrapper = FileUtils.loadStringFromAsset(this, "js/wrapper.js");
        } catch (IOException e) {
            Timber.e(e, "Error to load 'js/wrapper.js'");
            throw new RuntimeException("Critical failure, app is missing 'wrapper.js' asset.");
        }

        // Prime the pump, as it were. We want at least a default geometry in place.
        try {
            // NOTE: Pick your poison.
//            String geoJs = FileUtils.loadStringFromAsset(this, "js/bundle.js");
//            programBus.publishGeoScript(geoJs);
            programBus.publishGeoData(generateScene());

            String vertexShader = FileUtils.loadStringFromAsset(this, "shaders/_300.instanced.v5.vs");
            programBus.publishVertexShader(vertexShader);
            String fragmentShader = FileUtils.loadStringFromAsset(this, "shaders/_300.default.v5.fs");
            programBus.publishFragmentShader(fragmentShader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GeometryData generateScene() {
        Timber.d("generateScene()");
        Timber.d("buildCube()");
        GeometryData data = new GeometryData();
        data.objs = new ArrayList<>();
        float deg = (float) (Math.PI / 2f);
        GeometryData.Instanced i =
                new GeometryData.Instanced(1, new float[][]{{-0.0f, 0.0f, -15f}, {deg / 2, deg / 2, 0}, {1, 1, 1}, {1.0f, 0.0f, 1.0f, 1}, {1, 0, 0, 0}});
        GeometryData.Obj obj = new GeometryData.Obj(DefaultModels.CUBE_COORDS, DefaultModels.CUBE_NORMALS, i);
        data.objs.add(obj);
        return data;
//        return DefaultModels.buildCube();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.subscribeProgramBus();
    }

    @Override
    protected void onPause() {
        disposables.dispose();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Toothpick.closeScope(this);
        super.onDestroy();
    }

    public void subscribeProgramBus() {
        disposables = new CompositeDisposable();
        disposables.add(
                Observable
                        .combineLatest(
                                programBus.vertexShaderBus().doOnNext(shader -> Timber.d("Vector Shader code changed.")),
                                programBus.fragmentShaderBus().doOnNext(shader -> Timber.d("Fragment Shader code changed.")),
                                programBus
                                        .geoScriptBus()
                                        .doOnNext(script->Timber.d("Got geoScript, length: %d", script.length()))
                                        .debounce(500, TimeUnit.MILLISECONDS)
                                        .doOnNext(script->Timber.d("Debounced geoScript, length: %d", script.length()))
                                        .map(script -> String.format(geoWrapper, script)) // TODO: Came from original rhino setup, might be removeable
                                        .doOnNext(script->Timber.d("Wrapped geoScript, length: %d", script.length()))
                                        .observeOn(Schedulers.computation())
                                        .map(geometryManager::webviewGeometryData)
                                        .doOnError(throwable -> Timber.e(throwable, "GeometryScript failed to execute."))
                                        .retryWhen(e -> e.flatMap( i -> Observable.timer(5000, TimeUnit.MILLISECONDS)))
                                        .doOnNext(shader -> Timber.d("Geometry changed.")),
                                Program::new
                        )
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .doOnNext(shader -> Timber.d("GL Program component changed."))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( program -> {
                            debugGLSurfaceView.queueEvent(() -> {
                                // TODO: Under the hood, in the renderer, the threading approach was broken. Go review when time permits / when working on examples.
                                renderer.updateVertexShader(program.vertexShader);
                                renderer.updateFragmentShader(program.fragmentShader);
                                renderer.updateGeometryData(program.geometryData);
                            });
                        } )
        );

    }

    private class Program {
        final String vertexShader;
        final String fragmentShader;
        final GeometryData geometryData;

        public Program(String vertexShader, String fragmentShader, GeometryData geometryData) {
            this.vertexShader = vertexShader;
            this.fragmentShader = fragmentShader;
            this.geometryData = geometryData;
        }
    }
}
