package com.portingdeadmods.researchd.client.screens.editor.widgets.popups;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.ClientResearch;
import com.portingdeadmods.researchd.api.client.ClientResearchMethodType;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ResearchMethodCreationPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation DEFAULT_ID = ConsumeItemResearchMethod.ID;

    private final RememberingLinearLayout layout;
    private final ClientResearchMethodType clientResearchMethod;

    public ResearchMethodCreationPopupWidget(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        LinearLayout l = new LinearLayout(width, height, LinearLayout.Orientation.VERTICAL);
        this.layout = new RememberingLinearLayout(l);
        this.clientResearchMethod = ResearchdClient.CLIENT_RESEARCH_METHOD_TYPES.get(DEFAULT_ID);
        this.buildLayout();
    }

    public int getHorizontalPadding() {
        return 8;
    }

    public int getVerticalPadding() {
        return 8;
    }

    protected void buildLayout() {
        if (this.clientResearchMethod != null) {
            this.clientResearchMethod.buildLayout(this.layout, new ClientResearch.Context(Spaghetti.tryGetResearchScreen(), this, this.getWidth(), this.getHeight(), this.getWidth() - 14, this.getHeight() - 14, 7));
            this.layout.getLayout().arrangeElements();
            FrameLayout.centerInRectangle(this.layout.getLayout(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
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

        guiGraphics.blitSprite(EditorSharedSprites.EDITOR_WIDGET_BACKGROUND_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        super.renderElements(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    public @Nullable Layout getLayout() {
        return this.layout.getLayout();
    }
}
