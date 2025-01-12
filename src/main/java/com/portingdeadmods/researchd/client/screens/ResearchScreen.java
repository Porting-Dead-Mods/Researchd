package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.ResearchManager;
import com.portingdeadmods.researchd.client.screens.graph.ResearchGraph;
import com.portingdeadmods.researchd.client.screens.list.TechList;
import com.portingdeadmods.researchd.client.screens.queue.ResearchQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResearchScreen extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_screen.png");

    private final TechList techList;
    private final ResearchQueue researchQueue;
    private final ResearchGraph researchGraph;

    public ResearchScreen() {
        super(Component.translatable("screen.researchd.research"));
        this.techList = new TechList(0, 103, 7, 7);
        this.techList.fillList();
        this.researchQueue = new ResearchQueue(0, 0);
        this.researchQueue.fillList();
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        Researchd.LOGGER.debug("Width: {}", width);
        int x = 174;
        ResearchManager manager = new ResearchManager(mc.level);
        manager.setCoordinates(174, 10);
        this.researchGraph = new ResearchGraph(manager, x, 0, 300, 253);
        //ResearchNode node = new ResearchNode(SimpleResearch.debug(Items.IRON_NUGGET), EntryType.RESEARCHED, x + 60, 60);
        //this.researchGraph.setNode(node);
        //node.addNext(new ResearchNode(SimpleResearch.debug(Items.IRON_BARS), EntryType.RESEARCHABLE, x + 70, 100));
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(this.techList);
        addRenderableWidget(this.researchQueue);
        addRenderableWidget(this.techList.button);
        addRenderableWidget(this.researchGraph);
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
