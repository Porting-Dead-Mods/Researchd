package com.portingdeadmods.researchd.client.screens.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.BaseScreen;
import com.portingdeadmods.researchd.client.screens.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class ResearchTeamSettingsScreen extends BaseScreen {
    public static final ResourceLocation SCREEN_TEXTURE = Researchd.rl("textures/gui/team_settings_screen.png");
    private LinearLayout layout;
    private final ClientResearchTeamHelper researchTeamHelper;
    private final Screen prevScreen;
    private PlayerManagementDraggableWidget window;

    public ResearchTeamSettingsScreen() {
        super(Component.literal("Team Settings"), 480, 264, 128, 195);
        this.researchTeamHelper = new ClientResearchTeamHelper();
        this.prevScreen = Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        super.init();

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(Objects.requireNonNull(player));

        this.layout = LinearLayout.vertical().spacing(8);
        this.layout.setPosition(this.leftPos + 8, this.topPos + 6);
        this.layout.addChild(new StringWidget(this.title, this.font));
        EditBox editBox = this.layout.addChild(new EditBox(this.font, 112, 16, Component.empty()));
        editBox.setValue(researchTeam.getName());
        this.layout.addChild(Button.builder(Component.literal("Manage Members"), btn -> {
            this.window.visible = true;
        }).size(112, 16).build());
        this.layout.addChild(Button.builder(Component.literal("Transfer Ownership"), btn -> {}).size(112, 16).build());
        this.layout.addChild(Button.builder(Component.literal("Leave"), btn -> {}).size(112, 16).build());
        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);

        this.window = this.addRenderableWidget(new PlayerManagementDraggableWidget(this.leftPos, this.topPos, Component.empty()));
        this.window.visible = false;
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.prevScreen != null) {
            Minecraft.getInstance().setScreen(this.prevScreen);
        }

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(SCREEN_TEXTURE, this.leftPos, this.topPos, 0, 0, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

}
