package com.portingdeadmods.researchd.impl.capabilities;

import com.portingdeadmods.researchd.api.capabilties.Research;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public record SimpleResearch(Optional<ResourceKey<Research>> parent, boolean requiresParent) implements Research {

}
