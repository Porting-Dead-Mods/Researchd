package com.portingdeadmods.researchd.compat.immersiveengineering;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.effects.SimpleStringEffectData;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class UnlockIEMultiblockEffectData extends SimpleStringEffectData<UnlockIEMultiblockEffect> {
    public static final MapCodec<UnlockIEMultiblockEffectData> CODEC =
            SimpleStringEffectData.codec("blocked_multiblocks", UnlockIEMultiblockEffectData::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockIEMultiblockEffectData> STREAM_CODEC =
            SimpleStringEffectData.streamCodec(UnlockIEMultiblockEffectData::new);

    public static final ResearchEffectDataType<UnlockIEMultiblockEffectData> TYPE =
            ResearchEffectDataType.simple(UnlockIEMultiblockEffectData::new, CODEC, STREAM_CODEC);

    public UnlockIEMultiblockEffectData(UniqueArray<String> values) {
        super(values);
    }

    public UnlockIEMultiblockEffectData() {
        super();
    }

    @Override
    protected String key(UnlockIEMultiblockEffect effect) {
        return effect.multiblock().toString();
    }

    public boolean isBlocked(ResourceLocation multiblock) {
        return contains(multiblock.toString());
    }

    @Override
    public ResearchEffectDataType<UnlockIEMultiblockEffectData> type() {
        return TYPE;
    }
}
