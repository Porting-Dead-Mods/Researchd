package com.portingdeadmods.researchd.client.impl.editor.effects;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.impl.editor.widgets.EditableIdListWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.RegistryVerifyEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;

public class RecipeUnlockEffectObject implements TypedEditorObject<RecipeUnlockEffect, ResearchEffectType> {
    public static final ResourceLocation ID = Researchd.rl("recipe_unlock");
    public static final RecipeUnlockEffectObject INSTANCE = new RecipeUnlockEffectObject();

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.RECIPE_UNLOCK.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @Nullable RecipeUnlockEffect previous, @UnknownNullability EditorContext context) {
        layout.addWidget(null, new StringWidget(Component.literal("By id:"), GuiUtils.getFont()));
        //RegistryVerifyEditBox idEditBox = layout.addWidget("id_edit_box", RegistryVerifyEditBox.forIds(this.getIds(), context.innerWidth() - 8, 16));
        layout.addWidget("id_edit_boxes", new EditableIdListWidget(context.innerWidth() - 8, 60, this.getIds(), newVal -> this.update(layout, context)));
        //idEditBox.setResponder(newVal -> this.update(layout, context));
    }

    private Collection<ResourceLocation> getIds() {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        return manager.getRecipeIds().toList();
    }

    @Override
    public RecipeUnlockEffect create(RememberingLinearLayout layout) {
        EditableIdListWidget idEditBoxes = layout.getChild("id_edit_boxes", EditableIdListWidget.class);
        return new RecipeUnlockEffect(idEditBoxes.getIds().map(ResourceLocation::parse).toArray(ResourceLocation[]::new));
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        EditableIdListWidget idEditBoxes = layout.getChild("id_edit_boxes", EditableIdListWidget.class);
        boolean idEditBoxesEmpty = idEditBoxes.getItems().isEmpty();
        if (idEditBoxesEmpty) {
            return Result.err("At least one recipe id needs to be provided");
        }
        boolean idEditBoxesValid = idEditBoxes.getItems().stream().anyMatch(EditableIdListWidget.Element::isValid);
        if (!idEditBoxesValid) {
            return Result.err("At least one id needs to be valid");
        }

        return Result.ok(Unit.INSTANCE);
    }
}
