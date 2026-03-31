package com.portingdeadmods.researchd.client.impl.info.effects;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.ValueEffectModifierEffect;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.common.util.Size2i;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ValueEffectModifierEffectWidget<T extends ValueEffectModifierEffect> extends AbstractResearchInfoWidget<T> {
    private List<Component> tooltip;

    public ValueEffectModifierEffectWidget(int x, int y, T value) {
        super(x, y, value);
    }

    public ValueEffectModifierEffectWidget(int x, int y, Object value) {
        this(x, y, (T) value);
    }

    @Override
    public Size2i getSize() {
        MutableComponent text = getText();
        return new Size2i(this.font.width(text) + 4, 16);
    }

    private @NotNull MutableComponent getText() {
        return Component.literal(this.value.operator() + " " + this.value.amount());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), FastColor.ARGB32.color(69, 69, 69));
        guiGraphics.drawCenteredString(this.font, this.getText(), this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2 - 3, -1);

        if (this.isHovered()) {
            if (this.tooltip == null) {
                this.tooltip = List.of(this.value.desc());
            }
            GuiUtils.renderTooltip(this.tooltip);
        }
    }
}
