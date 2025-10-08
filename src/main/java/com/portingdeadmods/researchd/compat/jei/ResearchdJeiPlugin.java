package com.portingdeadmods.researchd.compat.jei;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.compat.JEICompat;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ResearchdJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = Researchd.rl("researchd_jei_plugin");

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEICompat.RUNTIME = jeiRuntime;
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }
}
