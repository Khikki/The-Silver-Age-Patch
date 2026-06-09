package com.phoen1x.thesilveragepatch.impl;

import com.phoen1x.thesilveragepatch.impl.res.ResourcePackGenerator;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TheSilverAgePatch implements ModInitializer {
    public static final List<Runnable> LATE_INIT = new ArrayList<>();

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets("thesilverage");
        PolymerResourcePackUtils.addModAssets("thesilveragepatch");

        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of("thesilverage", "block"));
        ResourcePackGenerator.setup();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            LATE_INIT.forEach(Runnable::run);
            LATE_INIT.clear();
        });
    }

    public static Identifier id(String path) {
        return Identifier.of("thesilveragepatch", path);
    }
}