package com.kanawish.glepisodes.module.domain;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;


/**
 */
public class GeoScriptEventListener implements ValueEventListener {
    public GeoScriptEventListener() {
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError firebaseError) {
        Timber.d(firebaseError.getMessage());
    }
}
