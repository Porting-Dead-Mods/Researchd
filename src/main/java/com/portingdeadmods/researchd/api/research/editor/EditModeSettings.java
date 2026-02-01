package com.portingdeadmods.researchd.api.research.editor;

import com.portingdeadmods.researchd.utils.PrettyPath;
import org.jetbrains.annotations.Nullable;

public interface EditModeSettings {
    @Nullable Datapack currentDatapack();

    @Nullable PrettyPath currentResourcePack();

    default boolean isConfigured() {
        return this.currentDatapack() != null && this.currentResourcePack() != null;
    }

}
