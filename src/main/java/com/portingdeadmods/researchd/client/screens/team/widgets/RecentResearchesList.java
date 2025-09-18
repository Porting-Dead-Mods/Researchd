package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import com.portingdeadmods.researchd.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class RecentResearchesList extends ContainerObjectSelectionList<RecentResearchesList.Entry> {
    public RecentResearchesList(int height, int y) {
        super(Minecraft.getInstance(), 223, height, y, 32);
        this.headerHeight = 0;
    }

    public void addResearches(List<ResearchInstance> researches) {
        researches.stream().map(Entry::new).forEach(this::addEntry);
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() - 4;
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<RecentResearchesList.Entry> {
        private final ResearchInstance research;
        private final ItemStack researchIcon;

        public Entry(ResearchInstance research) {
            Minecraft mc = Minecraft.getInstance();

            this.research = research;
            this.researchIcon = mc.level.registryAccess()
                    .holderOrThrow(this.research.getKey()).value().icon()
                    .getDefaultInstance();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            ResourceLocation resourcelocation = ResearchTeamScreen.RECENT_RESEARCH_SPRITES.get(true, this.isMouseOver(mouseX, mouseY));
            guiGraphics.blitSprite(resourcelocation, left, top, width, height);

            PoseStack poseStack = guiGraphics.pose();
            float scale = 1.75f;
            int padding = (int) ((34f - 16f * scale) / 2f); // 32 + 2 (smth smth border 2px)

            poseStack.pushPose();
            {
                poseStack.scale(scale, scale, scale);
                guiGraphics.renderFakeItem(researchIcon, (int) (((float) left + padding) / scale), (int) (((float) top + padding) / scale));
            }
            poseStack.popPose();

            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            if (level == null) return;

            Component researchName = research.getDisplayName(level.registryAccess());
            guiGraphics.drawString(minecraft.font, researchName, left + 32, top + 4, 0xFFFFFF);

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
                guiGraphics.drawString(minecraft.font, metadata, (int) ((left + 32) / metadataScale), (int) ((top + 4 + 4 + minecraft.font.lineHeight) / metadataScale), 0xAAAAAA);
            }
            poseStack.popPose();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }
}
