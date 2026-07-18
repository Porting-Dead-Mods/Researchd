package com.portingdeadmods.researchd.client.impl.info.effects;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.impl.research.effect.CommandResearchEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.GuiUtils;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class CommandResearchEffectWidget extends AbstractResearchInfoWidget<CommandResearchEffect> {
    public static final Size2i SPRITE_SIZE = new Size2i(14, 14);
    private static final ResourceLocation COMMAND_BLOCK_FRONT = ResourceLocation.withDefaultNamespace("block/command_block_front");

    public CommandResearchEffectWidget(int x, int y, CommandResearchEffect effect) {
        super(x, y, effect);
    }

    @Override
    public Size2i getSize() {
        return SPRITE_SIZE;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getSize().width, this.getY() + this.getSize().height, BACKGROUND_COLOR);
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(COMMAND_BLOCK_FRONT);

        guiGraphics.blit((int) ((this.getX() + 1)), (int) ((this.getY() + 1)), 0, SPRITE_SIZE.width, SPRITE_SIZE.height, sprite);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            if (!this.value.onUnlockCommand().isBlank()) {
                tooltip.add(ResearchdTranslations.component(ResearchdTranslations.Research.COMMAND_EFFECT_TOOLTIP_UNLOCK, parseCommand(this.value.onUnlockCommand())));
            }
            if (!this.value.onLockCommand().isBlank()) {
                tooltip.add(ResearchdTranslations.component(ResearchdTranslations.Research.COMMAND_EFFECT_TOOLTIP_LOCK, parseCommand(this.value.onLockCommand())));
            }
            if (!tooltip.isEmpty()) {
                GuiUtils.renderTooltip(tooltip);
            }
        }
    }

    private static MutableComponent parseCommand(String command) {
        MutableComponent result = Component.empty();
        Matcher matcher = CommandResearchEffect.PLACEHOLDER_PATTERN.matcher(command);
        int last = 0;
        while (matcher.find()) {
            if (matcher.start() > last) {
                result.append(Component.literal(command.substring(last, matcher.start())));
            }
            result.append(Component.literal(resolvePlaceholder(matcher.group())).withStyle(ChatFormatting.GOLD));
            last = matcher.end();
        }
        if (last < command.length()) {
            result.append(Component.literal(command.substring(last)));
        }
        return result;
    }

    private static String resolvePlaceholder(String placeholder) {
        switch (placeholder) {
            case CommandResearchEffect.PLAYER_NAME_PLACEHOLDER -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) return player.getGameProfile().getName();
            }
            case CommandResearchEffect.TEAM_NAME_PLACEHOLDER -> {
                ResearchTeam team = ResearchTeamHelperClient.getTeam();
                if (team != null) return team.getName();
            }
        }

        return placeholder;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
}
