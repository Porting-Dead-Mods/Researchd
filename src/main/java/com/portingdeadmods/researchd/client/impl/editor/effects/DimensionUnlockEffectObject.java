package com.portingdeadmods.researchd.client.impl.editor.effects;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.RegistryVerifyEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DimensionUnlockEffectObject implements TypedEditorObject<DimensionUnlockEffect, ResearchEffectType> {
    public static final ResourceLocation ID = Researchd.rl("dimension_unlock");
    public static final DimensionUnlockEffectObject INSTANCE = new DimensionUnlockEffectObject();

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.DIMENSION_UNLOCK.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @Nullable DimensionUnlockEffect previous, EditorContext context) {
        layout.addWidget(null, GuiUtils.stringWidget("Unlocks Dimension:"));
        Optional<Registry<DimensionType>> registry = Minecraft.getInstance().level.registryAccess().registry(Registries.DIMENSION_TYPE);
        registry.ifPresent(dimensionTypes -> {
            RegistryVerifyEditBox idEditBox = layout.addWidget("id_edit_box", RegistryVerifyEditBox.forRegistry(dimensionTypes, context.innerWidth() - 8, 16));
            idEditBox.setResponder(newVal -> this.update(layout, context));
            if (previous != null) {
                idEditBox.setValue(previous.dimension().toString());
            }
        });
    }

    @Override
    public DimensionUnlockEffect create(RememberingLinearLayout layout) {
        return new DimensionUnlockEffect(ResourceLocation.parse(layout.getChild("id_edit_box", BackgroundEditBox.class).getValue()), ResourceLocation.withDefaultNamespace(""));
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        boolean idEditBoxValid = layout.getChild("id_edit_box", RegistryVerifyEditBox.class).isValid();
        if (!idEditBoxValid) {
            return Result.err("Research Effect needs a valid dimension id");
        }

        return Result.ok(Unit.INSTANCE);
    }

}
