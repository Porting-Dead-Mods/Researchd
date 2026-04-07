package com.portingdeadmods.researchd.client.screens.editor.widgets.dropdowns;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation.ResearchCreationPopupWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation.ResearchPackCreationPopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DropDownWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class GraphDropDownWidget extends DropDownWidget<LayoutElement> {
    private final ResearchScreen screen;
    private final int x;
    private final int y;
    private final @Nullable Research previousResearch;

    public GraphDropDownWidget(ResearchScreen screen, int x, int y) {
        this(null, screen, x, y);
    }

    public GraphDropDownWidget(@Nullable Research previousResearch, ResearchScreen screen, int x, int y) {
        this.previousResearch = previousResearch;
        this.screen = screen;
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, this.x, this.y, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void buildOptions() {
        if (this.previousResearch == null) {
            this.addOption(new StringOption(Component.literal("New Research"), Minecraft.getInstance().font, this::createNewResearch));
            this.addOption(new StringOption(Component.literal("New Research Pack"), Minecraft.getInstance().font, this::createNewResearchPack));
        } else {
            this.addOption(new StringOption(Component.literal("Edit Research"), Minecraft.getInstance().font, this::createNewResearch));
        }
    }

    private void createNewResearchPack(StringOption opt) {
        this.screen.openPopupCentered(new ResearchPackCreationPopupWidget(0, 0, 128, 182));
        this.screen.setDropDown(null);
    }

    private void createNewResearch(StringOption opt) {
        this.screen.openPopupCentered(new ResearchCreationPopupWidget(this.previousResearch, 0, 0, 128, 182));
        this.screen.setDropDown(null);
    }
}
