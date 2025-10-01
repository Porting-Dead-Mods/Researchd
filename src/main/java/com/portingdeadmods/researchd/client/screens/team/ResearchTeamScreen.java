package com.portingdeadmods.researchd.client.screens.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.BaseScreen;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.screens.team.widgets.RecentResearchesList;
import com.portingdeadmods.researchd.client.screens.team.widgets.TeamMembersList;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.List;
import java.util.Objects;

public class ResearchTeamScreen extends BaseScreen {
    public static final ResourceLocation SCREEN_TEXTURE = Researchd.rl("textures/gui/team_screen.png");
    public static final WidgetSprites TEAM_MEMBER_BUTTON_SPRITES = new WidgetSprites(Researchd.rl("team_member"), Researchd.rl("team_member_focused"));
    public static final WidgetSprites SETTINGS_BUTTON_SPRITES = new WidgetSprites(Researchd.rl("settings_button"), Researchd.rl("settings_button_disabled"), Researchd.rl("settings_button_focused"));
    public static final WidgetSprites INVITE_BUTTON_SPRITES = new WidgetSprites(Researchd.rl("invite_button"), Researchd.rl("invite_button_disabled"), Researchd.rl("invite_button_focused"));
    public static final WidgetSprites RECENT_RESEARCH_SPRITES = new WidgetSprites(Researchd.rl("recent_research"), Researchd.rl("recent_research_focused"));

    private LocalPlayer player;

    // Widgets n Layouts
    private LinearLayout layout;
    private EditBox teamNameEdit;
    private ImageButton inviteButton;
    private ImageButton settingsButton;
    private PlayerManagementDraggableWidget inviteWidget;
    public PlayerManagementDraggableWidget getInviteWidget() {
        return inviteWidget;
    }
    private TeamMembersList teamMembersList;
    public TeamMembersList getTeamMembersList() {
        return teamMembersList;
    }

    public ResearchTeamScreen() {
        super(ResearchdTranslations.component(ResearchdTranslations.Team.SCREEN_TITLE), 480, 264, 480 - 64 * 2, 264 - 32 * 2);
    }

    @Override
    protected void init() {
        super.init();

        Minecraft mc = Minecraft.getInstance();
        this.player = mc.player;

        // Team information
        ResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(Objects.requireNonNull(player));
        String name = researchTeam.getName();
        List<ResearchInstance> recentResearches = ResearchHelperCommon.getRecentResearches(researchTeam);

        // Layout setup
        this.layout = LinearLayout.vertical().spacing(5);

        // Layout - Header
        LinearLayout headerLayout = layout.addChild(LinearLayout.horizontal().spacing(4));

        // Layout - Header - Team Name
        this.teamNameEdit = headerLayout.addChild(new EditBox(this.font, 208, 16, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if (!focused) {
                    ClientResearchTeamHelper.setTeamNameSynced(this.getValue());
                }
            }
        });
        this.teamNameEdit.setValue(name);
        this.teamNameEdit.setTextColor(FastColor.ARGB32.color(255, 140, 140, 140));
        this.teamNameEdit.setMaxLength(32);
        this.teamNameEdit.setTextShadow(false);
        this.teamNameEdit.setBordered(false);

        // Layout - Header - Buttons
        headerLayout.addChild(new SpacerElement(77, 0));

        this.inviteButton = new ImageButton(14, 14, INVITE_BUTTON_SPRITES, (btn) -> {
            this.inviteWidget.setVisible(!this.inviteWidget.visible);
        }, ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_INVITE));
        headerLayout.addChild(this.inviteButton);

        this.settingsButton = new ImageButton(14, 14, SETTINGS_BUTTON_SPRITES, (btn) -> {
            ResearchTeamSettingsScreen screen = new ResearchTeamSettingsScreen();
            screen.setTempTeamName(this.teamNameEdit.getValue());
            Minecraft.getInstance().setScreen(screen);
        }, ResearchdTranslations.component(ResearchdTranslations.Team.BUTTON_TEAM_SETTINGS));
        headerLayout.addChild(this.settingsButton);

        // Layout - Elements
        LinearLayout linearLayout = layout.addChild(LinearLayout.horizontal().spacing(-1));

        // Layout - Elements - Team Information
        LinearLayout teamMembersLayout = linearLayout.addChild(LinearLayout.vertical());
        teamMembersLayout.addChild(new StringWidget(ResearchdTranslations.component(ResearchdTranslations.Team.TITLE_MEMBERS), this.font));
        teamMembersLayout.addChild(new SpacerElement(-1, 1));
        linearLayout.spacing(11);
        teamMembersList = teamMembersLayout.addChild(new TeamMembersList(94, 142, 94, 22, ClientResearchTeamHelper.getTeamMembers(), false));

        // Layout - Elements - Recent Researches
        linearLayout.spacing(11);
        LinearLayout recentResearchesLayout = linearLayout.addChild(LinearLayout.vertical());
        recentResearchesLayout.addChild(new StringWidget(ResearchdTranslations.component(ResearchdTranslations.Team.TITLE_RECENTLY_RESEARCHED), this.font));
        recentResearchesLayout.spacing(1);
        recentResearchesLayout.addChild(new RecentResearchesList(230, 142, 221, 32, recentResearches, true));

        // Layout - Final
        this.layout.arrangeElements();
        this.layout.setX(this.leftPos + 10);
        this.layout.setY(this.topPos + 11);
        this.layout.visitWidgets(this::addRenderableWidget);
        teamMembersList.setX(teamMembersList.getX() - 1);

        this.inviteWidget = new PlayerManagementDraggableWidget(
                this.leftPos,
                this.topPos,
                ClientResearchTeamHelper.getPlayersNotInTeam(),
                new PlayerManagementDraggableWidget.PlayerManagementButtons(false, false, false, false, true),
                Component.empty()
        );

        inviteWidget.setVisible(false);
        inviteWidget.visitWidgets(this::addRenderableOnly);

		// Call visible logic on init asw since it flickers for 1 frame on screen creation
	    if (!Minecraft.getInstance().isSingleplayer()) {
		    if (Minecraft.getInstance().getSingleplayerServer() != null)
			    this.inviteButton.active = Minecraft.getInstance().getSingleplayerServer().getPlayerCount() > 1;
		    else if (Minecraft.getInstance().getConnection() != null)
			    this.inviteButton.active = Minecraft.getInstance().getConnection().getOnlinePlayers().size() > 1;
	    }
	    this.inviteButton.active = this.inviteButton.active && (ClientResearchTeamHelper.getPlayerPermissionLevel(this.player) >= 1);
    }

    @Override
    public void onClose() {
        super.onClose();

        ClientResearchTeamHelper.setTeamNameSynced(this.teamNameEdit.getValue());

    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.inviteWidget.isLazyHovered()) {
            return this.inviteWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.inviteWidget.isHovered()) {
            return this.inviteWidget.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(SCREEN_TEXTURE, leftPos, topPos, textureWidth, textureHeight, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
	    this.inviteButton.active = false;
		int count;
	    if (!Minecraft.getInstance().isSingleplayer()) {
		    if (Minecraft.getInstance().getSingleplayerServer() != null) {
			    count = Minecraft.getInstance().getSingleplayerServer().getPlayerCount();
			    this.inviteButton.active = count > 1;
		    } else if (Minecraft.getInstance().getConnection() != null) {
			    count = Minecraft.getInstance().getConnection().getOnlinePlayers().size();
			    this.inviteButton.active = count > 1;
		    }
	    }
		this.inviteButton.active = this.inviteButton.active && (ClientResearchTeamHelper.getPlayerPermissionLevel(this.player) >= 1);
	}

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

}
