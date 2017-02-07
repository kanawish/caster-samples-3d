package com.kanawish.glepisodes.module.domain;

import android.app.Application;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kanawish.sample.tools.domain.PipelineProgramBus;
import com.kanawish.sample.tools.model.ScriptData;
import com.kanawish.sample.tools.model.ShaderData;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by ecaron on 15-10-27.
 * <p>
 * This will publish geoScript & shader events.
 */
@Singleton
public class FirebaseManager implements ScriptManager {

    public static final String GEO_SCRIPT = "geo_script";
    public static final String VERTEX_SHADER = "vertex_shader";
    public static final String FRAGMENT_SHADER = "fragment_shader";

    private final FirebaseDatabase firebaseRef;

    private final ValueEventListener geoListener;
    private final ValueEventListener vertexListener;
    private final ValueEventListener fragmentListener;

    @Inject
    public FirebaseManager(Application app, PipelineProgramBus bus) {

        firebaseRef = FirebaseDatabase.getInstance();

        geoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ScriptData value = dataSnapshot.getValue(ScriptData.class);
                if (value == null) return;
                bus.publishGeoScript(value.getCode());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Timber.e(firebaseError.toException(), firebaseError.toString());
            }
        };
        firebaseRef.getReference(GEO_SCRIPT).addValueEventListener(geoListener);

        vertexListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ShaderData value = dataSnapshot.getValue(ShaderData.class);
                if (value == null) return;
                bus.publishVertexShader(value.getCode());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Timber.e(firebaseError.toException(), firebaseError.toString());
            }
        };
        firebaseRef.getReference(VERTEX_SHADER).addValueEventListener(vertexListener);

        fragmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ShaderData value = dataSnapshot.getValue(ShaderData.class);
                if (value == null) return;
                bus.publishFragmentShader(value.getCode());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Timber.e(firebaseError.toException(), firebaseError.toString());
            }
        };
        firebaseRef.getReference(FRAGMENT_SHADER).addValueEventListener(fragmentListener);
    }

    /**
     * For outside users, we simply expose the firebase for now.
     *
     * @return firebaseRef
     */
    public FirebaseDatabase getFirebaseRef() {
        return firebaseRef;
    }

    /**
     * What we need usually would give up 'onDestroy'.
     * <p>
     * My understanding right now since this Manager is effectively an Application-level
     * singleton, we should not need to call this directly.
     * <p>
     * Including destroy for the case where we might want to make this an Activity-level singleton.
     */
    public void destroy() {
        firebaseRef.getReference("fragment_shader").removeEventListener(fragmentListener);
        firebaseRef.getReference("vertex_shader").removeEventListener(vertexListener);
        firebaseRef.getReference("geoData").removeEventListener(geoListener);
    }
}
