package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ResearchMethodTypePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ResearchMethodSelectionWidget extends AbstractWidget {
    private ResearchMethodTypePopupWidget methodTypePopupWidget;
    private @Nullable PopupWidget parentPopupWidget;
    private @Nullable ResearchMethod createdMethod;
    private @Nullable AbstractResearchInfoWidget<? extends ResearchMethod> createdMethodInfoWidget;

    public ResearchMethodSelectionWidget(@Nullable PopupWidget parentPopupWidget, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.parentPopupWidget = parentPopupWidget;
    }

    public void setCreatedMethod(ResearchMethod method) {
        this.createdMethod = method;
        this.createdMethodInfoWidget = ResearchdClient.RESEARCH_METHOD_WIDGETS.get(method.id()).createMethod(this.getX(), this.getY(), this.createdMethod);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if (this.createdMethod == null) {
            guiGraphics.drawCenteredString(PopupWidget.getFont(), "Create Method", this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - PopupWidget.getFont().lineHeight) / 2, -1);
        } else {
            this.createdMethodInfoWidget.render(guiGraphics, mouseX, mouseY, partialTick);
            this.createdMethodInfoWidget.renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (this.parentPopupWidget != null) {
                Spaghetti.tryGetResearchScreen().closePopup(this.parentPopupWidget);
            }
            this.methodTypePopupWidget = Spaghetti.tryGetResearchScreen().openPopupCentered(new ResearchMethodTypePopupWidget(this.parentPopupWidget, this, CommonComponents.EMPTY));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

}
