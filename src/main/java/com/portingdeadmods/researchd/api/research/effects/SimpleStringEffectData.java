package com.portingdeadmods.researchd.api.research.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.function.Function;

/**
 * Subclasses only need to supply key(ResearchEffect), type() and build their codecs
 */
public abstract class SimpleStringEffectData<E extends ResearchEffect> implements ResearchEffectData<E> {
    protected final UniqueArray<String> values;

    protected SimpleStringEffectData(UniqueArray<String> values) {
        this.values = values;
    }

    protected SimpleStringEffectData() {
        this(new UniqueArray<>());
    }

    protected abstract String key(E effect);

    @Override
    public void add(E effect, Level level) {
        this.values.add(key(effect));
    }

    @Override
    public void remove(E effect, Level level) {
        this.values.remove(key(effect));
    }

    public boolean contains(String value) {
        return this.values.contains(value);
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public UniqueArray<String> values() {
        return this.values;
    }

    @Override
    public UniqueArray<String> getAll() {
        return this.values;
    }

    public static <T extends SimpleStringEffectData<?>> MapCodec<T> codec(String fieldName, Function<UniqueArray<String>, T> factory) {
        return UniqueArray.CODEC(Codec.STRING)
                .xmap(factory, SimpleStringEffectData::values)
                .fieldOf(fieldName);
    }

    public static <T extends SimpleStringEffectData<?>> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(Function<UniqueArray<String>, T> factory) {
        return StreamCodec.composite(
                UniqueArray.STREAM_CODEC(ByteBufCodecs.STRING_UTF8),
                SimpleStringEffectData::values,
                factory
        );
    }
}
