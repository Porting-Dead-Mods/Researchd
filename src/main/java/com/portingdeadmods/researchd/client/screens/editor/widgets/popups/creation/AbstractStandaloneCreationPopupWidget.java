package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation;

import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.StandaloneEditorObject;
import com.portingdeadmods.researchd.client.impl.editor.EditorContextImpl;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.SelectPackPopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ScrollableWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class AbstractStandaloneCreationPopupWidget<O> extends DraggablePopupWidget {
    private final RememberingLinearLayout layout;
    private final StandaloneEditorObject<? extends O> clientObject;
    private final ResearchScreen screen;
    private final Function<ResourceLocation, StandaloneEditorObject<? extends O>> editorObjectGetterFunction;
    @Nullable
    private final O previous;
    private PDLButton createButton;
    private final ScrollableWidget<LinearLayout> scrollableWidget;
    private final ResourceLocation defaultId;

    public AbstractStandaloneCreationPopupWidget(ResourceLocation defaultId, Function<ResourceLocation, StandaloneEditorObject<? extends O>> editorObjectGetterFunction, @Nullable O previous, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.editorObjectGetterFunction = editorObjectGetterFunction;
        this.previous = previous;
        this.screen = Spaghetti.tryGetResearchScreen();
        LinearLayout l = new LinearLayout(width - 14, height - 14, LinearLayout.Orientation.VERTICAL);
        this.layout = new RememberingLinearLayout(l);
        this.defaultId = defaultId;
        this.clientObject = editorObjectGetterFunction.apply(defaultId);
        this.buildLayout();
        this.scrollableWidget = new ScrollableWidget<>(this.layout.getLayout(), x + 7, y + 7, width - 14, height - 36 - 14, CommonComponents.EMPTY);
        this.addRenderableWidget(this.scrollableWidget);
    }

    @Override
    protected void onOpen() {
        //this.scrollableWidget.resetScrollOffset();
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
        if (this.clientObject != null) {
            EditorContextImpl context = new EditorContextImpl(this.createButton, this.screen, this, this.getWidth(), this.getHeight(), this.getWidth() - 16, this.getHeight() - 16, 7);
            this.buildLayoutFromPrevious(context);
            this.clientObject.update(this.layout, context);
            this.layout.getLayout().arrangeElements();
        }
    }

    private <P extends O> void buildLayoutFromPrevious(EditorContextImpl context) {
        StandaloneEditorObject<P> obj = (StandaloneEditorObject<P>) this.clientObject;
        obj.buildLayout(this.layout, (P) this.previous, context);
    }

    private void onCreatePressed(PDLButton pdlButton) {
        O object = this.clientObject.create(this.layout);
        ResourceLocation id = this.clientObject.createId(this.layout);
        this.insertObjectToData(id, object);
        this.screen.closePopup(this);
    }

    protected abstract void insertObjectToData(ResourceLocation id, O object);

    protected abstract Component getTitle();

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

        guiGraphics.drawScrollingString(getFont(), this.getTitle(), this.getX() + 5, this.getX() + this.getWidth() - 5, this.getY() + 8, -1);

        super.renderElements(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    public @Nullable Layout getLayout() {
        return this.layout.getLayout();
    }
}
