package com.portingdeadmods.researchd.client.screens.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QueueControllsButton extends ImageButton {
	protected final WidgetSprites sprites;
	private final int index;
	private final String type;

	public QueueControllsButton(int index, String type, int x, int y, int width, int height, WidgetSprites sprites, Button.OnPress onPress, Component message) {
		super(x, y, width, height, sprites, onPress, message);
		this.index = index;
		this.type = type;
		this.sprites = sprites;
	}

	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		ResourceLocation resourcelocation = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
		guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
	}

	public int getIndex() {
		return this.index;
	}

	public String getType() {
		return this.type;
	}
}
