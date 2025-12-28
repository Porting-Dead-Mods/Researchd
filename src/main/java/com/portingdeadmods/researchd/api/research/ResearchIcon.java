package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchIconSerializer;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ResearchIcon} is a utility interface containing
 * the id as well as the object that renders for the icon.
 * <p>
 * The actual rendering of the icon is handled on the client
 * side through a {@link com.portingdeadmods.researchd.api.client.ClientResearchIcon}
 * class
 * <p>
 * The default Research Icon implementation is {@link ItemResearchIcon}
 * which is used in {@link com.portingdeadmods.researchd.impl.research.SimpleResearch}
 */
public interface ResearchIcon {
    Codec<ResearchIcon> CODEC = ResearchdRegistries.RESEARCH_ICON_SERIALIZER.byNameCodec().dispatch(ResearchIcon::getSerializer, ResearchIconSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, ResearchIcon> STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC).cast();

    /**
     * @return The id of this type of Research Icon.
     * Usually this is just a constant in the Research
     * Icon class
     */
    ResourceLocation id();

    ResearchIconSerializer<? extends ResearchIcon> getSerializer();
}
