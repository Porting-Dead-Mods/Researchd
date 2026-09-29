package com.portingdeadmods.researchd.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.portingdeadmods.portingdeadlibs.utils.AABBUtils;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchLabBER implements BlockEntityRenderer<ResearchLabControllerBE, ResearchLabBER.RenderState> {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private static final double PACK_RENDERING_HEIGHT = 1.4;
    private static final double PACK_RENDERING_RADIUS = 0.75;

    private final BlockModelResolver blockModelResolver;
    private final ItemModelResolver itemModelResolver;

    public ResearchLabBER(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(
            ResearchLabControllerBE blockEntity,
            RenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        // The controller block itself is invisible; its model is drawn here, scaled up
        this.blockModelResolver.update(state.labModel, blockEntity.getBlockState(), BLOCK_DISPLAY_CONTEXT);

        state.gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
        int seed = (int) ResearchdBlocks.RESEARCH_LAB_CONTROLLER
                .get()
                .defaultBlockState()
                .getSeed(blockEntity.getBlockPos());
        state.packs.clear();
        for (ItemStack pack : blockEntity.getItemHandler().copyToList()) {
            ItemStackRenderState packState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(
                    packState, pack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed);
            state.packs.add(packState);
        }
    }

    @Override
    public void submit(
            RenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        // Lab Model
        poseStack.pushPose();
        {
            poseStack.scale(3f / 2.8f, 3f / 2.8f, 3f / 2.8f);
            poseStack.translate(0.5, 0, 0.5);
            state.labModel.submitMultiLayer(
                    poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();

        // Research Packs
        double len = state.packs.size();
        for (int i = 0; i < state.packs.size(); i++) {
            ItemStackRenderState pack = state.packs.get(i);
            if (pack.isEmpty()) continue;

            double idx = i;
            float duration = 50f * (float) len; // ticks per rotation
            double theta = (state.gameTime % duration) / duration;

            double sin = Math.sin(Math.PI * 2 * ((idx / len) + theta)) * PACK_RENDERING_RADIUS;
            double cos = Math.cos(Math.PI * 2 * ((idx / len) + theta)) * PACK_RENDERING_RADIUS;

            poseStack.pushPose();
            {
                float bonus;
                if (idx % 2 == 0) {
                    bonus = 0.25f;
                } else {
                    bonus = -0.25f;
                }

                poseStack.translate(
                        sin + 0.5f, PACK_RENDERING_HEIGHT + Math.sin((bonus + theta) * Math.PI * 2) * 0.1, cos + 0.5f);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                double angle = -360.0 * ((idx / len) + theta);
                poseStack.mulPose(Axis.YN.rotationDegrees((float) angle));

                pack.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            }
            poseStack.popPose();
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(ResearchLabControllerBE blockEntity) {
        return AABBUtils.move(new AABB(blockEntity.getBlockPos()), Direction.UP, 1)
                .inflate(1);
    }

    public static class RenderState extends BlockEntityRenderState {
        public final BlockModelRenderState labModel = new BlockModelRenderState();
        public final List<ItemStackRenderState> packs = new ArrayList<>();
        public long gameTime;
    }
}
