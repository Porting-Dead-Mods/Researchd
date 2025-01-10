package com.portingdeadmods.researchd.api.capabilties;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

/**
 * Represents a single research
 */
public interface Research {
    Codec<Research> CODEC = /* TODO */ null;

    Optional<ResourceKey<Research>> parent();

    boolean requiresParent();
}
