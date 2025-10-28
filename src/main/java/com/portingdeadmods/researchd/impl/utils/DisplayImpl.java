package com.portingdeadmods.researchd.impl.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record DisplayImpl(Optional<Component> name, Optional<Component> desc) {
    public static final DisplayImpl EMPTY = new DisplayImpl(Optional.empty(), Optional.empty());
    public static final Codec<DisplayImpl> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(DisplayImpl::name),
            ComponentSerialization.CODEC.optionalFieldOf("desc").forGetter(DisplayImpl::desc)
    ).apply(inst, DisplayImpl::new));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, DisplayImpl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC),
            DisplayImpl::name,
            ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC),
            DisplayImpl::desc,
            DisplayImpl::new
    );
}
