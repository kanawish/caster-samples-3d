package com.kanawish.glepisodes.module.app;

import toothpick.config.Module;

/**
 * Application level module.
 *
 *
 * GLHelper
 */
public class AppModule extends Module {

    public AppModule() {
        bind(GLHelper.class);
        // NOTE: Add bindings as needed here.
    }

}