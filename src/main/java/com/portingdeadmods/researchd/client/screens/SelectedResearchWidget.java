package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectedResearchWidget extends ResearchScreenWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/selected_research.png");
    private static final ResourceLocation SMALL_SCROLLER_SPRITE = Researchd.rl("scroller_small");
    public static final int BACKGROUND_WIDTH = 174;
    public static final int BACKGROUND_HEIGHT = 61;
    private ResearchInstance selectedInstance;
    private final Map<ResourceLocation, ResearchMethod> methods;

    public SelectedResearchWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.methods = new Object2ObjectArrayMap<>();
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);
        guiGraphics.blitSprite(SMALL_SCROLLER_SPRITE, getX() + getWidth() - 9, getY() + 13, 4, 7);

        if (selectedInstance != null) {
            renderResearchPanel(guiGraphics, selectedInstance, 12, 55, mouseX, mouseY, 2, false);

            guiGraphics.enableScissor(53, 55, 53 + 115, 55 + 48);

            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Utils.registryTranslation(this.selectedInstance.getResearch()), 12, 45, -1, false);
            int lineHeight = font.lineHeight + 2;

            int height = 0;
            for (Map.Entry<ResourceLocation, ResearchMethod> entry : this.methods.entrySet()) {
//                guiGraphics.drawString(font, Component.literal("Researched by"), 56, 57, -1, false);
//                ClientResearchMethod clientMethod = method.getClientMethod();
//                clientMethod.renderMethodTooltip(guiGraphics, entry.getValue(), 56, 57 + lineHeight, mouseX, mouseY);
//                height += entry.getValue().isEmpty() ? 0 : entry.getValue().getFirst().getClientMethod().height();
            }

            guiGraphics.drawString(font, "Effects", 56, 57 + height + 14, -1, false);

            guiGraphics.disableScissor();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public void setSelectedResearch(ResearchInstance instance) {
        this.selectedInstance = instance;

        this.methods.clear();
        ResearchMethod method = ResearchHelper.getResearch(this.selectedInstance.getResearch(), Minecraft.getInstance().level.registryAccess()).researchMethods();
        this.methods.put(method.id(), method);
    }

    public ResearchInstance getSelectedInstance() {
        return selectedInstance;
    }
}
