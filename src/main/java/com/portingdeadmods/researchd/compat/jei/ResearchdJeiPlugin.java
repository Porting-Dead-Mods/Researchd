package com.portingdeadmods.researchd.compat.jei;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.compat.JEICompat;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public final class ResearchdJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = Researchd.rl("researchd_jei_plugin");

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JEICompat.RUNTIME = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        JEICompat.RUNTIME = null;
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ResearchdItems.RESEARCH_PACK.get(), new SciencePackSubtypeInterpreter());
    }
}
