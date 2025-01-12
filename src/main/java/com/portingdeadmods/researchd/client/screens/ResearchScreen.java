package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechList;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.client.screens.queue.ResearchQueue;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResearchScreen extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_screen.png");

    private final TechList techList;
    private final ResearchQueue researchQueue;

    public ResearchScreen() {
        super(Component.translatable("screen.researchd.research"));
        this.techList = new TechList(0, 103, 7, 7);
        this.techList.fillList();
        this.researchQueue = new ResearchQueue(0, 0);
        this.researchQueue.fillList();
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        Researchd.LOGGER.debug("Width: {}", width);
    }

    @Override
    protected void init() {
        super.init();

        this.techList.visitWidgets(this::addRenderableWidget);
        addRenderableWidget(this.researchQueue);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

}
