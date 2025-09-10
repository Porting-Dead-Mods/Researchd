package com.portingdeadmods.researchd.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
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

            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
            BlockState state = researchLabControllerBE.getBlockState();
            BlockPos pos = researchLabControllerBE.getBlockPos();
            blockRenderer.getModelRenderer().tesselateBlock(
                    researchLabControllerBE.getLevel(),
                    blockRenderer.getBlockModel(state),
                    state,
                    pos,
                    poseStack,
                    multiBufferSource.getBuffer(RenderType.TRANSLUCENT),
                    true,
                    researchLabControllerBE.getLevel().random,
                    state.getSeed(pos),
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    RenderType.TRANSLUCENT
            );
        }
        poseStack.popPose();
    }
}
