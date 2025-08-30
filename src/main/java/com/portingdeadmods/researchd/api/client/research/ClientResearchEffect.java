package com.portingdeadmods.researchd.api.client.research;

import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.common.util.Size2i;

public interface ClientResearchEffect<T extends ResearchEffect> {
    void renderInfo(GuiGraphics guiGraphics, T effect, int x, int y, int mouseX, int mouseY);

    Size2i getBox(T effect, int mouseX, int mouseY);

    default void renderTooltip(GuiGraphics guiGraphics, T effect, int x, int y, int mouseX, int mouseY) {

    }
}
