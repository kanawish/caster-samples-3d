package com.kanawish.sample.tools.domain;


import com.kanawish.sample.tools.model.GeometryData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 */
@Singleton
public class PipelineProgramBus {

    // NOTE: Ideally, we need a 'forked input' bus here, for Geo script/data.

    // Geometry ScriptData bus.
    private final Subject<String> geoScriptBus;
    {
        // Behavior will provide latest value and subsequent changes to subscribers.
        BehaviorSubject<String> subject = BehaviorSubject.create();
        geoScriptBus = subject.toSerialized();
    }

    // Geometry Data bus.
    private final Subject<GeometryData> geoDataBus;
    {
        // Behavior will provide latest value and subsequent changes to subscribers.
        BehaviorSubject<GeometryData> subject = BehaviorSubject.create();
        geoDataBus = subject.toSerialized();
    }

    private final Subject<String> vertexShaderBus;
    {
        // Behavior will provide latest value and subsequent changes to subscribers.
        BehaviorSubject<String> subject = BehaviorSubject.create();
        vertexShaderBus = subject.toSerialized();
    }

    private final Subject<String> fragmentShaderBus;
    {
        // Behavior will provide latest value and subsequent changes to subscribers.
        BehaviorSubject<String> subject = BehaviorSubject.create();
        fragmentShaderBus = subject.toSerialized();
    }

    @Inject
    public PipelineProgramBus() {
    }

    public Observable<String> geoScriptBus() {
        return geoScriptBus.doOnNext(s-> Timber.i("Test GSB %d",s.length()));
    }

    public void publishGeoScript(String script) {
        geoScriptBus.onNext(script);
    }

    public Observable<GeometryData> geoDataBus() {
        return geoDataBus.doOnNext(s-> Timber.i("Test G_D_B"));
    }

    public void publishGeoData(GeometryData geometryData) {
        geoDataBus.onNext(geometryData);
    }

    public Observable<String> vertexShaderBus() {
        return vertexShaderBus;
    }

    public void publishVertexShader(String shader) {
        vertexShaderBus.onNext(shader);
    }

    public Observable<String> fragmentShaderBus() {
        return fragmentShaderBus;
    }

    public void publishFragmentShader(String shader) {
        fragmentShaderBus.onNext(shader);
    }

}
