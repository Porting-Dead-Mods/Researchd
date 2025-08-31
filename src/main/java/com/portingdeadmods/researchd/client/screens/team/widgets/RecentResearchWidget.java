package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.world.item.ItemStack;

public class RecentResearchWidget extends ImageButton {
    private final ResearchInstance research;
    private final ItemStack researchIcon;

    public RecentResearchWidget(int width, int height, ResearchInstance research, WidgetSprites sprites, OnPress onPress) {
        this(0, 0, width, height, research, sprites, onPress);
    }

    public RecentResearchWidget(int x, int y, int width, int height, ResearchInstance research, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
        this.research = research;

        Research research1 = this.research.lookup(Minecraft.getInstance().level.registryAccess());
        this.researchIcon = new ItemStack(research1.icon());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        PoseStack poseStack = guiGraphics.pose();
        float scale = 2;
        poseStack.pushPose();
        {
            poseStack.scale(scale, scale, scale);
            guiGraphics.renderFakeItem(researchIcon, (int) ((getX() + this.height / 2f) / scale), (int) ((getY() + this.height / 2f) / scale));
        }
        poseStack.popPose();
    }
}
