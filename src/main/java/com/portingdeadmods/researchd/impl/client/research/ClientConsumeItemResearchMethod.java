package com.portingdeadmods.researchd.impl.client.research;

import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.impl.research.ConsumeItemResearchMethod;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ClientConsumeItemResearchMethod implements ClientResearchMethod {
    public static final ClientConsumeItemResearchMethod INSTANCE = new ClientConsumeItemResearchMethod();

    private ClientConsumeItemResearchMethod() {
    }

    @Override
    public void renderMethodTooltip(GuiGraphics guiGraphics, List<? extends ResearchMethod> methods, int x, int y, int mouseX, int mouseY) {

    }

    @Override
    public int height() {
        return 0;
    }
}
