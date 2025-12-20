package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.impl.research.ItemResearchIcon;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSelectorWidget extends AbstractWidget {
    private final ResearchScreen screen;
    private ItemStack selected;

    public ItemSelectorWidget(ResearchScreen screen, int x, int y, Component message) {
        super(x, y, 18, 18, message);
        this.screen = screen;
        this.setTooltip(Tooltip.create(Component.literal("Select Icon")));
        this.selected = new ItemStack(Items.DIRT);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.selected != null) {
            guiGraphics.renderItem(this.selected, this.getX() + 1, this.getY() + 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.screen.openPopupCentered(new SelectorPopupWidget(0, 0, CommonComponents.EMPTY));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public List<ItemStack> getSelected() {
        return List.of(selected);
    }

    public ItemResearchIcon createIcon() {
        return new ItemResearchIcon(this.getSelected());
    }

    public static class SelectorPopupWidget extends DraggablePopupWidget {
        public SelectorPopupWidget(int x, int y, Component message) {
            super(x, y, 256, 256, message);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        }

        @Override
        public Layout getLayout() {
            return null;
        }
    }

}
