package com.portingdeadmods.researchd.impl.editor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.EditModeSettings;
import com.portingdeadmods.researchd.utils.PrettyPath;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

public record EditModeSettingsImpl(PrettyPath currentDatapack, PrettyPath currentResourcePack) implements EditModeSettings {
    public static final Codec<EditModeSettingsImpl> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            PrettyPath.CODEC.optionalFieldOf("current_datapack").forGetter(s -> Optional.ofNullable(s.currentDatapack())),
            PrettyPath.CODEC.optionalFieldOf("current_resource_pack").forGetter(s -> Optional.ofNullable(s.currentResourcePack()))
    ).apply(inst, (d, r) -> new EditModeSettingsImpl(d.orElse(null), r.orElse(null))));
    public static final StreamCodec<RegistryFriendlyByteBuf, EditModeSettingsImpl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(PrettyPath.STREAM_CODEC),
            t -> Optional.ofNullable(t.currentDatapack()),
            ByteBufCodecs.optional(PrettyPath.STREAM_CODEC),
            t -> Optional.ofNullable(t.currentResourcePack()),
            (dp, rp) -> new EditModeSettingsImpl(dp.orElse(null), rp.orElse(null))
    );
    public static final EditModeSettingsImpl EMPTY = new EditModeSettingsImpl(null, null);
}
