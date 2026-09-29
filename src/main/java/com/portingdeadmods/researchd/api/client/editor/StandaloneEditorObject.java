package com.portingdeadmods.researchd.api.client.editor;

import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import net.minecraft.resources.Identifier;

/**
 * An editor object that will also create an
 * id, for saving as a standalone data file
 */
public interface StandaloneEditorObject<O> extends EditorObject<O> {
    Identifier createId(RememberingLinearLayout layout, String namespace);
}
