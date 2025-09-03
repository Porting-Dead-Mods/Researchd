package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.registries.ResearchdResearchPacks;
import com.portingdeadmods.researchd.registries.ResearchdResearches;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class DatapackRegistryProvider extends DatapackBuiltinEntriesProvider {
    public DatapackRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Researchd.MODID));
    }

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // -- RESEARCH PACK --
            .add(ResearchdRegistries.RESEARCH_PACK_KEY, ResearchdResearchPacks::bootstrap)
            .add(ResearchdRegistries.RESEARCH_KEY, ResearchdResearches::bootstrap);

}
