package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface ResearchMethod {
    Codec<ResearchMethod> CODEC =
            ResearchdRegistries.RESEARCH_METHOD_SERIALIZER.byNameCodec().dispatch(ResearchMethod::getSerializer, ResearchMethodSerializer::codec);

    boolean canResearch(Player player, ResourceKey<Research> research);

    void onResearchStart(Player player, ResourceKey<Research> research);

    ResourceLocation id();

    default Component getTranslation() {
        ResourceLocation id = id();
        return Component.translatable("research_method." + id.getNamespace() + "." + id.getPath());
    }

    ClientResearchMethod<?> getClientMethod();

    ResearchMethodSerializer<?> getSerializer();
}
