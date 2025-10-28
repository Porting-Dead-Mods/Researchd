package com.portingdeadmods.researchd.api.research;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public interface RegistryDisplay<T> {
    Component getDisplayName(ResourceKey<T> key);

    default Component getDisplayNameUnsafe(ResourceKey<?> key) {
        return this.getDisplayName((ResourceKey<T>) key);
    }

    Component getDisplayDescription(ResourceKey<T> key);

    default Component getDisplayDescriptionUnsafe(ResourceKey<?> key) {
        return this.getDisplayDescription((ResourceKey<T>) key);
    }

}
