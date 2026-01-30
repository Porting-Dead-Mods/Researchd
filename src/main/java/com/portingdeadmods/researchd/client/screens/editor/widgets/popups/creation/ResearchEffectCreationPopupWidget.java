package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.ClientResearchEffectType;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.BaseResearchEffectCreationWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.SelectPackPopupWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.selection.ResearchEffectTypeSelectionPopupWidget;
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

public class ResearchEffectCreationPopupWidget extends PopupWidget {
    public static final ResourceLocation BACKGROUND_SPRITE = Researchd.rl("widget/research_method_creation_widget");

    private final RememberingLinearLayout layout;
    private final ClientResearchEffectType clientResearchEffect;
    private final PDLButton createButton;
    private final ResearchEffectTypeSelectionPopupWidget parentPopupWidget;
    //private final BaseResearchEffectCreationWidget originSelectionWidget;

    public ResearchEffectCreationPopupWidget(@Nullable ResearchEffectTypeSelectionPopupWidget parentPopupWidget, ResearchEffectType type, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.parentPopupWidget = parentPopupWidget;
        //this.originSelectionWidget = originSelectionWidget;
        LinearLayout l = new LinearLayout(width, height, LinearLayout.Orientation.VERTICAL);
        this.layout = new RememberingLinearLayout(l);
        this.clientResearchEffect = ResearchdClient.CLIENT_RESEARCH_EFFECT_TYPES.get(type.id());
        this.buildLayout();
        this.createButton = this.addRenderableWidget(PDLButton.builder(this::onCreateButtonPressed)
                .message(Component.literal("Create"))
                .sprites(SelectPackPopupWidget.EDITOR_BUTTON_SPRITES)
                .pos(x, y)
                .size(this.getWidth() - 18, 16)
                .build());
    }

    private void onCreateButtonPressed(PDLButton button) {
        //this.originSelectionWidget.setCreatedEffect(this.clientResearchEffect.createResearchEffect(this.layout));
        this.parentPopupWidget.addEffect(this.clientResearchEffect.createResearchEffect(this.layout));
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
        if (this.clientResearchEffect != null) {
            this.clientResearchEffect.buildLayout(this.layout, new ClientResearchEffectType.Context(this.createButton, Spaghetti.tryGetResearchScreen(), this, this.getWidth(), this.getHeight(), this.getWidth() - 14, this.getHeight() - 14, 7));
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

        ClientResearchIcon.getClientIcon(this.clientResearchEffect.type().icon()).render(guiGraphics, this.getX() + 3, this.getY() + 3, mouseX, mouseY, 1, partialTick);
        Component name = this.clientResearchEffect.type().getName();
        guiGraphics.drawScrollingString(getFont(), name, this.getX() + 21, this.getX() + this.getWidth() - 6, this.getY() + 7, -1);
    }

    @Override
    public @Nullable Layout getLayout() {
        return this.layout.getLayout();
    }
}
