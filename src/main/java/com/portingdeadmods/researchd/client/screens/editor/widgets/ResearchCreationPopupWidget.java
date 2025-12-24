package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.ClientResearch;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ResearchCreationPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation BACKGROUND_SPRITE = Researchd.rl("widget/data_creation_widget");
    public static final ResourceLocation DEFAULT_ID = Researchd.rl(SimpleResearch.ID);

    private final RememberingLinearLayout layout;
    private final ClientResearch clientResearch;
    private final ResearchScreen screen;

    public ResearchCreationPopupWidget(ResearchScreen screen, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.screen = screen;
        LinearLayout l = LinearLayout.vertical();
        this.layout = new RememberingLinearLayout(l);
        this.clientResearch = ResearchdClient.CLIENT_RESEARCHES.get(DEFAULT_ID);
        this.buildLayout();
    }

    public int getHorizontalPadding() {
        return 8;
    }

    public int getVerticalPadding() {
        return 8;
    }

    protected void buildLayout() {
        if (this.clientResearch != null) {
            this.clientResearch.buildLayout(this.layout, new ClientResearch.Context(this.screen, this));
            this.layout.getLayout().arrangeElements();
            this.layout.getChildren().forEach(this::addRenderableWidget);
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        if (this.getLayout() != null) {
            this.getLayout().setX(x + this.getHorizontalPadding());
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        if (this.getLayout() != null) {
            this.getLayout().setY(y + this.getVerticalPadding());
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blitSprite(BACKGROUND_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        super.renderElements(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    public @Nullable Layout getLayout() {
        return this.layout.getLayout();
    }
}
