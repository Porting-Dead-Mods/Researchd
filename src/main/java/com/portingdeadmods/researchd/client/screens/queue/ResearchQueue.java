package com.portingdeadmods.researchd.client.screens.queue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ResearchQueue extends AbstractWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_queue.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 103;

    private final List<TechListEntry> queue;

    public ResearchQueue(int x, int y) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, Component.empty());
        this.queue = new ArrayList<>();
    }

    public void fillList() {
        for (int col = 0; col < 7; col++) {
            this.queue.add(new TechListEntry(SimpleResearch.debug(Items.OAK_LOG), EntryType.RESEARCHABLE, 12 + col * TechListEntry.WIDTH, 17));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);

        for (TechListEntry entry : this.queue) {
            entry.render(guiGraphics, i, i1, v);
        }

        PoseStack poseStack = guiGraphics.pose();

        TechListEntry entry = new TechListEntry(SimpleResearch.debug(Items.BELL), EntryType.RESEARCHABLE, 6 + getX(), getY() + 27);

        poseStack.pushPose();
        {
            poseStack.translate(0, 1, 0);
            poseStack.scale(2, 2, 2);
            entry.render(guiGraphics, i, i1, v);
        }
        poseStack.popPose();

        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, Component.literal("Cost"), 56, 57, -1, false);
        int lineHeight = font.lineHeight + 2;
        guiGraphics.drawString(font, Component.literal("Red, Green x10"), 56, 57+ lineHeight, -1, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
