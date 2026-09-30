package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.utils.registries.RegistryManagersGetter;
import com.portingdeadmods.researchd.utils.registries.ReloadableRegistryManager;
import java.util.List;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin implements RegistryManagersGetter {
    @Unique @Final
    @Mutable
    private ReloadableRegistryManager<Research> researchd$researchesManager;

    @Unique @Final
    @Mutable
    private ReloadableRegistryManager<ResearchPack> researchd$researchPacksManager;

    // The managers are handed to the resource manager in ResearchdLifecycleHandler#onAddReloadListeners:
    // NeoForge rejects mod listeners added to the vanilla listener list by mixin.
    @Inject(method = "<init>", at = @At("TAIL"))
    private void researchd$init(
            LayeredRegistryAccess<RegistryLayer> fullLayers,
            HolderLookup.Provider loadingContext,
            FeatureFlagSet enabledFeatures,
            Commands.CommandSelection commandSelection,
            List<Registry.PendingTags<?>> postponedTags,
            PermissionSet functionCompilationPermissions,
            List<DataComponentInitializers.PendingComponents<?>> newComponents,
            CallbackInfo ci) {
        this.researchd$researchesManager =
                new ReloadableRegistryManager<>(loadingContext, ResearchdRegistries.RESEARCH_KEY, Research.CODEC);
        this.researchd$researchPacksManager = new ReloadableRegistryManager<>(
                loadingContext, ResearchdRegistries.RESEARCH_PACK_KEY, ResearchPack.CODEC);
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
