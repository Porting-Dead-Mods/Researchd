package com.portingdeadmods.researchd.client.impl.editor.effects;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.impl.research.effect.CommandResearchEffect;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

public class CommandEffectObject implements TypedEditorObject<CommandResearchEffect, ResearchEffectType> {
    public static final ResourceLocation ID = CommandResearchEffect.ID;
    public static final CommandEffectObject INSTANCE = new CommandEffectObject();

    private static final int MAX_COMMAND_LENGTH = 32500;

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.COMMAND.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @Nullable CommandResearchEffect previous, EditorContext context) {
        layout.addWidget(null, GuiUtils.stringWidget("Command on unlock:"));
        addCommandEditBox(layout, context, "on_unlock_edit_box", previous != null ? previous.onUnlockCommand() : "");
        layout.addWidget(null, GuiUtils.stringWidget("Command on lock:"));
        addCommandEditBox(layout, context, "on_lock_edit_box", previous != null ? previous.onLockCommand() : "");
    }

    private void addCommandEditBox(RememberingLinearLayout layout, EditorContext context, String name, String value) {
        BackgroundEditBox editBox = layout.addWidget(name, new BackgroundEditBox(GuiUtils.getFont(), context.innerWidth() - 8, 16));
        editBox.setMaxLength(MAX_COMMAND_LENGTH);
        editBox.setValue(value);
        editBox.setResponder(newVal -> this.update(layout, context));
    }

    @Override
    public CommandResearchEffect create(RememberingLinearLayout layout) {
        return new CommandResearchEffect(
                layout.getChild("on_unlock_edit_box", BackgroundEditBox.class).getValue(),
                layout.getChild("on_lock_edit_box", BackgroundEditBox.class).getValue()
        );
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        boolean unlockBlank = layout.getChild("on_unlock_edit_box", BackgroundEditBox.class).getValue().isBlank();
        boolean lockBlank = layout.getChild("on_lock_edit_box", BackgroundEditBox.class).getValue().isBlank();
        if (unlockBlank && lockBlank) {
            return Result.err("At least one command needs to be provided");
        }

        return Result.ok(Unit.INSTANCE);
    }
}
