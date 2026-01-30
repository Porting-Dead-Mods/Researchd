package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.client.editor.ClientResearch;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.SelectPackPopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ScrollableWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.networking.editor.CreateResearchPayload;
import com.portingdeadmods.researchd.resources.editor.EditorResearchProvider;
import com.portingdeadmods.researchd.utils.PrettyPath;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class ResearchCreationPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation DEFAULT_ID = Researchd.rl(SimpleResearch.ID);

    private final RememberingLinearLayout layout;
    private final ClientResearch clientResearch;
    private final ResearchScreen screen;
    private PDLButton createButton;
    private final ScrollableWidget<LinearLayout> scrollableWidget;

    public ResearchCreationPopupWidget(ResearchScreen screen, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.screen = screen;
        LinearLayout l = new LinearLayout(width - 14, height - 14, LinearLayout.Orientation.VERTICAL);
        this.layout = new RememberingLinearLayout(l);
        this.clientResearch = ResearchdClient.CLIENT_RESEARCHES.get(DEFAULT_ID);
        this.buildLayout();
        this.scrollableWidget = new ScrollableWidget<>(this.layout.getLayout(), x + 7, y + 7, width - 14, height - 36 - 14, CommonComponents.EMPTY);
        this.addRenderableWidget(this.scrollableWidget);
    }

    public int getHorizontalPadding() {
        return 8;
    }

    public int getVerticalPadding() {
        return 24;
    }

    protected void buildLayout() {
        this.createButton = this.addRenderableWidget(PDLButton.builder(this::onCreatePressed)
                .message(Component.literal("Create"))
                .sprites(SelectPackPopupWidget.EDITOR_BUTTON_SPRITES)
                .size(100, 16)
                .build());
        if (this.clientResearch != null) {
            ClientResearch.Context context = new ClientResearch.Context(this.createButton, this.screen, this, this.getWidth(), this.getHeight(), this.getWidth() - 16, this.getHeight() - 16, 7);
            this.clientResearch.buildLayout(this.layout, context);
            this.clientResearch.updateResearch(this.layout, context);
            this.layout.getLayout().arrangeElements();
        }
    }

    private void onCreatePressed(PDLButton pdlButton) {
        Research research = this.clientResearch.createResearch(this.layout);
        ResourceLocation id = this.clientResearch.createId(this.layout);
        PacketDistributor.sendToServer(new CreateResearchPayload(ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, id), research, true));
        this.screen.closePopup(this);
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        if (this.getLayout() != null) {
            this.scrollableWidget.setX(x + this.getHorizontalPadding());
            this.createButton.setPosition(this.getX() + (width - this.createButton.getWidth()) / 2, this.createButton.getY());
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        if (this.getLayout() != null) {
            this.scrollableWidget.setY(y + this.getVerticalPadding());
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
