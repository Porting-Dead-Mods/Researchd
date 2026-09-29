package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.renderers.ResearchPackTintSource;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

public class ModelsProvider extends ModelProvider {
    private static final TextureSlot TEXTURE0 = TextureSlot.create("texture0");

    public ModelsProvider(PackOutput output) {
        super(output, Researchd.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerResearchLabModel(blockModels);

        // The item models themselves are hand-written in src/main/resources.
        itemModels.itemModelOutput.accept(
                ResearchdItems.RESEARCH_PACK.get(),
                ItemModelUtils.tintedModel(
                        Researchd.rl("item/research_pack"),
                        ItemModelUtils.constantTint(-1),
                        new ResearchPackTintSource()));
        itemModels.itemModelOutput.accept(
                ResearchdItems.GREEN_RESEARCH_PACK_ICON.get(),
                ItemModelUtils.plainModel(Researchd.rl("item/green_research_pack_icon")));
        itemModels.itemModelOutput.accept(
                ResearchdItems.RESEARCH_LAB.get(), ItemModelUtils.plainModel(Researchd.rl("item/research_lab")));
    }

    private void registerResearchLabModel(BlockModelGenerators blockModels) {
        ExtendedModelTemplate researchLabTemplate = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TEXTURE0)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .customLoader(ObjModelBuilder::new, loader -> loader.modelLocation(
                                Researchd.rl("models/block/research_lab.obj"))
                        .automaticCulling(false)
                        .shadeQuads(true)
                        .flipV(true)
                        .emissiveAmbient(true))
                .build();
        TextureMapping textures = new TextureMapping()
                .put(TEXTURE0, new Material(Researchd.rl("block/research_lab")))
                .put(TextureSlot.PARTICLE, new Material(Identifier.withDefaultNamespace("block/glass")));
        Identifier researchLabModel =
                researchLabTemplate.create(Researchd.rl("block/research_lab"), textures, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), BlockModelGenerators.plainVariant(researchLabModel)));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                ResearchdBlocks.RESEARCH_LAB_PART.get(),
                BlockModelGenerators.plainVariant(Identifier.withDefaultNamespace("block/air"))));
    }
}
