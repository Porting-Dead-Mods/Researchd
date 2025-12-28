package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.portingdeadmods.researchd.client.screens.lib.widgets.EditBoxExtension;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EditBox.class)
public class EditBoxMixin implements EditBoxExtension {
    @ModifyExpressionValue(
            method = "renderWidget",
            at = @At(
                    value = "FIELD",
                    target = "net/minecraft/client/gui/components/EditBox.SPRITES : Lnet/minecraft/client/gui/components/WidgetSprites;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private WidgetSprites replaceSpritesField(WidgetSprites original) {
        return this.getSprites(original);
    }

    @Override
    public WidgetSprites getSprites(WidgetSprites original) {
        return original;
    }
}
