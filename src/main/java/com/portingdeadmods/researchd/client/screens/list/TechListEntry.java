package com.portingdeadmods.researchd.client.screens.list;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TechListEntry extends AbstractWidget {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 24;

    private Research research;
    private EntryType type;

    public TechListEntry(@Nullable Research research, @Nullable EntryType type, int x, int y) {
        super(x, y, WIDTH, HEIGHT, Component.empty());
        this.research = research;
        this.type = type;
    }

    public Research getResearch() {
        return research;
    }

    public EntryType getType() {
        return type;
    }

    public void setResearch(Research research) {
        this.research = research;
    }

    public void setType(EntryType type) {
        this.type = type;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (type != null) {
            guiGraphics.blitSprite(type.getSpriteTexture(), getX(), getY(), WIDTH, HEIGHT);

            guiGraphics.renderItem(research.icon().getDefaultInstance(), getX() + 2, getY() + 2);

            if (isHovered()) {
                int color = -2130706433;
                guiGraphics.fillGradient(RenderType.guiOverlay(), getX(), getY(), getX() + 20, getY() + 20, color, color, 0);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TechListEntry entry)) return false;
        return Objects.equals(research, entry.research) && type == entry.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(research, type);
    }
}
