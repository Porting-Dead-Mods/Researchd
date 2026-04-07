package com.portingdeadmods.researchd.client.impl.editor.effects;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.RegistryVerifyEditBox;
import com.portingdeadmods.researchd.client.screens.editor.widgets.SuggestionRegistryVerifyEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.impl.research.effect.ValueEffectModifierEffect;
import com.portingdeadmods.researchd.utils.GuiUtils;
import com.portingdeadmods.researchd.utils.TextUtils;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class ValueEffectModifierObject implements TypedEditorObject<ValueEffectModifierEffect, ResearchEffectType> {
    private final ResearchEffectType effectType;
    private final BiFunction<ValueEffect, Float, ValueEffectModifierEffect> effectFactory;

    public ValueEffectModifierObject(ResearchEffectType effectType, BiFunction<ValueEffect, Float, ValueEffectModifierEffect> effectFactory) {
        this.effectType = effectType;
        this.effectFactory = effectFactory;
    }

    @Override
    public ResearchEffectType type() {
        return effectType;
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @Nullable ValueEffectModifierEffect previous, EditorContext context) {
        layout.getLayout().spacing(2);
        layout.addWidget(null, GuiUtils.stringWidget("Value Effect:"));
        SuggestionRegistryVerifyEditBox effectEditBox = layout.addWidget("effect", new SuggestionRegistryVerifyEditBox(GuiUtils.getFont(), ResearchdRegistries.VALUE_EFFECT, context.innerWidth() - 8, 16));
        effectEditBox.setResponder(newVal -> {
            this.update(layout, context);
        });
        layout.addWidget(null, GuiUtils.stringWidget("Amount:"));
        BackgroundEditBox valueEditBox = layout.addWidget("value", new BackgroundEditBox(GuiUtils.getFont(), context.innerWidth() - 8, 16, "1.0"));
        //valueEditBox.setResponder(newVal -> this.update(layout, context));
        valueEditBox.setFilter(TextUtils::isValidFloat);
    }

    @Override
    public ValueEffectModifierEffect create(RememberingLinearLayout layout) {
        RegistryVerifyEditBox effectEditBox = layout.getChild("effect", RegistryVerifyEditBox.class);
        BackgroundEditBox valueEditBox = layout.getChild("value", BackgroundEditBox.class);
        return this.effectFactory.apply(effectEditBox.getObjectById(), Float.parseFloat(valueEditBox.getValue()));
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        RegistryVerifyEditBox effectEditBox = layout.getChild("effect", RegistryVerifyEditBox.class);
        if (!effectEditBox.isValid()) {
            return Result.err("Research effect needs a valid value effect");
        }
        return Result.ok(Unit.INSTANCE);
    }
}
