package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TagsProvider {
    public static void createTagProviders(DataGenerator generator, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper, boolean isServer) {
        generator.addProvider(isServer, new BlocksProvider(packOutput, lookupProvider, existingFileHelper));
    }

    protected static class BlocksProvider extends BlockTagsProvider {
        public BlocksProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Researchd.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), ResearchdBlocks.RESEARCH_LAB_PART.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), ResearchdBlocks.RESEARCH_LAB_PART.get());
        }
    }
}
