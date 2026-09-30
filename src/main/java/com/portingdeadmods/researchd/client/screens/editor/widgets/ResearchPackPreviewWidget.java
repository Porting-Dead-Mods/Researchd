package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.portingdeadlibs.api.misc.RGBAColor;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

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
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int i1, float v) {
        ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();

        ResearchdClient.previewRendererResearchPackColor = this.getColor().toARGB();
        guiGraphics.item(stack, this.getX(), this.getY());
        ResearchdClient.previewRendererResearchPackColor = -1;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
