package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.NumberUtils;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.joml.Matrix3x2fStack;

public class RecentResearchesList extends ContainerWidget<ResearchInstance> {
    public RecentResearchesList(
            int width,
            int height,
            int itemWidth,
            int itemHeight,
            Collection<ResearchInstance> items,
            boolean renderScroller) {
        super(width, height, itemWidth, itemHeight, Orientation.VERTICAL, 1, 10, items, renderScroller);
    }

    @Override
    public boolean isScrollbarHovered(int mouseX, int mouseY) {
        return mouseX > this.getX() + this.getItemWidth() + 3 && mouseX < this.getX() + this.getWidth() + 3;
    }

    @Override
    public void clickedItem(ResearchInstance item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {}

    @Override
    protected int getScissorsHeight() {
        return this.getHeight();
    }

    @Override
    public void internalRenderItem(
            GuiGraphicsExtractor guiGraphics,
            ResearchInstance research,
            int xIndex,
            int index,
            int left,
            int top,
            int mouseX,
            int mouseY) {
        Identifier resourcelocation =
                ResearchTeamScreen.RECENT_RESEARCH_SPRITES.get(true, this.isItemHovered(index, mouseX, mouseY));
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED, resourcelocation, left, top, this.getItemWidth(), this.getItemHeight());

        Matrix3x2fStack poseStack = guiGraphics.pose();
        float scale = 1.75f;
        int padding = (int) ((34f - 16f * scale) / 2f); // 32 + 2 (smth smth border 2px)

        ResearchScreen.CLIENT_ICONS
                .get(research.getResearch().identifier())
                .render(
                        guiGraphics,
                        (int) (((float) left + padding) / scale),
                        (int) (((float) top + padding) / scale),
                        mouseX,
                        mouseY,
                        scale,
                        0);

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        Component researchName = research.getDisplayName(level);
        guiGraphics.text(minecraft.font, researchName, left + 32, top + 4, 0xFFFFFFFF);

        UUID researchedByUUID = research.getResearchedPlayer();
        String researchedBy;
        if (researchedByUUID != null) {
            researchedBy = PlayerUtils.getPlayerNameFromUUID(level, researchedByUUID);
        } else {
            researchedBy = "NO UUID";
        }

        long researchedTime = research.getResearchedTime();

        String researchedDate = NumberUtils.getTimeDifferenceFormatted(0, researchedTime);

        Component metadata = ResearchdTranslations.component(
                ResearchdTranslations.Gui.RESEARCHED_BY_ON, researchedBy, researchedDate);
        poseStack.pushMatrix();
        {
            float metadataScale = 0.75f;
            poseStack.scale(metadataScale, metadataScale);
            guiGraphics.text(
                    minecraft.font,
                    metadata,
                    (int) ((left + 32) / metadataScale),
                    (int) ((top + 4 + 4 + minecraft.font.lineHeight) / metadataScale),
                    0xFFAAAAAA);
        }
        poseStack.popMatrix();
    }
}
