package com.portingdeadmods.researchd.api.client.research;

import com.portingdeadmods.researchd.api.research.ResearchMethod;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public interface ClientResearchMethod<T extends ResearchMethod> {
    void renderMethodInfo(GuiGraphics guiGraphics, T methods, int x, int y, int mouseX, int mouseY);

    int height();
}
