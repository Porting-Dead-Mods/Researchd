package com.portingdeadmods.researchd.api.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SavedDataWrapper<T> extends SavedData {
    private final SavedDataHolder<T> registeredSavedDataHolder;
    private T data;

    public SavedDataWrapper(SavedDataHolder<T> registeredSavedDataHolder, T data) {
        this.registeredSavedDataHolder = registeredSavedDataHolder;
        this.data = data;
    }

    public SavedDataWrapper(SavedDataHolder<T> registeredSavedDataHolder) {
        this(registeredSavedDataHolder, registeredSavedDataHolder.value().defaultValueSupplier().get());
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        DataResult<Tag> result = this.registeredSavedDataHolder.value().codec().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.data);
        Optional<Tag> _tag = result.resultOrPartial(err -> Researchd.LOGGER.error("Encountered error encoding {} saved data: {}", getName(this.registeredSavedDataHolder), err));
        _tag.ifPresent(tag -> compoundTag.put(getName(this.registeredSavedDataHolder) + "_data", tag));
        return compoundTag;
    }

    private static <T> T load(SavedDataHolder<T> registeredSavedDataHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        DataResult<Pair<T, Tag>> result = registeredSavedDataHolder.value().codec().decode(provider.createSerializationContext(NbtOps.INSTANCE), compoundTag.get(getName(registeredSavedDataHolder) + "_data"));
        return result.resultOrPartial(err -> Researchd.LOGGER.error("Encountered error decoding {} saved data: {}", getName(registeredSavedDataHolder), err)).map(Pair::getFirst).orElse(null);
    }

    private static <T> String getName(SavedDataHolder<T> registeredSavedDataHolder) {
        return registeredSavedDataHolder.key().toString();
    }

    private static <T> SavedData.Factory<SavedDataWrapper<T>> factory(SavedDataHolder<T> registeredSavedDataHolder) {
        return new SavedData.Factory<>(
                () -> new SavedDataWrapper<>(registeredSavedDataHolder),
                (tag, provider) -> new SavedDataWrapper<>(registeredSavedDataHolder, load(registeredSavedDataHolder, tag, provider))
        );
    }

    protected static <T> T getData(SavedDataHolder<T> registeredSavedDataHolder, ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(factory(registeredSavedDataHolder), getName(registeredSavedDataHolder)).data;
    }

    protected static <T> void setData(SavedDataHolder<T> registeredSavedDataHolder, ServerLevel serverLevel, T data) {
        SavedDataWrapper<T> dataWrapper = serverLevel.getDataStorage().computeIfAbsent(factory(registeredSavedDataHolder), getName(registeredSavedDataHolder));
        dataWrapper.data = data;
        dataWrapper.setDirty();
    }

}
