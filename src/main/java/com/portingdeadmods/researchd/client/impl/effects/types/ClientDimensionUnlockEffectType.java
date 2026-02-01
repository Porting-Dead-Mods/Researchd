package com.portingdeadmods.researchd.client.impl.effects.types;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.ClientResearchEffectType;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.RegistryVerifyEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;

public class ClientDimensionUnlockEffectType implements ClientResearchEffectType {
    public static final ResourceLocation ID = Researchd.rl("dimension_unlock");
    public static final ClientDimensionUnlockEffectType INSTANCE = new ClientDimensionUnlockEffectType();

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.DIMENSION_UNLOCK.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, Context context) {
        Optional<Registry<DimensionType>> registry = Minecraft.getInstance().level.registryAccess().registry(Registries.DIMENSION_TYPE);
        registry.ifPresent(dimensionTypes -> {
            layout.addWidget("id_edit_box", new RegistryVerifyEditBox(PopupWidget.getFont(), dimensionTypes, context.innerWidth() - 8, 16, CommonComponents.EMPTY));
        });
    }

    @Override
    public ResearchEffect createResearchEffect(RememberingLinearLayout layout) {
        return new DimensionUnlockEffect(ResourceLocation.parse(layout.getChild("id_edit_box", BackgroundEditBox.class).getValue()), ResourceLocation.withDefaultNamespace(""));
    }
}
