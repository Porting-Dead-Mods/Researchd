package com.portingdeadmods.researchd.api.editor;

import com.portingdeadmods.researchd.utils.PrettyPath;
import org.jetbrains.annotations.Nullable;

public interface EditModeSettings {
    @Nullable PackLocation currentDatapack();

    @Nullable PackLocation currentResourcePack();

    default boolean isConfigured() {
        return this.currentDatapack() != null && this.currentResourcePack() != null;
    }

}
