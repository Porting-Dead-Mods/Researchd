package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import com.portingdeadmods.researchd.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class RecentResearchWidget extends ImageButton {
    private final ResearchInstance research;
    private final ItemStack researchIcon;

    public RecentResearchWidget(int width, int height, ResearchInstance research, WidgetSprites sprites, OnPress onPress) {
        this(0, 0, width, height, research, sprites, onPress);
    }

    public RecentResearchWidget(int x, int y, int width, int height, ResearchInstance research, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
        this.research = research;

        Research research1 = this.research.lookup(Minecraft.getInstance().level.registryAccess());
        this.researchIcon = new ItemStack(research1.icon());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        PoseStack poseStack = guiGraphics.pose();
        float scale = 1.75f;
        int padding = (int) ((34f - 16f * scale) / 2f); // 32 + 2 (smth smth border 2px)

        poseStack.pushPose();
        {
            poseStack.scale(scale, scale, scale);
            guiGraphics.renderFakeItem(researchIcon, (int) (((float) getX() + padding) / scale), (int) (((float) getY() + padding) / scale));
        }
        poseStack.popPose();

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        Component researchName = research.getDisplayName(level.registryAccess());
        guiGraphics.drawString(minecraft.font, researchName, getX() + 32, getY() + 4, 0xFFFFFF);

        UUID researchedByUUID = research.getResearchedPlayer();
        String researchedBy;
        if (researchedByUUID != null)
            researchedBy = PlayerUtils.getPlayerNameFromUUID(level, researchedByUUID);
        else
            researchedBy = "NO UUID";

        long researchedTime = research.getResearchedTime();
        TimeUtils.TimeDifference time = new TimeUtils.TimeDifference(0, (int) researchedTime);
        String researchedDate = time.getFormatted();

        Component metadata = ResearchdTranslations.component(ResearchdTranslations.Gui.RESEARCHED_BY_ON, researchedBy, researchedDate);
        poseStack.pushPose();
        {
            float metadataScale = 0.75f;
            poseStack.scale(metadataScale, metadataScale, metadataScale);
            guiGraphics.drawString(minecraft.font, metadata, (int) ((getX() + 32) / metadataScale), (int) ((getY() + 4 + 4 + minecraft.font.lineHeight) / metadataScale), 0xAAAAAA);
        }
        poseStack.popPose();
    }
}