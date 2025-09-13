package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockModelProvider extends BlockStateProvider {
    public BlockModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Researchd.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerResearchLabModel();
    }

    private void registerResearchLabModel() {
        BlockModelBuilder researchLabBuilder = models().getBuilder("block/research_lab")
                .customLoader((builder, existingFileHelper) ->
                        ObjModelBuilder.begin(builder, existingFileHelper)
                                .modelLocation(modLoc("models/block/research_lab.obj"))
                                .automaticCulling(false)
                                .shadeQuads(true)
                                .flipV(true)
                                .emissiveAmbient(true)
                ).end()
                .texture("texture0", modLoc("block/research_lab"))
                .texture("particle", mcLoc("block/glass"));

        simpleBlock(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), researchLabBuilder);
    }
}
