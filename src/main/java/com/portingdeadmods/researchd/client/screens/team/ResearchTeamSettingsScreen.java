package com.portingdeadmods.researchd.client.screens.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.BaseScreen;
import com.portingdeadmods.researchd.client.screens.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class ResearchTeamSettingsScreen extends BaseScreen {
    public static final ResourceLocation SCREEN_TEXTURE = Researchd.rl("textures/gui/team_settings_screen.png");
    private LinearLayout layout;
    private final Screen prevScreen;
    private String tempTeamName;

    // Widgets
    private StringWidget titleWidget;
    private EditBox teamNameEdit;

    private Button manageMembersButton;
    private Button transferOwnershipButton;
    private Button leaveButton;

    private PlayerManagementDraggableWidget playerManagementWindow;
    private PlayerManagementDraggableWidget transferOwnershipWindow;

    public ResearchTeamSettingsScreen() {
        super(Component.literal("Team Settings"), 480, 264, 128, 195);
        this.prevScreen = Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        super.init();

        // Variables
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(Objects.requireNonNull(player));

        // Layout Setup
        this.layout = LinearLayout.vertical().spacing(8);
        this.layout.setPosition(this.leftPos + 8, this.topPos + 6);

        // Layout Widgets - Title
        this.titleWidget = new StringWidget(this.title, this.font);
        this.layout.addChild(this.titleWidget);

        // Layout Widgets - Team Name
        this.teamNameEdit = this.layout.addChild(new EditBox(this.font, 112, 16, Component.empty()));
        if (this.tempTeamName == null) {
            this.teamNameEdit.setValue(researchTeam.getName());
        } else {
            this.teamNameEdit.setValue(this.tempTeamName);
            this.tempTeamName = null;
        }

        // Layout Widgets - Buttons
        this.manageMembersButton = Button.builder(Component.literal("Manage Members"), btn -> {
            this.playerManagementWindow.visible = !this.playerManagementWindow.visible;
        }).size(112, 16).build();
        this.transferOwnershipButton = Button.builder(Component.literal("Transfer Ownership"), btn -> {
            this.transferOwnershipWindow.visible = !this.transferOwnershipWindow.visible;
        }).size(112, 16).build();
        this.leaveButton = Button.builder(Component.literal("Leave"), btn -> {
        }).size(112, 16).build();

        if (ClientResearchTeamHelper.getPlayerRole(Minecraft.getInstance().player.getUUID()) == ResearchTeamRole.OWNER) {
            this.layout.addChild(manageMembersButton);
            this.layout.addChild(transferOwnershipButton);
        }
        this.layout.addChild(leaveButton);

        // Layout Final
        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);

        // Windows - Player Management
        this.playerManagementWindow = new PlayerManagementDraggableWidget(
                this.leftPos,
                this.topPos,
                ClientResearchTeamHelper.getTeamMembers(),
                new PlayerManagementDraggableWidget.PlayerManagementButtons(true, true, true, false),
                Component.empty()
        );
        this.playerManagementWindow.visible = false;
        this.playerManagementWindow.visitWidgets(this::addRenderableWidget);

        // Windows - Transfer Ownership
        this.transferOwnershipWindow = new PlayerManagementDraggableWidget(
                this.leftPos + this.width / 2,
                this.topPos + this.height / 2,
                ClientResearchTeamHelper.getTeamMembers(),
                new PlayerManagementDraggableWidget.PlayerManagementButtons(false, false, false, true),
                Component.empty()
        );
        this.transferOwnershipWindow.visible = false;
        this.transferOwnershipWindow.visitWidgets(this::addRenderableWidget);
    }

    public void setTempTeamName(String tempTeamName) {
        this.tempTeamName = tempTeamName;
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
