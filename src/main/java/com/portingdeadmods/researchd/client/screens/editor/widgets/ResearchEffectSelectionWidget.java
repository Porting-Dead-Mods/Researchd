package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ResearchEffectTypeSelectionPopupWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ResearchMethodTypePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ResearchEffectSelectionWidget extends AbstractWidget {
    private ResearchEffectTypeSelectionPopupWidget effectTypePopupWidget;
    private final @Nullable PopupWidget parentPopupWidget;
    private ResearchEffect createdEffect;
    private AbstractResearchInfoWidget<? extends ResearchEffect> createdEffectInfoWidget;

    public ResearchEffectSelectionWidget(@Nullable PopupWidget parentPopupWidget, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.createdEffect = EmptyResearchEffect.INSTANCE;
        this.parentPopupWidget = parentPopupWidget;
    }

    public void setCreatedEffect(ResearchEffect effect) {
        this.createdEffect = effect;
        this.createdEffectInfoWidget = ResearchdClient.RESEARCH_EFFECT_WIDGETS.get(effect.id()).createEffect(this.getX(), this.getY(), this.createdEffect);
        this.createdEffectInfoWidget.setPosition(this.getX() + (this.width - this.createdEffectInfoWidget.getWidth()) / 2, this.getY() + (this.height - this.createdEffectInfoWidget.getHeight()) / 2);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if (this.createdEffect == null || this.createdEffect == EmptyResearchEffect.INSTANCE) {
            guiGraphics.drawCenteredString(PopupWidget.getFont(), "Create Effect", this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - PopupWidget.getFont().lineHeight) / 2, -1);
        } else {
            this.createdEffectInfoWidget.render(guiGraphics, mouseX, mouseY, partialTick);
            this.createdEffectInfoWidget.renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (this.parentPopupWidget != null) {
                Spaghetti.tryGetResearchScreen().closePopup(this.parentPopupWidget);
            }
            this.effectTypePopupWidget = Spaghetti.tryGetResearchScreen().openPopupCentered(new ResearchEffectTypeSelectionPopupWidget(this.parentPopupWidget, this));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        if (this.createdEffectInfoWidget != null) {
            this.createdEffectInfoWidget.setX(x + (this.width - this.createdEffectInfoWidget.getWidth()) / 2);
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        if (this.createdEffectInfoWidget != null) {
            this.createdEffectInfoWidget.setY(y + (this.height - this.createdEffectInfoWidget.getHeight()) / 2);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public ResearchEffect getEffect() {
        return this.createdEffect;
    }

}
