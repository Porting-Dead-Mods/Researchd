package com.portingdeadmods.researchd.client.screens.team;

import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.networking.team.LeaveTeamPayload;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperClient;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperServer;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ResearchTeamSettingsScreen extends BaseTeamScreen {
    public static final Identifier SCREEN_TEXTURE = Researchd.rl("textures/gui/team_settings_screen.png");
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

    public PlayerManagementDraggableWidget getPlayerManagementWindow() {
        return playerManagementWindow;
    }

    private PlayerManagementDraggableWidget transferOwnershipWindow;

    public PlayerManagementDraggableWidget getTransferOwnershipWindow() {
        return transferOwnershipWindow;
    }

    public ResearchTeamSettingsScreen() {
        super(ResearchdTranslations.component(ResearchdTranslations.Team.SETTINGS_SCREEN_TITLE), 480, 264, 128, 195);
        this.prevScreen = Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        super.init();

        // Variables
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ResearchTeam researchTeam = ResearchTeamHelperServer.getTeamByMember(Objects.requireNonNull(player));
        if (researchTeam == null) {
            this.onClose();
            return;
        }

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
        this.manageMembersButton = Button.builder(
                        ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_MANAGE_MEMBERS), btn -> {
                            this.playerManagementWindow.setVisible(!this.playerManagementWindow.visible);
                        })
                .size(112, 16)
                .build();
        this.transferOwnershipButton = Button.builder(
                        ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_TRANSFER_OWNERSHIP), btn -> {
                            this.transferOwnershipWindow.setVisible(!this.transferOwnershipWindow.visible);
                        })
                .size(112, 16)
                .build();
        this.leaveButton = Button.builder(
                        ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_LEAVE_TEAM), btn -> {
                            ClientPacketDistributor.sendToServer(new LeaveTeamPayload(PlayerUtils.EmptyUUID));
                        })
                .size(112, 16)
                .build();

        if (ResearchTeamHelperClient.getPlayerRole(
                        Minecraft.getInstance().player.getUUID())
                == ResearchTeamRole.OWNER) {
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
                ResearchTeamHelperClient.getTeamMembers(),
                new PlayerManagementDraggableWidget.PlayerManagementButtons(true, true, true, false, false),
                Component.empty());
        this.playerManagementWindow.setVisible(false);
        this.playerManagementWindow.visitWidgets(this::addRenderableOnly);

        // Windows - Transfer Ownership
        this.transferOwnershipWindow = new PlayerManagementDraggableWidget(
                this.leftPos + this.width / 2,
                this.topPos + this.height / 2,
                ResearchTeamHelperClient.getTeamMembers(),
                new PlayerManagementDraggableWidget.PlayerManagementButtons(false, false, false, true, false),
                Component.empty());
        this.transferOwnershipWindow.setVisible(false);
        this.transferOwnershipWindow.visitWidgets(this::addRenderableOnly);
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
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.playerManagementWindow.isLazyHovered()) {
            return this.playerManagementWindow.mouseDragged(event, dragX, dragY);
        } else if (this.transferOwnershipWindow.isLazyHovered()) {
            return this.transferOwnershipWindow.mouseDragged(event, dragX, dragY);
        }

        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.playerManagementWindow.isHovered()) {
            return this.playerManagementWindow.mouseClicked(event, doubleClick);
        } else if (this.transferOwnershipWindow.isHovered()) {
            return this.transferOwnershipWindow.mouseClicked(event, doubleClick);
        } else if (this.transferOwnershipWindow.popupWidget.isHovered()) {
            return this.transferOwnershipWindow.popupWidget.mouseClicked(event, doubleClick);
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.playerManagementWindow.isLazyHovered()) {
            return this.playerManagementWindow.mouseReleased(event);
        } else if (this.transferOwnershipWindow.isLazyHovered()) {
            return this.transferOwnershipWindow.mouseReleased(event);
        }

        return super.mouseReleased(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.manageMembersButton.active = (ResearchTeamHelperClient.getRole().getPermissionLevel() > 0);
        this.transferOwnershipButton.active = (ResearchTeamHelperClient.getRole() == ResearchTeamRole.OWNER);
        this.teamNameEdit.setEditable(ResearchTeamHelperClient.getRole() == ResearchTeamRole.OWNER);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                SCREEN_TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.width,
                this.height,
                this.textureWidth,
                this.textureHeight);
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor guiGraphics) {}
}
