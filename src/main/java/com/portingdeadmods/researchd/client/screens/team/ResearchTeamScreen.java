package com.portingdeadmods.researchd.client.screens.team;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.BaseScreen;
import com.portingdeadmods.researchd.client.screens.team.widgets.RecentResearchWidget;
import com.portingdeadmods.researchd.client.screens.team.widgets.TeamMemberWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec2;

import java.util.*;

public class ResearchTeamScreen extends BaseScreen {
    public static final ResourceLocation SCREEN_TEXTURE = Researchd.rl("textures/gui/team_screen.png");
    public static final WidgetSprites TEAM_MEMBER_BUTTON_SPRITES = new WidgetSprites(Researchd.rl("team_member"), Researchd.rl("team_member_focused"));
    public static final WidgetSprites SETTINGS_BUTTON_SPRITES = new WidgetSprites(Researchd.rl("settings_button"), Researchd.rl("settings_button_focused"));
    public static final WidgetSprites INVITE_BUTTON_SPRITES = new WidgetSprites(Researchd.rl("invite_button"), Researchd.rl("invite_button_focused"));
    public static final WidgetSprites RECENT_RESEARCH_SPRITES = new WidgetSprites(Researchd.rl("recent_research"), Researchd.rl("recent_research_focused"));
    private HeaderAndFooterLayout layout;
    private final ClientResearchTeamHelper researchTeamHelper;

    public ResearchTeamScreen() {
        super(Component.translatable("screen.researchd.research_team"), 480, 264, 480 - 64 * 2, 264 - 32 * 2);

        this.researchTeamHelper = new ClientResearchTeamHelper();
    }

    @Override
    protected void init() {
        super.init();

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(Objects.requireNonNull(player));

        String name = researchTeam.getName();

        List<GameProfile> players;
        if (!mc.isSingleplayer()){
            players = mc.getCurrentServer().players.sample();
        } else {
            players = List.of(mc.player.getGameProfile());
        }
        List<GameProfile> members = researchTeam.getMembers().stream().map(uuid -> {
            for (GameProfile profile : players) {
                if (profile.getId().equals(uuid)) {
                    return profile;
                }
            }
            return null;
        }).toList();

        List<ResearchInstance> recentResearches = ResearchHelper.getRecentResearches(researchTeam);

        this.layout = new HeaderAndFooterLayout(this);
        LinearLayout layout = this.layout.addToContents(LinearLayout.vertical()).spacing(5);

        LinearLayout headerLayout = layout.addChild(LinearLayout.horizontal().spacing(4));
        EditBox teamNameEdit = headerLayout.addChild(new EditBox(this.font, 208, 16, Component.empty()) {
            @Override
            public void setValue(String text) {
                String oldValue = this.getValue();
                super.setValue(text);
                String newValue = this.getValue();
                if (!oldValue.equals(newValue)) {
                    ResearchTeamScreen.this.researchTeamHelper.setTeamNameSynced(newValue);
                }
            }
        });

        teamNameEdit.setValue(name);
        teamNameEdit.setTextColor(FastColor.ARGB32.color(255, 140, 140, 140));
        teamNameEdit.setMaxLength(32);
        teamNameEdit.setTextShadow(false);
        teamNameEdit.setBordered(false);
        headerLayout.addChild(new SpacerElement(77, 0));
        headerLayout.addChild(new ImageButton(14, 14, INVITE_BUTTON_SPRITES, (btn) -> {
        }, Component.literal("Invite Player")));
        headerLayout.addChild(new ImageButton(14, 14, SETTINGS_BUTTON_SPRITES, (btn) -> {
            Minecraft.getInstance().setScreen(new ResearchTeamSettingsScreen());
        }, Component.literal("Team Settings")));
        LinearLayout linearLayout = layout.addChild(LinearLayout.horizontal().spacing(12));
        LinearLayout teamMembersLayout = linearLayout.addChild(LinearLayout.vertical());
        LinearLayout recentResearchesLayout = linearLayout.addChild(LinearLayout.vertical());
        teamMembersLayout.addChild(new StringWidget(Component.literal("Members"), this.font));
        teamMembersLayout.addChild(new SpacerElement(0, 2));
        for (GameProfile member : members) {
            teamMembersLayout.addChild(new TeamMemberWidget(94, 22, member, TEAM_MEMBER_BUTTON_SPRITES, btn -> {
            }));
        }
        recentResearchesLayout.addChild(new StringWidget(Component.literal("Recently Researched"), this.font));
        recentResearchesLayout.addChild(new SpacerElement(0, 2));
        for (ResearchInstance instance : recentResearches) {
            recentResearchesLayout.addChild(new RecentResearchWidget(223, 32, instance, RECENT_RESEARCH_SPRITES, (btn1) -> {
            }));
        }
        this.layout.arrangeElements();
        layout.setX(this.leftPos + 10);
        layout.setY(this.topPos + 11);
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(SCREEN_TEXTURE, leftPos, topPos, textureWidth, textureHeight, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

}
