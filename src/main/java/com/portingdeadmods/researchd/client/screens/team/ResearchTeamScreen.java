package com.portingdeadmods.researchd.client.screens.team;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.BaseScreen;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.screens.team.widgets.RecentResearchWidget;
import com.portingdeadmods.researchd.client.screens.team.widgets.TeamMemberWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.*;

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

    public ResearchTeamScreen() {
        super(Component.translatable("screen.researchd.research_team.title"), 480, 264, 480 - 64 * 2, 264 - 32 * 2);
    }

    @Override
    protected void init() {
        super.init();

        Minecraft mc = Minecraft.getInstance();
        this.player = mc.player;

        // Team information
        ResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(Objects.requireNonNull(player));
        String name = researchTeam.getName();
        List<GameProfile> members = ClientResearchTeamHelper.getTeamMembers();
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
        }, Component.translatable("screen.researchd.research_team.buttons.invite"));
        headerLayout.addChild(this.inviteButton);

        this.settingsButton = new ImageButton(14, 14, SETTINGS_BUTTON_SPRITES, (btn) -> {
            ResearchTeamSettingsScreen screen = new ResearchTeamSettingsScreen();
            screen.setTempTeamName(this.teamNameEdit.getValue());
            Minecraft.getInstance().setScreen(screen);
        }, Component.translatable("screen.researchd.research_team.buttons.team_settings"));
        headerLayout.addChild(this.settingsButton);

        // Layout - Elements
        LinearLayout linearLayout = layout.addChild(LinearLayout.horizontal().spacing(12));

        // Layout - Elements - Team Information
        // TODO: Make this into a container list like the ones in draggable widget
        LinearLayout teamMembersLayout = linearLayout.addChild(LinearLayout.vertical());
        teamMembersLayout.addChild(new StringWidget(Component.translatable("screen.researchd.research_team.titles.members"), this.font));
        teamMembersLayout.addChild(new SpacerElement(0, 2));
        for (GameProfile member : members) {
            teamMembersLayout.addChild(new TeamMemberWidget(94, 22, member, TEAM_MEMBER_BUTTON_SPRITES, btn -> {
            }));
        }

        // Layout - Elements - Recent Researches
        // TODO: Make this into a container list like the ones in draggable widget
        LinearLayout recentResearchesLayout = linearLayout.addChild(LinearLayout.vertical());
        recentResearchesLayout.addChild(new StringWidget(Component.translatable("screen.researchd.research_team.titles.recently_researched"), this.font));
        recentResearchesLayout.addChild(new SpacerElement(0, 2));
        for (ResearchInstance instance : recentResearches) {
            recentResearchesLayout.addChild(new RecentResearchWidget(223, 32, instance, RECENT_RESEARCH_SPRITES, (btn1) -> {
            }));
        }

        // Layout - Final
        this.layout.arrangeElements();
        this.layout.setX(this.leftPos + 10);
        this.layout.setY(this.topPos + 11);
        this.layout.visitWidgets(this::addRenderableWidget);

        this.inviteWidget = new PlayerManagementDraggableWidget(
                this.leftPos,
                this.topPos,
                ClientResearchTeamHelper.getPlayersNotInTeam(),
                new PlayerManagementDraggableWidget.PlayerManagementButtons(false, false, false, false, true),
                Component.empty()
        );
        inviteWidget.setVisible(false);
        inviteWidget.visitWidgets(this::addRenderableOnly);
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

        this.settingsButton.active = (ClientResearchTeamHelper.getPlayerPermissionLevel(this.player) >= 1);
        this.inviteButton.active = (ClientResearchTeamHelper.getPlayerPermissionLevel(this.player) > 0);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

}
