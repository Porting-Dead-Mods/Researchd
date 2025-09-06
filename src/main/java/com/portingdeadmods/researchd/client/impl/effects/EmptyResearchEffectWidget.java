package com.portingdeadmods.researchd.client.impl.effects;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.common.util.Size2i;

public class EmptyResearchEffectWidget extends AbstractResearchInfoWidget<EmptyResearchEffect> {
    public EmptyResearchEffectWidget(int x, int y, EmptyResearchEffect value) {
        super(x, y, value);
    }

    @Override
    public Size2i getSize() {
        return new Size2i(0, 0);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
    }
}
