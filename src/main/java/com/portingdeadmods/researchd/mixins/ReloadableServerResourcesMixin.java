package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.resources.RegistryManagersGetter;
import com.portingdeadmods.researchd.utils.researches.ReloadableRegistryManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin implements RegistryManagersGetter {
    @Unique
    @Final
    @Mutable
    private ReloadableRegistryManager<Research> researchd$researchesManager;
    @Unique
    @Final
    @Mutable
    private ReloadableRegistryManager<ResearchPack> researchd$researchPacksManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void researchd$init(RegistryAccess.Frozen registryAccess, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
        this.researchd$researchesManager = new ReloadableRegistryManager<>(registryAccess, ResearchdRegistries.RESEARCH_KEY, Research.CODEC);
        this.researchd$researchPacksManager = new ReloadableRegistryManager<>(registryAccess, ResearchdRegistries.RESEARCH_PACK_KEY, ResearchPack.CODEC);
    }

    @ModifyReturnValue(method = "listeners", at = @At("RETURN"))
    private List<PreparableReloadListener> researchd$listeners(List<PreparableReloadListener> original) {
        List<PreparableReloadListener> copy = new ArrayList<>(original);
        copy.add(this.researchd$researchesManager);
        copy.add(this.researchd$researchPacksManager);
        return List.copyOf(copy);
    }

    @Override
    public ReloadableRegistryManager<Research> researchd$getResearchesManager() {
        return this.researchd$researchesManager;
    }

    @Override
    public ReloadableRegistryManager<ResearchPack> researchd$getResearchPackManager() {
        return this.researchd$researchPacksManager;
    }

}
