package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.client.screens.lib.widgets.DropDownWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;

public class GraphDropDownWidget extends DropDownWidget<LayoutElement> {
    private final ResearchScreen screen;
    private final int x;
    private final int y;

    public GraphDropDownWidget(ResearchScreen screen, int x, int y) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.setVisible(true);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, this.x, this.y, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void buildOptions() {
        this.addOption(new StringOption(Component.literal("New Research"), Minecraft.getInstance().font, this::createNewResearch));
        this.addOption(new StringOption(Component.literal("Uwu"), Minecraft.getInstance().font));
        this.addOption(new StringOption(Component.literal("Close Screen lol"), Minecraft.getInstance().font, opt -> Minecraft.getInstance().setScreen(null)));
    }

    private void createNewResearch(StringOption opt) {
        this.screen.openPopupCentered(new ResearchCreationPopupWidget(this.screen, 0, 0, 128, 128));
        this.screen.setDropDown(null);
    }
}
