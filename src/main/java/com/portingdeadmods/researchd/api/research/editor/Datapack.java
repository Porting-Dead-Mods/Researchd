package com.portingdeadmods.researchd.api.research.editor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.utils.PrettyPath;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Datapack(PrettyPath path, String namespace) {
    public static final Codec<Datapack> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            PrettyPath.CODEC.fieldOf("path").forGetter(Datapack::path),
            Codec.STRING.fieldOf("namespace").forGetter(Datapack::namespace)
    ).apply(inst, Datapack::new));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, Datapack> STREAM_CODEC = StreamCodec.composite(
            PrettyPath.STREAM_CODEC,
            Datapack::path,
            ByteBufCodecs.STRING_UTF8,
            Datapack::namespace,
            Datapack::new
    );
}
