package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.researchd.utils.PrettyPath;
import org.jetbrains.annotations.Nullable;

public interface EditModeSettings {
    @Nullable PrettyPath currentDatapack();

    @Nullable PrettyPath currentResourcePack();

    default boolean isConfigured() {
        return this.currentDatapack() != null && this.currentResourcePack() != null;
    }

}
