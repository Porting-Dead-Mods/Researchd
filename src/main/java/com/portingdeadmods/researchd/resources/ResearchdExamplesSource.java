package com.portingdeadmods.researchd.resources;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public record ResearchdExamplesSource(String packId, PackType packType, Pack.Position packPosition, PackSource source,
                                     PackResources packResources) implements RepositorySource {
    public ResearchdExamplesSource(String packId, PackType packType, Pack.Position packPosition, PackResources packResources) {
        this(packId, packType, packPosition, PackSource.FEATURE, packResources);
    }

    @Override
    public void loadPacks(@NotNull Consumer<Pack> onLoad) {
        PackLocationInfo locationInfo = new PackLocationInfo(packId, Component.literal(packId), source, Optional.empty());
        PackSelectionConfig selectionConfig = new PackSelectionConfig(packType == PackType.CLIENT_RESOURCES, packPosition, false);
        Pack.ResourcesSupplier resourcesSupplier = new Pack.ResourcesSupplier() {
            @Override
            public @NotNull PackResources openPrimary(@NotNull PackLocationInfo packLocationInfo) {
                return packResources;
            }

            @Override
            public @NotNull PackResources openFull(@NotNull PackLocationInfo packLocationInfo, @NotNull Pack.Metadata metadata) {
                return packResources;
            }
        };
        onLoad.accept(Pack.readMetaAndCreate(locationInfo, resourcesSupplier, packType, selectionConfig));
    }
}
