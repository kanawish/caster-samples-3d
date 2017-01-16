package com.kanawish.sample.gldemo.module;

import android.app.Activity;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.config.Module;
import toothpick.smoothie.module.SmoothieActivityModule;

/**
 * Created by kanawish on 2017-01-03.
 */

public class ActivityModule extends Module {

    public ActivityModule() {
    }

    public static final Scope buildActivityScope(Activity activity) {
        Scope scope = Toothpick.openScopes(activity.getApplication(), activity);
        scope.installModules(
                new SmoothieActivityModule(activity)
        );
        return scope;
    }
}
