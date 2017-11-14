package com.kanawish.glepisodes.di

import android.app.Application

import com.kanawish.glepisodes.tools.GLHelper

import toothpick.config.Module

/**
 * Application level module.
 *
 *
 * GLHelper
 */
class AppModule(app: Application) : Module() {

    init {
        bind(GLHelper::class.java)
    }

}