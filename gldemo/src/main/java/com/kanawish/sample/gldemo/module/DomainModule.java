package com.kanawish.sample.gldemo.module;

import com.kanawish.sample.gldemo.module.domain.FileSystemManager;
import com.kanawish.sample.gldemo.module.domain.ScriptManager;
import toothpick.config.Module;

public class DomainModule extends Module {

    public DomainModule() {
        bind(ScriptManager.class).to(FileSystemManager.class);
    }

}
