package com.portingdeadmods.researchd.api.research;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface EditModeSettings {
    @Nullable Path currentDatapack();

    @Nullable Path currentResourcePack();
}
