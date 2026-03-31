package com.portingdeadmods.researchd.api.client.editor;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;

public interface EditorObject<O> {
    void buildLayout(RememberingLinearLayout layout, EditorContext context);

    O create(RememberingLinearLayout layout);

    Result<Unit, Exception> valid(RememberingLinearLayout layout);

    default void update(RememberingLinearLayout layout, EditorContext context) {
        if (this.valid(layout) instanceof Result.Err<Unit, Exception>(Exception error)) {
            context.setCreateButtonActive(false);
            context.setCreateButtonTooltip(Tooltip.create(Component.literal(error.getMessage())));
        } else {
            context.setCreateButtonActive(true);
            context.setCreateButtonTooltip(null);
        }
    }
}
