package com.portingdeadmods.researchd.api.pdl.data;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PDLClientSavedData {
    public static final Map<ResourceLocation, Object> CLIENT_SAVED_DATA_CACHE = new ConcurrentHashMap<>();
}
