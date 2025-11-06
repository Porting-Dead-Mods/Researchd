package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum ResearchInteractionType implements StringRepresentable {
    /* Default Research Completion */
    DEFAULT("default"),
    /* Research Editing mode */
    EDIT("edit");

    public static final Codec<ResearchInteractionType> CODEC = StringRepresentable.fromEnum(ResearchInteractionType::values);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchInteractionType> STREAM_CODEC = CodecUtils.enumStreamCodec(ResearchInteractionType.class);

    private final String name;

    ResearchInteractionType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
