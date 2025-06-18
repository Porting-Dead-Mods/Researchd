package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
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

        Research research1 = ResearchHelper.getResearch(this.research.getResearch(), Minecraft.getInstance().level.registryAccess());
        this.researchIcon = new ItemStack(research1.icon());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.renderFakeItem(researchIcon, getX(), getY());
    }
}
