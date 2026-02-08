package com.portingdeadmods.researchd.api.editor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.utils.ResearchdCodecUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.StringRepresentable;

import java.nio.file.Path;

public record PackLocation(Path rootPath, String namespace, PackType type) {
    public static final Codec<PackLocation> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResearchdCodecUtils.PATH_CODEC.fieldOf("root_path").forGetter(PackLocation::rootPath),
            Codec.STRING.fieldOf("namespace").forGetter(PackLocation::namespace),
            StringRepresentable.fromEnum(PackType::values).fieldOf("type").forGetter(PackLocation::type)
    ).apply(inst, PackLocation::new));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, PackLocation> STREAM_CODEC = StreamCodec.composite(
            ResearchdCodecUtils.PATH_STREAM_CODEC,
            PackLocation::rootPath,
            ByteBufCodecs.STRING_UTF8,
            PackLocation::namespace,
            CodecUtils.enumStreamCodec(PackType.class),
            PackLocation::type,
            PackLocation::new
    );

    public String rootPackName() {
        return this.rootPath.getFileName().toString();
    }

}
