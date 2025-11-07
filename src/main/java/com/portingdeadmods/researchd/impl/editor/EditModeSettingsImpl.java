package com.portingdeadmods.researchd.impl.editor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.EditModeSettings;
import com.portingdeadmods.researchd.utils.ResearchdCodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

public record EditModeSettingsImpl(Path currentDatapack, Path currentResourcePack) implements EditModeSettings {
    public static final Codec<EditModeSettingsImpl> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResearchdCodecUtils.PATH_CODEC.optionalFieldOf("current_datapack", null).forGetter(EditModeSettingsImpl::currentDatapack),
            ResearchdCodecUtils.PATH_CODEC.optionalFieldOf("current_resource_pack", null).forGetter(EditModeSettingsImpl::currentResourcePack)
    ).apply(inst, EditModeSettingsImpl::new));
    public static final StreamCodec<ByteBuf, EditModeSettingsImpl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResearchdCodecUtils.PATH_STREAM_CODEC),
            t -> Optional.ofNullable(t.currentDatapack()),
            ByteBufCodecs.optional(ResearchdCodecUtils.PATH_STREAM_CODEC),
            t -> Optional.ofNullable(t.currentResourcePack()),
            (dp, rp) -> new EditModeSettingsImpl(dp.orElse(null), rp.orElse(null))
    );
    public static final EditModeSettingsImpl EMPTY = new EditModeSettingsImpl(null, null);
}
