package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.editor.ClientResearchMethodType;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.BaseResearchMethodCreationWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.SelectPackPopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ResearchMethodCreationPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation BACKGROUND_SPRITE = Researchd.rl("widget/research_method_creation_widget");

    private final RememberingLinearLayout layout;
    private final ClientResearchMethodType clientResearchMethod;
    private final PDLButton createButton;
    private final PopupWidget parentPopupWidget;
    private final BaseResearchMethodCreationWidget originSelectionWidget;

    public ResearchMethodCreationPopupWidget(@Nullable PopupWidget parentPopupWidget, ResearchMethodType type, BaseResearchMethodCreationWidget originSelectionWidget, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.parentPopupWidget = parentPopupWidget;
        this.originSelectionWidget = originSelectionWidget;
        LinearLayout l = new LinearLayout(width, height, LinearLayout.Orientation.VERTICAL);
        this.layout = new RememberingLinearLayout(l);
        this.clientResearchMethod = ResearchdClient.CLIENT_RESEARCH_METHOD_TYPES.get(type.id());
        this.buildLayout();
        this.createButton = this.addRenderableWidget(PDLButton.builder(this::onCreateButtonPressed)
                .message(Component.literal("Create"))
                .sprites(SelectPackPopupWidget.EDITOR_BUTTON_SPRITES)
                .pos(x, y)
                .size(this.getWidth() - 18, 16)
                .build());
    }

    private void onCreateButtonPressed(PDLButton button) {
        this.originSelectionWidget.setCreatedMethod(this.clientResearchMethod.createResearchEffect(this.layout));
        ResearchScreen screen = Spaghetti.tryGetResearchScreen();
        screen.closePopup(this);
        if (this.parentPopupWidget != null) {
            screen.openPopupCentered(this.parentPopupWidget);
        }
    }

    public int getHorizontalPadding() {
        return 8;
    }

    public int getVerticalPadding() {
        return 8;
    }

    protected void buildLayout() {
        if (this.clientResearchMethod != null) {
            this.clientResearchMethod.buildLayout(this.layout, new ClientResearchMethodType.Context(this.createButton, Spaghetti.tryGetResearchScreen(), this, this.getWidth(), this.getHeight(), this.getWidth() - 14, this.getHeight() - 14, 7));
            this.layout.getLayout().arrangeElements();
            FrameLayout.centerInRectangle(this.layout.getLayout(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            this.layout.getChildren().forEach(this::addRenderableWidget);
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        if (this.getLayout() != null) {
            this.getLayout().setX(x + (this.getWidth() - this.getLayout().getWidth()) / 2);
            this.createButton.setX(x + 9);
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        if (this.getLayout() != null) {
            this.getLayout().setY(y + (this.getHeight() - this.getLayout().getHeight()) / 2);
            this.createButton.setY(y + this.getHeight() - 5 - 16);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(BACKGROUND_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        ClientResearchIcon.getClientIcon(this.clientResearchMethod.type().icon()).render(guiGraphics, this.getX() + 3, this.getY() + 3, mouseX, mouseY, 1, partialTick);
        Component name = this.clientResearchMethod.type().getName();
        guiGraphics.drawScrollingString(getFont(), name, this.getX() + 21, this.getX() + this.getWidth() - 6, this.getY() + 7, -1);
    }

    @Override
    public @Nullable Layout getLayout() {
        return this.layout.getLayout();
    }
}
