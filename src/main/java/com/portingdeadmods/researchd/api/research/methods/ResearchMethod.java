package com.portingdeadmods.researchd.api.research.methods;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ResearchMethod {
    Codec<ResearchMethod> CODEC =
            ResearchdRegistries.RESEARCH_METHOD_SERIALIZER.byNameCodec().dispatch(ResearchMethod::getSerializer, ResearchMethodSerializer::codec);

    StreamCodec<RegistryFriendlyByteBuf, ResearchMethod> STREAM_CODEC =
            ResearchMethodSerializer.STREAM_CODEC.dispatch(ResearchMethod::getSerializer, ResearchMethodSerializer::streamCodec);

    ResourceLocation id();

    void checkProgress(Level level, ResourceKey<Research> research, ResearchProgress.Task task, MethodContext context);

    ResearchProgress createProgress();

    float getMaxProgress();

    default boolean shouldCheckProgress() {
        return true;
    }

    default Component getTranslation() {
        ResourceLocation id = id();
        return Component.translatable("research_method." + id.getNamespace() + "." + id.getPath());
    }

    ResearchMethodSerializer<?> getSerializer();

    interface MethodContext {
        ResearchTeam team();
    }

    record SimpleMethodContext(ResearchTeam team, @Nullable ResearchLabControllerBE blockEntity) implements MethodContext {
    }
}
