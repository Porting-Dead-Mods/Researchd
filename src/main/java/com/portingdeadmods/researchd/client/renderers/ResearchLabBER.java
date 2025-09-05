package com.portingdeadmods.researchd.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ResearchLabBER implements BlockEntityRenderer<ResearchLabControllerBE> {
    private final BakedModel model;
    private final BlockState blockState;

    public ResearchLabBER(BlockEntityRendererProvider.Context context) {
        this.model = Minecraft.getInstance().getModelManager().getModel(ResearchdClient.RESEARCH_LAB_MODEL);
        this.blockState = ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get().defaultBlockState();
    }

    @Override
    public void render(ResearchLabControllerBE researchLabControllerBE, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0, 0.5);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.SOLID), this.blockState, this.model, 100, 100, 100, packedLight, packedOverlay, ModelData.EMPTY, RenderType.SOLID);
        }
        poseStack.popPose();
    }
}
