package com.portingdeadmods.researchd.client.screens.list;

import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class TechListEntry extends AbstractWidget {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 24;

    private ResearchInstance research;

    public TechListEntry(ResearchInstance research, int x, int y) {
        super(x, y, WIDTH, HEIGHT, Component.empty());
        this.research = research;
    }

    public ResearchInstance getResearch() {
        return research;
    }

    public void setResearch(ResearchInstance research) {
        this.research = research;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(getResearch().getResearchStatus().getSpriteTexture(), getX(), getY(), WIDTH, HEIGHT);

        RegistryAccess lookup = Minecraft.getInstance().level.registryAccess();
        guiGraphics.renderItem(ResearchHelper.getResearch(getResearch().getResearch(), lookup).icon().getDefaultInstance(), getX() + 2, getY() + 2);

        if (isHovered()) {
            int color = -2130706433;
            guiGraphics.fillGradient(RenderType.guiOverlay(), getX(), getY(), getX() + 20, getY() + 20, color, color, 0);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.research.setResearchStatus(EntryType.RESEARCHED);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TechListEntry entry)) return false;
        return Objects.equals(research, entry.research);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(research);
    }
}
