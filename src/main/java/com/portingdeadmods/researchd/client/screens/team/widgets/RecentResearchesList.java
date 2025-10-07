package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.client.widgets.ContainerWidget;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import com.portingdeadmods.researchd.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.UUID;

public class RecentResearchesList extends ContainerWidget<ResearchInstance> {
    public RecentResearchesList(int width, int height, int itemWidth, int itemHeight, Collection<ResearchInstance> items, boolean renderScroller) {
        super(width, height, itemWidth, itemHeight, items, renderScroller);
    }

    @Override
    public boolean isScrollbarHovered(int mouseX, int mouseY) {
        return mouseX > this.getX() + this.getItemWidth() + 3 && mouseX < this.getX() + this.getWidth() + 3;
    }

    @Override
    public void clickedItem(ResearchInstance item, int index, int left, int top, int mouseX, int mouseY) {

    }

    @Override
    public void renderItem(GuiGraphics guiGraphics, ResearchInstance research, int index, int left, int top, int mouseX, int mouseY) {
        ResourceLocation resourcelocation = ResearchTeamScreen.RECENT_RESEARCH_SPRITES.get(true, this.isItemHovered(index, mouseX, mouseY));
        guiGraphics.blitSprite(resourcelocation, left, top, this.getItemWidth(), this.getItemHeight());

        PoseStack poseStack = guiGraphics.pose();
        float scale = 1.75f;
        int padding = (int) ((34f - 16f * scale) / 2f); // 32 + 2 (smth smth border 2px)

        ResearchScreen.CLIENT_ICONS.get(research.getKey().location()).render(guiGraphics, (int) (((float) left + padding) / scale), (int) (((float) top + padding) / scale), mouseX, mouseY, scale, 0);

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        Component researchName = research.getDisplayName(level);
        guiGraphics.drawString(minecraft.font, researchName, left + 32, top + 4, 0xFFFFFF);

        UUID researchedByUUID = research.getResearchedPlayer();
        String researchedBy;
        if (researchedByUUID != null) {
            researchedBy = PlayerUtils.getPlayerNameFromUUID(level, researchedByUUID);
        } else {
            researchedBy = "NO UUID";
        }

        long researchedTime = research.getResearchedTime();
        TimeUtils.TimeDifference time = new TimeUtils.TimeDifference(0, (int) researchedTime);
        String researchedDate = time.getFormatted();

        Component metadata = ResearchdTranslations.component(ResearchdTranslations.Gui.RESEARCHED_BY_ON, researchedBy, researchedDate);
        poseStack.pushPose();
        {
            float metadataScale = 0.75f;
            poseStack.scale(metadataScale, metadataScale, metadataScale);
            guiGraphics.drawString(minecraft.font, metadata, (int) ((left + 32) / metadataScale), (int) ((top + 4 + 4 + minecraft.font.lineHeight) / metadataScale), 0xAAAAAA);
        }
        poseStack.popPose();
    }

}
