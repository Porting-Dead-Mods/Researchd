package com.portingdeadmods.researchd.client.screens.editor.widgets.popups;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.editor.ClientResearch;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.utils.PrettyPath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ResearchCreationPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation DEFAULT_ID = Researchd.rl(SimpleResearch.ID);

    private final RememberingLinearLayout layout;
    private final ClientResearch clientResearch;
    private final ResearchScreen screen;
    private PDLButton createButton;

    public ResearchCreationPopupWidget(ResearchScreen screen, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.screen = screen;
        LinearLayout l = new LinearLayout(width - 14, height - 14, LinearLayout.Orientation.VERTICAL);
        this.layout = new RememberingLinearLayout(l);
        this.clientResearch = ResearchdClient.CLIENT_RESEARCHES.get(DEFAULT_ID);
        this.buildLayout();
    }

    public int getHorizontalPadding() {
        return 8;
    }

    public int getVerticalPadding() {
        return 24;
    }

    protected void buildLayout() {
        if (this.clientResearch != null) {
            this.clientResearch.buildLayout(this.layout, new ClientResearch.Context(null, this.screen, this, this.getWidth(), this.getHeight(), this.getWidth() - 16, this.getHeight() - 16, 7));
            this.layout.getLayout().arrangeElements();
            this.layout.getChildren().forEach(this::addRenderableWidget);
        }
        this.createButton = this.addRenderableWidget(PDLButton.builder(this::onCreatePressed)
                .message(Component.literal("Create"))
                .sprites(SelectPackPopupWidget.EDITOR_BUTTON_SPRITES)
                        .size(100, 16)
                .build());
    }

    private void onCreatePressed(PDLButton pdlButton) {
        Research research = this.clientResearch.createResearch(this.layout);
        EditModeSettingsImpl settings = Minecraft.getInstance().player.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
        PrettyPath prettyPath = settings.currentDatapack();
        Researchd.LOGGER.debug("Full Path: {}", prettyPath.fullPath());
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        if (this.getLayout() != null) {
            this.getLayout().setX(x + this.getHorizontalPadding());
            this.createButton.setPosition(this.getX() + (width - this.createButton.getWidth()) / 2, this.createButton.getY());
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        if (this.getLayout() != null) {
            this.getLayout().setY(y + this.getVerticalPadding());
            this.createButton.setPosition(this.createButton.getX(), this.getY() + (height - this.createButton.getHeight()) / 2 + 78);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blitSprite(ResearchMethodCreationPopupWidget.BACKGROUND_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        guiGraphics.drawScrollingString(getFont(), Component.literal("Create Research"), this.getX() + 5, this.getX() + this.getWidth() - 5, this.getY() + 8, -1);

        super.renderElements(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    public @Nullable Layout getLayout() {
        return this.layout.getLayout();
    }
}
