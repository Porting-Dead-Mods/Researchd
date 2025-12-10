package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Most basic research, providing functionality and data for both displaying and research logic
 * <p>
 * The default research implementation is {@link com.portingdeadmods.researchd.impl.research.SimpleResearch}
 * which implements the methods listed here and should be suitable for most use-cases.
 */
public interface Research {
    Codec<Research> CODEC = ResearchdRegistries.RESEARCH_SERIALIZER.byNameCodec().dispatch(Research::getSerializer, ResearchSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, Research> STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC).cast();
    Codec<ResourceKey<Research>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_KEY);
    StreamCodec<ByteBuf, ResourceKey<Research>> RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_KEY);

    /**
     * @return The research icon of this research
     */
    ResearchIcon researchIcon();

    /**
     * @return The research method that is required
     * for this research to be completed
     */
    ResearchMethod researchMethod();

    /**
     * @return The research effect that happens after
     * the research is completed
     */
    ResearchEffect researchEffect();

    /**
     * @return A {@link List} of {@link ResourceKey}
     * pointing to the parent researchPacks of this research
     */
    List<ResourceKey<Research>> parents();

    /**
     * @return whether the parent researchPacks need to be completed to start this research
     */
    boolean requiresParent();

    /**
     * @return The {@link ResourceLocation} id of the research page this research belongs to.
     * Only root researches (without parents) should explicitly define this.
     * Child researches inherit the page from their parents.
     * Defaults to {@link ResearchPage#DEFAULT_PAGE_ID}
     */
    default ResourceLocation researchPage() {
        return ResearchPage.DEFAULT_PAGE_ID;
    }

    /**
     * @return serializer providing typed codecs for the Research
     */
    ResearchSerializer<?> getSerializer();

    static Component getLangName(ResourceKey<Research> key) {
        String registryPath = ResearchdRegistries.RESEARCH_KEY.location().getPath();
        String keyNamespace = key.location().getNamespace();
        String keyPath = key.location().getPath();
        return Component.translatable(String.format("%s.%s.%s_name", registryPath, keyNamespace, keyPath));
    }

    static Component getLangDesc(ResourceKey<Research> key) {
        String registryPath = ResearchdRegistries.RESEARCH_KEY.location().getPath();
        String keyNamespace = key.location().getNamespace();
        String keyPath = key.location().getPath();
        return Component.translatable(String.format("%s.%s.%s_desc", registryPath, keyNamespace, keyPath));
    }

}
