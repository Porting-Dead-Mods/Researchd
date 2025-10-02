package com.portingdeadmods.researchd.client.screens.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.client.screens.BaseScreen;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import com.portingdeadmods.researchd.networking.team.LeaveTeamPayload;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.PlayerUtils;
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
import net.neoforged.neoforge.network.PacketDistributor;

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
        SimpleResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(Objects.requireNonNull(player));

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
        this.manageMembersButton = Button.builder(ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_MANAGE_MEMBERS), btn -> {
            this.playerManagementWindow.setVisible(!this.playerManagementWindow.visible);
        }).size(112, 16).build();
        this.transferOwnershipButton = Button.builder(ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_TRANSFER_OWNERSHIP), btn -> {
            this.transferOwnershipWindow.setVisible(!this.transferOwnershipWindow.visible);
        }).size(112, 16).build();
        this.leaveButton = Button.builder(ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_LEAVE_TEAM), btn -> {
	        PacketDistributor.sendToServer(new LeaveTeamPayload(PlayerUtils.EMPTY_UUID));
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
                new PlayerManagementDraggableWidget.PlayerManagementButtons(true, true, true, false, false),
                Component.empty()
        );
        this.playerManagementWindow.setVisible(false);
        this.playerManagementWindow.visitWidgets(this::addRenderableOnly);

        // Windows - Transfer Ownership
        this.transferOwnershipWindow = new PlayerManagementDraggableWidget(
                this.leftPos + this.width / 2,
                this.topPos + this.height / 2,
                ClientResearchTeamHelper.getTeamMembers(),
                new PlayerManagementDraggableWidget.PlayerManagementButtons(false, false, false, true, false),
                Component.empty()
        );
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.playerManagementWindow.isLazyHovered()) {
            return this.playerManagementWindow.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        } else if (this.transferOwnershipWindow.isLazyHovered()){
            return this.transferOwnershipWindow.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.playerManagementWindow.isHovered()) {
            return this.playerManagementWindow.mouseClicked(mouseX, mouseY, button);
        } else if (this.transferOwnershipWindow.isHovered()) {
            return this.transferOwnershipWindow.mouseClicked(mouseX, mouseY, button);
        } else if (this.transferOwnershipWindow.popupWidget.isHovered()) {
            return this.transferOwnershipWindow.popupWidget.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.playerManagementWindow.isLazyHovered()) {
            return this.playerManagementWindow.mouseReleased(mouseX, mouseY, button);
        } else if (this.transferOwnershipWindow.isLazyHovered()) {
            return this.transferOwnershipWindow.mouseReleased(mouseX, mouseY, button);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.manageMembersButton.active = (ClientResearchTeamHelper.getRole().getPermissionLevel() > 0);
		this.transferOwnershipButton.active = (ClientResearchTeamHelper.getRole() == ResearchTeamRole.OWNER);
		this.teamNameEdit.setEditable(ClientResearchTeamHelper.getRole() == ResearchTeamRole.OWNER);
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
