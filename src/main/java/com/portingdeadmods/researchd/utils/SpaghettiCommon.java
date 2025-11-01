package com.portingdeadmods.researchd.utils;

import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class SpaghettiCommon {
    public static Level tryGetLevel() {
        if (FMLEnvironment.dist.isClient()) {
            return Spaghetti.getClientLevel();
        } else {
            return ServerLifecycleHooks.getCurrentServer().overworld();
        }
    }
}
