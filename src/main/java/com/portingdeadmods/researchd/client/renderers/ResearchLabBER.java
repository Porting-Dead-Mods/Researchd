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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ResearchLabBER implements BlockEntityRenderer<ResearchLabControllerBE> {
    private final BlockEntityRendererProvider.Context context;

    private final BakedModel model;
    private final BlockState blockState;

    private final double PACK_RENDERING_HEIGHT = 1.4;
    private final double PACK_RENDERING_RADIUS = 0.75;

    public ResearchLabBER(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.model = Minecraft.getInstance().getModelManager().getModel(ResearchdClient.RESEARCH_LAB_MODEL);
        this.blockState = ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get().defaultBlockState();
    }

    @Override
    public void render(ResearchLabControllerBE researchLabControllerBE, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        // Lab Model
        poseStack.pushPose();
        {
        poseStack.scale(3f / 2.8f,  3f / 2.8f, 3f / 2.8f);
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
                multiBufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(RenderType.TRANSLUCENT, false)),
                true,
                researchLabControllerBE.getLevel().random,
                state.getSeed(pos),
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderTypeHelper.getEntityRenderType(RenderType.TRANSLUCENT, false)
        );
        }

        poseStack.popPose();

        // Research Packs
        for (ItemStack pack : researchLabControllerBE.getItemHandlerStacksList()) {
            double len = researchLabControllerBE.getItemHandlerStacksList().size();
            double idx = researchLabControllerBE.getItemHandlerStacksList().indexOf(pack);

            float duration = 50f * (float) len; // ticks per rotation
            double theta;
            if (researchLabControllerBE.getLevel() != null) {
                theta = (researchLabControllerBE.getLevel().getGameTime() % duration) / duration;
            } else {
                System.out.println("Level is null for some reason");
                theta = 0;
            }

            double sin = Math.sin(Math.PI * 2 * ((idx / len) + theta)) * PACK_RENDERING_RADIUS;
            double cos = Math.cos(Math.PI * 2 * ((idx / len) + theta)) * PACK_RENDERING_RADIUS;

            poseStack.pushPose();
            {
                // System.out.println("Rendering pack at index " + idx + " of " + len + " at position (" + sin + ", " + PACK_RENDERING_HEIGHT + ", " + cos + ")");
                float bonus;
                if (idx % 2 == 0) {
                    bonus = 0.25f;
                } else {
                    bonus = -0.25f;
                }

                poseStack.translate(sin + 0.5f, PACK_RENDERING_HEIGHT + Math.sin((bonus + theta) * Math.PI * 2) * 0.1, cos + 0.5f);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                double angle = -360.0 * ((idx / len) + theta);
                poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees((float) angle));

                context.getItemRenderer()
                        .renderStatic(pack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, multiBufferSource, researchLabControllerBE.getLevel(), (int) blockState.getSeed(researchLabControllerBE.getBlockPos()));
            }
            poseStack.popPose();
        }
    }
}
