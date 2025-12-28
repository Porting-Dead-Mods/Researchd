package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ResearchMethodTypePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ResearchMethodSelectionWidget extends AbstractWidget {
    private ResearchMethodTypePopupWidget methodTypePopupWidget;
    private @Nullable PopupWidget parentPopupWidget;

    public ResearchMethodSelectionWidget(@Nullable PopupWidget parentPopupWidget, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.parentPopupWidget = parentPopupWidget;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.drawCenteredString(PopupWidget.getFont(), "Create Method", this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - PopupWidget.getFont().lineHeight) / 2, -1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.parentPopupWidget != null) {
            Spaghetti.tryGetResearchScreen().closePopup(this.parentPopupWidget);
        }
        this.methodTypePopupWidget = Spaghetti.tryGetResearchScreen().openPopupCentered(new ResearchMethodTypePopupWidget(this.parentPopupWidget, CommonComponents.EMPTY));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

}
