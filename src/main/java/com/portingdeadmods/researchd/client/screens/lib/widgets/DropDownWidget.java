package com.portingdeadmods.researchd.client.screens.lib.widgets;

import com.google.common.collect.ImmutableList;
import com.portingdeadmods.researchd.Researchd;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;

public abstract class DropDownWidget<P extends LayoutElement> implements GuiEventListener {
    private P parent;
    private boolean visible;
    private boolean focused;
    private boolean hovered;
    private Option hoveredOption;
    private List<DropDownWidget.Option> options;

    public DropDownWidget() {
        this.visible = false;
        this.focused = false;
        this.setVisible(true);
    }

    public final void rebuildOptions() {
        this.options = new ArrayList<>();
        this.buildOptions();
        this.options = ImmutableList.copyOf(this.options);
    }

    protected abstract void buildOptions();

    protected Identifier getBackgroundTexture() {
        return Researchd.rl("dropdown_background");
    }

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

    public boolean isHovered() {
        return hovered;
    }

    public void render(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible || this.options == null || this.options.isEmpty()) return;

        int width = 4;
        int height = 2;
        for (Option option : this.options) {
            if (option.width() > width - 4) {
                width = option.width() + 4;
            }
            height += option.height() + 2;
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getBackgroundTexture(), x, y, width, height);

        // render background
        int curHeight = 0;
        for (Option option : this.options) {
            OptionContext context = new OptionContext(width - 2, height - 4);
            option.render(guiGraphics, x + 2, y + curHeight + 2, mouseX, mouseY, partialTicks, context);
            if (option.isHovered(x + 2, y + curHeight + 2, mouseX, mouseY, context)) {
                hoveredOption = option;
            }
            curHeight += option.height() + 2;
        }

        this.hovered = mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (hoveredOption != null && !this.options.isEmpty()) {
            optionClicked(hoveredOption, (int) event.x(), (int) event.y());
            this.playDownSound(Minecraft.getInstance().getSoundManager());
        }
        return false;
    }

    protected void optionClicked(Option option, int mouseX, int mouseY) {
        option.clicked(mouseX, mouseY);
    }

    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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

        default boolean isHovered(int x, int y, int mouseX, int mouseY) {
            return mouseX > x && mouseY > y && mouseX < x + this.width() && mouseY < y + this.height();
        }

        default boolean isHovered(int x, int y, int mouseX, int mouseY, OptionContext context) {
            return mouseX > x && mouseY > y - 1 && mouseX < x + context.maxWidth() && mouseY < y + this.height() + 1;
        }

        default void clicked(int mouseX, int mouseY) {}

        void render(
                GuiGraphicsExtractor guiGraphics,
                int x,
                int y,
                int mouseX,
                int mouseY,
                float partialTicks,
                OptionContext context);
    }

    public record OptionContext(int maxWidth, int totalHeight) {}

    public record StringOption(Component value, Font font, Consumer<StringOption> onClicked) implements Option {
        public StringOption(Component value, Font font) {
            this(value, font, opt -> {});
        }

        @Override
        public int width() {
            return this.font().width(this.value());
        }

        @Override
        public int height() {
            return this.font().lineHeight;
        }

        @Override
        public void clicked(int mouseX, int mouseY) {
            this.onClicked().accept(this);
        }

        @Override
        public void render(
                GuiGraphicsExtractor guiGraphics,
                int x,
                int y,
                int mouseX,
                int mouseY,
                float partialTicks,
                OptionContext context) {
            if (this.isHovered(x, y, mouseX, mouseY, context)) {
                guiGraphics.fill(
                        x - 1, y - 1, x + context.maxWidth() - 1, y + this.height() + 1, ARGB.color(120, 120, 120));
            }

            guiGraphics.text(this.font(), this.value(), x, y, -1);
        }
    }
}
