package com.portingdeadmods.researchd.api.client.research;

import com.portingdeadmods.researchd.api.research.ResearchEffect;
import net.minecraft.client.gui.GuiGraphics;

public interface ClientResearchEffect<T extends ResearchEffect> {
    void renderResearchEffect(GuiGraphics guiGraphics, T effect, int x, int y, int mouseX, int mouseY);
}
