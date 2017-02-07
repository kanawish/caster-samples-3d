package com.kanawish.glepisodes.module;

import toothpick.config.Module;

/**
 */
public class AppModule extends Module {

    public AppModule() {
        // TODO: Add more toothpick bindings here.
        bind(ActivityHierarchyServer.class).toInstance(ActivityHierarchyServer.NONE);
    }
}
