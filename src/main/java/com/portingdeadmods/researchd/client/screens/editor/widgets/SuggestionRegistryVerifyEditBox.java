package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.client.screens.editor.widgets.dropdowns.RegistrySuggestionDropDownWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class SuggestionRegistryVerifyEditBox extends RegistryVerifyEditBox {
    private final RegistrySuggestionDropDownWidget dropDown;

    public SuggestionRegistryVerifyEditBox(Font font, @Nullable Registry<?> registry, int width, int height) {
        super(font, BackgroundEditBox.SPRITES, registry, List.of(), width, height, CommonComponents.EMPTY);
        this.dropDown = new RegistrySuggestionDropDownWidget(registry, 0, 0, opt -> this.setValue(opt.value().getString()), this::getValue);
        this.dropDown.rebuildOptions();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 10);
            this.dropDown.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        poseStack.popPose();
    }

    @Override
    public void onValueChangedExtra(String newText) {
        super.onValueChangedExtra(newText);

        if (this.dropDown != null) {
            this.dropDown.rebuildOptions();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.dropDown.isHovered() && this.dropDown.isVisible() && this.dropDown.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        this.dropDown.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        this.dropDown.setY(y + 16);
    }
}
