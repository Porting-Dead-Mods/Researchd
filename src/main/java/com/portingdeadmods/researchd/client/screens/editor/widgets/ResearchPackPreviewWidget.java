package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.portingdeadlibs.api.utils.RGBAColor;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ResearchPackPreviewWidget extends AbstractWidget {
    private final Supplier<RGBAColor> colorSupplier;

    public ResearchPackPreviewWidget(Supplier<RGBAColor> colorSupplier, int width, int height) {
        this(colorSupplier, 0, 0, width, height);
    }

    public ResearchPackPreviewWidget(Supplier<RGBAColor> colorSupplier, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.colorSupplier = colorSupplier;
    }

    protected RGBAColor getColor() {
        return this.colorSupplier.get();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();

        ResearchdClient.previewRendererResearchPackColor = this.getColor().toARGB();
        guiGraphics.renderItem(stack, this.getX(), this.getY());
        ResearchdClient.previewRendererResearchPackColor = -1;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
