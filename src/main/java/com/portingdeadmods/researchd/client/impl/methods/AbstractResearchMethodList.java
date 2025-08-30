package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.common.util.Size2i;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractResearchMethodList<T extends ResearchMethodList> implements ClientResearchMethod<T> {
    @Override
    public void renderInfo(GuiGraphics guiGraphics, T method, int x, int y, int mouseX, int mouseY) {
        Size2i size = getSize(method, mouseX, mouseY, true);
        guiGraphics.fill(x - 1, y - 1, x + size.width + 1, y + size.height + 1, FastColor.ARGB32.color(109, 109, 109));

        List<? extends ResearchMethod> methods = method.methods();
        for (int i = 0; i < methods.size(); i++) {
            ResearchMethod researchMethod = methods.get(i);
            float padding = getPadding();
            int xSize = (int) (getSizeFor(method, mouseX, mouseY, i, false).width + i * padding);
            ClientResearchMethod.renderMethodInfo(guiGraphics, x + xSize, y, mouseX, mouseY, researchMethod);
            if (i > 0 && i - 1 != methods.size()) {
                ResearchMethod method1 = methods.get(i - 1);
                int ySize = (ClientResearchMethod.getSize(mouseX, mouseY, method1).height - Minecraft.getInstance().font.lineHeight) / 2;
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getSeparatorText(), x + xSize - 8, y + ySize, -1);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, T method, int x, int y, int mouseX, int mouseY) {
        for (ResearchMethod researchMethod : method.methods()) {
            ClientResearchMethod.renderTooltip(guiGraphics, x, y, mouseX, mouseY, researchMethod);
        }
    }

    public @NotNull String getSeparatorText() {
        return "or";
    }

    @Override
    public Size2i getSize(T method, int mouseX, int mouseY) {
        return getSizeFor(method, mouseX, mouseY, method.methods().size(), false);
    }

    public float getPadding() {
        Font font = Minecraft.getInstance().font;
        return font.width(getSeparatorText()) * 1.5f;
    }

    public Size2i getSize(T method, int mouseX, int mouseY, boolean includePadding) {
        return getSizeFor(method, mouseX, mouseY, method.methods().size(), includePadding);
    }

    public @NotNull Size2i getSizeFor(T method, int mouseX, int mouseY, int amount, boolean includePadding) {
        Size2i firstMethodSize = ClientResearchMethod.getSize(mouseX, mouseY, method.methods().getFirst());
        Size2i size = new Size2i(0, firstMethodSize.height);
        List<ResearchMethod> methods = method.methods();
        for (int i = 0; i < amount; i++) {
            ResearchMethod researchMethod = methods.get(i);
            size = new Size2i((int) (size.width + ClientResearchMethod.getSize(mouseX, mouseY, researchMethod).width + (includePadding && i > 0 ? getPadding() : 0)), size.height);
        }
        return size;
    }
}
