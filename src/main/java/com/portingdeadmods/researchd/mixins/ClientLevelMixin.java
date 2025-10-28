package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.resources.RegistryManagersGetter;
import com.portingdeadmods.researchd.utils.researches.ReloadableRegistryManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements RegistryManagersGetter {
    @Unique
    private ReloadableRegistryManager<Research> researchd$researchesManager;
    @Unique
    private ReloadableRegistryManager<ResearchPackImpl> researchd$researchPacks;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void researchd$init(ClientPacketListener connection, ClientLevel.ClientLevelData clientLevelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionType, int viewDistance, int serverSimulationDistance, Supplier<ProfilerFiller> profiler, LevelRenderer levelRenderer, boolean isDebug, long biomeZoomSeed, CallbackInfo ci) {
        this.researchd$researchesManager = new ReloadableRegistryManager<>(connection.registryAccess(), ResearchdRegistries.RESEARCH_KEY, Research.CODEC);
        this.researchd$researchPacks = new ReloadableRegistryManager<>(connection.registryAccess(), ResearchdRegistries.RESEARCH_PACK_KEY, ResearchPackImpl.CODEC);
    }

    @Override
    public ReloadableRegistryManager<Research> researchd$getResearchesManager() {
        return this.researchd$researchesManager;
    }

    @Override
    public ReloadableRegistryManager<ResearchPackImpl> researchd$getResearchPackManager() {
        return this.researchd$researchPacks;
    }
}
