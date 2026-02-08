package com.portingdeadmods.researchd.impl.editor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.editor.EditModeSettings;
import com.portingdeadmods.researchd.api.editor.PackLocation;
import com.portingdeadmods.researchd.resources.editor.EditorDatapackWriter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.Optional;

public final class EditModeSettingsImpl implements EditModeSettings {
    public static final Codec<EditModeSettingsImpl> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            PackLocation.CODEC.optionalFieldOf("current_datapack").forGetter(s -> Optional.ofNullable(s.currentDatapack())),
            PackLocation.CODEC.optionalFieldOf("current_resource_pack").forGetter(s -> Optional.ofNullable(s.currentResourcePack()))
    ).apply(inst, (d, r) -> new EditModeSettingsImpl(d.orElse(null), r.orElse(null))));
    public static final StreamCodec<RegistryFriendlyByteBuf, EditModeSettingsImpl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(PackLocation.STREAM_CODEC.cast()),
            t -> Optional.ofNullable(t.currentDatapack()),
            ByteBufCodecs.optional(PackLocation.STREAM_CODEC.cast()),
            t -> Optional.ofNullable(t.currentResourcePack()),
            (dp, rp) -> new EditModeSettingsImpl(dp.orElse(null), rp.orElse(null))
    );
    public static final EditModeSettingsImpl EMPTY = new EditModeSettingsImpl(null, null);
    private final PackLocation currentDatapack;
    private final PackLocation currentResourcePack;
    private final EditorDatapackWriter writer;

    public EditModeSettingsImpl(PackLocation currentDatapack, PackLocation currentResourcePack) {
        this.currentDatapack = currentDatapack;
        this.currentResourcePack = currentResourcePack;
        this.writer = new EditorDatapackWriter();
    }

    @Override
    public PackLocation currentDatapack() {
        return currentDatapack;
    }

    @Override
    public PackLocation currentResourcePack() {
        return currentResourcePack;
    }

    public EditorDatapackWriter getWriter() {
        return writer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EditModeSettingsImpl) obj;
        return Objects.equals(this.currentDatapack, that.currentDatapack) &&
                Objects.equals(this.currentResourcePack, that.currentResourcePack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentDatapack, currentResourcePack);
    }

    @Override
    public String toString() {
        return "EditModeSettingsImpl[" +
                "currentDatapack=" + currentDatapack + ", " +
                "currentResourcePack=" + currentResourcePack + ']';
    }

}
