package com.itedya.itedyaguilds;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BindingModule extends AbstractModule {
    private final ItedyaGuilds plugin;

    public BindingModule(ItedyaGuilds plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(ItedyaGuilds.class).toInstance(this.plugin);
    }
}
