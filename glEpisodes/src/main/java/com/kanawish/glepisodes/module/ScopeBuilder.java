package com.kanawish.glepisodes.module;

import android.app.Activity;
import android.app.Application;

import com.kanawish.glepisodes.module.app.AppModule;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;
import toothpick.smoothie.module.SmoothieApplicationModule;

/**
 * We define our dependency injection scopes here.
 *
 */
public class ScopeBuilder {

    public static Scope buildApplicationScope(Application application) {
        Scope appScope = Toothpick.openScope(application);
        appScope.installModules(
                // SmoothieApplicationModule provides all the application-level system resource injection
                new SmoothieApplicationModule(application),
                new AppModule()
        );
        return appScope;
    }

    public static Scope buildActivityScope(Activity activity) {
        Scope scope = Toothpick.openScopes(activity.getApplication(), activity);
        scope.installModules(
                new SmoothieActivityModule(activity)
        );
        return scope;
    }

}
