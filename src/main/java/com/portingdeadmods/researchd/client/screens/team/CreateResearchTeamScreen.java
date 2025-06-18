package com.portingdeadmods.researchd.client.screens.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.BaseScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreateResearchTeamScreen extends BaseScreen {
    public static final ResourceLocation SCREEN_TEXTURE = Researchd.rl("textures/gui/team_create_screen.png");
    private HeaderAndFooterLayout layout;

    public CreateResearchTeamScreen() {
        super(Component.translatable("screen.researchd.research_create_team"), 480, 264, 480 - 64 * 2 - 64 * 3, 264 - 32 * 2);
    }

    @Override
    protected void init() {
        super.init();
        this.layout = new HeaderAndFooterLayout(this);
        LinearLayout linearlayout = this.layout.addToContents(LinearLayout.vertical().spacing(4));

        linearlayout.addChild(new StringWidget(Component.literal("Team Name"), this.font));
        linearlayout.addChild(new SpacerElement(0, 8));
        linearlayout.addChild(new EditBox(this.font, 128, 16, Component.literal("Test")));
        linearlayout.addChild(Button.builder(Component.literal("Create"), null).build());

        this.layout.arrangeElements();
        linearlayout.setY(this.topPos + 6);
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(SCREEN_TEXTURE, leftPos, topPos, textureWidth, textureHeight, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

}
