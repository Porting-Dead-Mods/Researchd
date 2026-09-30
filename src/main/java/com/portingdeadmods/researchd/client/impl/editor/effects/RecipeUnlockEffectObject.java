package com.portingdeadmods.researchd.client.impl.editor.effects;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.impl.editor.widgets.EditableIdListWidget;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class RecipeUnlockEffectObject implements TypedEditorObject<RecipeUnlockEffect, ResearchEffectType> {
    public static final Identifier ID = Researchd.rl("recipe_unlock");
    public static final RecipeUnlockEffectObject INSTANCE = new RecipeUnlockEffectObject();

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.RECIPE_UNLOCK.get();
    }

    @Override
    public void buildLayout(
            RememberingLinearLayout layout,
            @Nullable RecipeUnlockEffect previous,
            @UnknownNullability EditorContext context) {
        layout.addWidget(null, new StringWidget(Component.literal("By id:"), GuiUtils.getFont()));
        // RegistryVerifyEditBox idEditBox = layout.addWidget("id_edit_box", RegistryVerifyEditBox.forIds(this.getIds(),
        // context.innerWidth() - 8, 16));
        // TODO(26.1 port, recipe picker): 26.1 clients don't receive recipes, so any well-formed id is accepted for
        // now. Offer (and check against) every recipe in the game once the client can ask the server for them
        layout.addWidget(
                "id_edit_boxes",
                new EditableIdListWidget(context.innerWidth() - 8, 60, null, newVal -> this.update(layout, context)));
        // idEditBox.setResponder(newVal -> this.update(layout, context));
    }

    @Override
    public RecipeUnlockEffect create(RememberingLinearLayout layout) {
        EditableIdListWidget idEditBoxes = layout.getChild("id_edit_boxes", EditableIdListWidget.class);
        return new RecipeUnlockEffect(
                idEditBoxes.getIds().map(Identifier::parse).toArray(Identifier[]::new));
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
