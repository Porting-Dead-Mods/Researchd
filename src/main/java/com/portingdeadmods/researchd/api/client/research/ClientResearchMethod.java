package com.portingdeadmods.researchd.api.client.research;

import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.common.util.Size2i;

public interface ClientResearchMethod<T extends ResearchMethod> {
    void renderInfo(GuiGraphics guiGraphics, T method, int x, int y, int mouseX, int mouseY);

    Size2i getSize(T method, int mouseX, int mouseY);

    default void renderTooltip(GuiGraphics guiGraphics, T method, int x, int y, int mouseX, int mouseY) {

    }

    static <T extends ResearchMethod> boolean isHovered(int x, int y, int mouseX, int mouseY, T researchMethod) {
        return mouseX > x && mouseX < x + getSize(mouseX, mouseY, researchMethod).width && mouseY > y && mouseY < y + getSize(mouseX, mouseY, researchMethod).height;
    }

    static <T extends ResearchMethod> Size2i getSize(int mouseX, int mouseY, T researchMethod) {
        ClientResearchMethod<T> clientMethod = (ClientResearchMethod<T>) researchMethod.getClientMethod();
        return clientMethod.getSize(researchMethod, mouseX, mouseY);
    }

    static <T extends ResearchMethod> void renderMethodInfo(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, T researchMethod) {
        ClientResearchMethod<T> clientMethod = (ClientResearchMethod<T>) researchMethod.getClientMethod();
        clientMethod.renderInfo(guiGraphics, researchMethod, x, y, mouseX, mouseY);
    }

    static <T extends ResearchMethod> void renderTooltip(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, T researchMethod) {
        ClientResearchMethod<T> clientMethod = (ClientResearchMethod<T>) researchMethod.getClientMethod();
        clientMethod.renderTooltip(guiGraphics, researchMethod, x, y, mouseX, mouseY);
    }
}
