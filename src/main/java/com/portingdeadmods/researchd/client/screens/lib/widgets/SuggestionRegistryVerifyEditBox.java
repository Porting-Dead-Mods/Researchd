package com.portingdeadmods.researchd.client.screens.lib.widgets;

import com.portingdeadmods.researchd.client.screens.editor.widgets.dropdowns.RegistrySuggestionDropDownWidget;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.Nullable;

public class SuggestionRegistryVerifyEditBox extends RegistryVerifyEditBox {
    private final RegistrySuggestionDropDownWidget dropDown;

    public SuggestionRegistryVerifyEditBox(Font font, @Nullable Registry<?> registry, int width, int height) {
        super(font, BackgroundEditBox.SPRITES, registry, List.of(), width, height, CommonComponents.EMPTY);
        this.dropDown = new RegistrySuggestionDropDownWidget(
                registry, 0, 0, opt -> this.setValue(opt.value().getString()), this::getValue);
        this.dropDown.rebuildOptions();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // TODO(26.1 port, GUI parity): 26.1 GUI layering follows draw order, so the drop-down no longer sits above
        // widgets drawn after it; checked in the client visual parity pass
        this.dropDown.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onValueChangedExtra(String newText) {
        super.onValueChangedExtra(newText);

        if (this.dropDown != null) {
            this.dropDown.rebuildOptions();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.dropDown.isHovered() && this.dropDown.isVisible() && this.dropDown.mouseClicked(event, doubleClick)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
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
