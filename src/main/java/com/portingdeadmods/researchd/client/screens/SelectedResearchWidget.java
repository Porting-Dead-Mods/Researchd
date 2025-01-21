package com.portingdeadmods.researchd.client.screens;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.registries.Researches;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedResearchWidget extends AbstractWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/selected_research.png");
    public static final int BACKGROUND_WIDTH = 174;
    public static final int BACKGROUND_HEIGHT = 61;
    private TechListEntry entry;
    private final Map<ResourceLocation, List<ResearchMethod>> methods;

    public SelectedResearchWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.methods = new Object2ObjectArrayMap<>();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);

        guiGraphics.enableScissor(53, 55, 53 + 115, 55 + 48);

        if (entry != null) {
            entry.renderWidget(guiGraphics, mouseX, mouseY, v, 2);

            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Utils.registryTranslation(this.entry.getResearch().getResearch()), 12, 45, -1, false);
            int lineHeight = font.lineHeight + 2;

            int height = 0;
            for (Map.Entry<ResourceLocation, List<ResearchMethod>> methodEntry : this.methods.entrySet()) {
                for (ResearchMethod method : methodEntry.getValue()) {
                    guiGraphics.drawString(font, Component.literal("Researched by"), 56, 57, -1, false);
                    ClientResearchMethod clientMethod = method.getClientMethod();
                    clientMethod.renderMethodTooltip(guiGraphics, methodEntry.getValue(), 56, 57 + lineHeight, mouseX, mouseY);
                }
                height += methodEntry.getValue().isEmpty() ? 0 : methodEntry.getValue().getFirst().getClientMethod().height();
            }

            guiGraphics.drawString(font, "Effects", 56, 57 + height + 6, -1, false);
        }

        guiGraphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public void setEntry(TechListEntry entry) {
        this.entry = entry.copy();
        this.entry.setX(12);
        this.entry.setY(55);

        this.methods.clear();
        List<ResearchMethod> methods = ResearchHelper.getResearch(this.entry.getResearch().getResearch(), Minecraft.getInstance().level.registryAccess()).researchMethods();
        for (ResearchMethod method : methods) {
            this.methods.computeIfAbsent(method.id(), key -> new ArrayList<>()).add(method);
        }
    }

    public TechListEntry getEntry() {
        return entry;
    }
}
