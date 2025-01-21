package com.portingdeadmods.researchd.api.client.research;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public interface ClientResearchMethod {
    void renderMethodTooltip(GuiGraphics guiGraphics, List<? extends ResearchMethod> rawMethods, int x, int y, int mouseX, int mouseY);

    int height();
}
