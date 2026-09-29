package com.portingdeadmods.researchd.client.screens.lib.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class AbstractLayoutWidget<L extends Layout> extends AbstractContainerWidget
        implements LayoutWidget<L> {
    protected final @Nullable L layout;
    private final List<AbstractWidget> widgets;

    public AbstractLayoutWidget(@Nullable L layout, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message, AbstractScrollArea.defaultSettings(10));
        this.layout = layout;
        if (this.layout != null) {
            this.layout.setPosition(x, y);
        }
        this.widgets = new ArrayList<>();
    }

    @Override
    public @org.jetbrains.annotations.Nullable L getLayout() {
        return this.layout;
    }

    @Override
    public Iterable<? extends LayoutElement> getElements() {
        return this.widgets;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.widgets;
    }

    protected <W extends AbstractWidget> W addRenderableWidget(W widget) {
        this.widgets.add(widget);
        return widget;
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        LayoutWidget.super.renderElements(guiGraphics, mouseX, mouseY, partialTick);
    }

    // AbstractContainerWidget is a scroll area on 26.1. Keep it unscrollable and hand
    // scrolling to the children, as the plain container did on 1.21.1.
    @Override
    protected int contentHeight() {
        return this.getHeight();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.getChildAt(mouseX, mouseY)
                .filter(child -> child.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
                .isPresent();
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
