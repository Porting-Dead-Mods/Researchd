package com.portingdeadmods.researchd.client.screens.widgets;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class SelectedResearchWidget extends ResearchScreenWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/selected_research.png");
    private static final ResourceLocation SMALL_SCROLLER_SPRITE = Researchd.rl("scroller_small");
    public static final int BACKGROUND_WIDTH = 174;
    public static final int BACKGROUND_HEIGHT = 72;
    private ResearchInstance selectedInstance;
    private final Map<ResourceLocation, ResearchMethod> methods;
    private int scrollOffset;

    public SelectedResearchWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.methods = new Object2ObjectArrayMap<>();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);
        guiGraphics.blitSprite(SMALL_SCROLLER_SPRITE, getX() + getWidth() - 9, getY() + 20, 4, 7);

        int offsetY = (int) -(this.scrollOffset * 1.5f);

        if (this.selectedInstance != null) {
            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Utils.registryTranslation(this.selectedInstance.getResearch()), 11, 49, -1);
            renderResearchPanel(guiGraphics, this.selectedInstance, 12, 60, mouseX, mouseY, 2, false);

            guiGraphics.enableScissor(53, 60, 53 + 115, 55 + 48);

            int lineHeight = font.lineHeight + 2;
            guiGraphics.drawString(font, "Researched by", 56, offsetY + 62, -1, false);

            int height = 0;
            for (Map.Entry<ResourceLocation, ResearchMethod> entry : this.methods.entrySet()) {
//                guiGraphics.drawString(font, Component.literal("Researched by"), 56, 57, -1, false);
//                ClientResearchMethod clientMethod = method.getClientMethod();
//                clientMethod.renderMethodTooltip(guiGraphics, entry.getValue(), 56, 57 + lineHeight, mouseX, mouseY);
//                height += entry.getValue().isEmpty() ? 0 : entry.getValue().getFirst().getClientMethod().height();
            }

            guiGraphics.drawString(font, "Effects", 56, offsetY + 57 + height + 32, -1, false);

            guiGraphics.disableScissor();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset = (int) Math.max(Math.max(this.scrollOffset - scrollY * 2, 0), 0);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public void setSelectedResearch(ResearchInstance instance) {
        this.selectedInstance = instance;

        this.methods.clear();
        this.scrollOffset = 0;
        ResearchMethod method = ResearchHelper.getResearch(this.selectedInstance.getResearch(), Minecraft.getInstance().level.registryAccess()).researchMethod();
        this.methods.put(method.id(), method);
    }

    public ResearchInstance getSelectedInstance() {
        return selectedInstance;
    }
}
