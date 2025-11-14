package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class DropDownWidget<P extends LayoutElement> implements GuiEventListener {
    private P parent;
    private boolean visible;
    private boolean focused;
    private List<DropDownWidget.Option> options;

    public DropDownWidget() {
        this.visible = false;
        this.focused = false;
    }

    public final void rebuildOptions() {
        this.options = new ArrayList<>();
        this.buildOptions();
        this.options = ImmutableList.copyOf(this.options);
    }

    protected abstract void buildOptions();

    protected DropDownWidget.Option addOption(DropDownWidget.Option option) {
        this.options.add(option);
        return option;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            // render background
            int curHeight = 0;
            for (Option option : this.options) {
                option.render(guiGraphics, x, y + curHeight, mouseX, mouseY, partialTicks);
                curHeight += option.height();
            }
        }
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    public void open() {
        this.visible = true;
    }

    public void close() {
        this.visible = false;
    }

    public interface Option {
        int width();

        int height();

        void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks);
    }

    public record StringOption(Component value, Font font) implements Option {
        @Override
        public int width() {
            return this.font().width(this.value());
        }

        @Override
        public int height() {
            return this.font().lineHeight;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
            guiGraphics.drawString(this.font(), this.value(), x, y, -1);
        }

    }

}
