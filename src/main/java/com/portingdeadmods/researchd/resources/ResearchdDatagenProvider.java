package com.portingdeadmods.researchd.resources;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.utils.researches.ReloadableRegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public interface ResearchdDatagenProvider<T> {
    String modid();

    ResourceKey<Registry<T>> registry();

    Codec<T> codec();

    Map<ResourceKey<T>, T> contents();

    void build();

    /**
     * Write the contents of this provider to the specified directory
     *
     * @param dataDir the directory the data goes into
     */
    default Result<Unit, Exception> write(Path dataDir) {
        for (Map.Entry<ResourceKey<T>, T> entry : this.contents().entrySet()) {
            ResourceKey<T> key = entry.getKey();
            T value = entry.getValue();
            DataResult<JsonElement> result;
            try {
                result = this.codec().encodeStart(JsonOps.INSTANCE, value);
            } catch (Exception e) {
                return Result.err("Failed to encode research: " + e);
            }
            if (result.isSuccess()) {
                JsonElement json = result.getOrThrow();
                String jsonString = ReloadableRegistryManager.GSON.toJson(json);
                try {
                    Files.writeString(dataDir.resolve(key.location().getPath() + ".json"), jsonString);
                } catch (IOException e) {
                    return Result.err("Failed to write contents to file: " + e.getMessage());
                }
            }
        }
        return Result.ok(Unit.INSTANCE);
    }
}
