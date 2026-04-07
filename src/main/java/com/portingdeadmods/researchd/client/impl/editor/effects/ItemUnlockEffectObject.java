package com.portingdeadmods.researchd.client.impl.editor.effects;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.RegistryVerifyEditBox;
import com.portingdeadmods.researchd.impl.research.effect.ItemUnlockEffect;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemUnlockEffectObject implements TypedEditorObject<ItemUnlockEffect, ResearchEffectType> {
    public static final ResourceLocation ID = Researchd.rl("item_unlock");
    public static final ItemUnlockEffectObject INSTANCE = new ItemUnlockEffectObject();

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.ITEM_UNLOCK.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @Nullable ItemUnlockEffect previous, EditorContext context) {
        layout.getLayout().spacing(2);

        ItemSelectorWidget itemSelector = new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 25, 20, false, false);
        RegistryVerifyEditBox idEditBox = RegistryVerifyEditBox.forRegistry(BuiltInRegistries.ITEM, context.innerWidth() - 8, 16);
        idEditBox.setResponder(newVal -> {
            ResourceLocation id = ResourceLocation.parse(newVal);
            if (idEditBox.isValid(id)) {
                itemSelector.setSelected(List.of(BuiltInRegistries.ITEM.get(id).getDefaultInstance()), false);
            }
            this.update(layout, context);
        });
        itemSelector.setResponder(ingredient -> idEditBox.setValue(BuiltInRegistries.ITEM.getKey(itemSelector.getSelected().getItems()[0].getItem()).toString()));

        layout.addWidget(null, GuiUtils.stringWidget("Unlocks Item:"));
        layout.addWidget("item_selector", itemSelector, LayoutSettings::alignHorizontallyCenter);
        layout.addWidget("id_edit_box", idEditBox);
    }

    @Override
    public ItemUnlockEffect create(RememberingLinearLayout layout) {
        RegistryVerifyEditBox idEditBox = layout.getChild("id_edit_box", RegistryVerifyEditBox.class);
        return new ItemUnlockEffect(idEditBox.createId());
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        boolean idEditBoxValid = layout.getChild("id_edit_box", RegistryVerifyEditBox.class).isValid();
        if (!idEditBoxValid) {
            return Result.err("Research Effect needs a valid item id");
        }

        return Result.ok(Unit.INSTANCE);
    }

}
