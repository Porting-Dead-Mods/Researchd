package com.portingdeadmods.researchd.client.screens.team;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BaseTeamScreen extends Screen {
    protected int topPos;
    protected int leftPos;
    protected final int textureWidth;
    protected final int textureHeight;
    protected final int width;
    protected final int height;

    protected BaseTeamScreen(Component title, int textureWidth, int textureHeight, int width, int height) {
        super(title);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        Window window = Minecraft.getInstance().getWindow();
        this.leftPos = (window.getGuiScaledWidth() - this.width) / 2;
        this.topPos = (window.getGuiScaledHeight() - this.height) / 2;
    }

}
