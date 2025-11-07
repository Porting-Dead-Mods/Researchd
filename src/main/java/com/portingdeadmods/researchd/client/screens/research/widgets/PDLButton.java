package com.portingdeadmods.researchd.client.screens.research.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.portingdeadmods.researchd.api.team.ResearchQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class PDLButton extends AbstractButton {
    public static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );
    protected static final Button.CreateNarration DEFAULT_NARRATION = Supplier::get;
    private final PDLButton.OnPress<PDLButton> onPress;
    private final Button.CreateNarration createNarration;
    private final WidgetSprites sprites;

    protected PDLButton(
            int x, int y, int width, int height, WidgetSprites sprites, Component message, PDLButton.OnPress<PDLButton> onPress, Button.CreateNarration createNarration
    ) {
        super(x, y, width, height, message);
        this.sprites = sprites;
        this.onPress = onPress;
        this.createNarration = createNarration;
    }

    protected PDLButton(PDLButton.Builder<PDLButton> builder) {
        this(builder.x, builder.y, builder.width, builder.height, builder.sprites, builder.message, builder.onPress, builder.createNarration);
        setTooltip(builder.tooltip);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blitSprite(this.sprites.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = getFGColor();
        this.renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return this.createNarration.createNarrationMessage(super::createNarrationMessage);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    public static <B extends AbstractButton> Builder<B> builder(Function<PDLButton.Builder<B>, B> buttonFactory, OnPress<B> onPress) {
        return new Builder<>(buttonFactory, onPress);
    }

    public static Builder<PDLButton> builder(OnPress<PDLButton> onPress) {
        return builder(PDLButton::new, onPress);
    }

    public static class Builder<B extends AbstractButton> {
        public Component message = CommonComponents.EMPTY;
        public final Function<Builder<B>, B> buttonFactory;
        public final PDLButton.OnPress<B> onPress;
        @Nullable
        public Tooltip tooltip;
        public int x;
        public int y;
        public int width = 150;
        public int height = 20;
        public Button.CreateNarration createNarration = PDLButton.DEFAULT_NARRATION;
        public WidgetSprites sprites = PDLButton.SPRITES;

        private Builder(Function<PDLButton.Builder<B>, B> buttonFactory, PDLButton.OnPress<B> onPress) {
            this.buttonFactory = buttonFactory;
            this.onPress = onPress;
        }

        public PDLButton.Builder<B> pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public PDLButton.Builder<B> width(int width) {
            this.width = width;
            return this;
        }

        public PDLButton.Builder<B> size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public PDLButton.Builder<B> bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        public PDLButton.Builder<B> tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public PDLButton.Builder<B> createNarration(Button.CreateNarration createNarration) {
            this.createNarration = createNarration;
            return this;
        }

        public PDLButton.Builder<B> message(Component message) {
            this.message = message;
            return this;
        }

        public PDLButton.Builder<B> sprites(WidgetSprites sprites) {
            this.sprites = sprites;
            return this;
        }

        public B build() {
            return this.buttonFactory.apply(this);
        }
    }

    @FunctionalInterface
    public interface ButtonFactory<B extends AbstractButton> {
        B create(int x, int y, int width, int height, WidgetSprites sprites, OnPress<B> onPress, Component message);
    }

    @FunctionalInterface
    public interface OnPress<B extends AbstractButton> extends Button.OnPress {
        void onPress(B button);

        @Override
        default void onPress(Button button) {
            this.onPress((B) button);
        }
    }
}
