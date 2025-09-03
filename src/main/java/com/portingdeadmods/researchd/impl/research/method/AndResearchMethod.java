package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodList;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record AndResearchMethod(List<ResearchMethod> methods) implements ResearchMethodList {
    private static final MapCodec<AndResearchMethod> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResearchMethod.CODEC.listOf().fieldOf("methods").forGetter(AndResearchMethod::methods)
    ).apply(inst, AndResearchMethod::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AndResearchMethod> STREAM_CODEC = StreamCodec.composite(
            ResearchMethod.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AndResearchMethod::methods,
            AndResearchMethod::new
    );

    public static final ResearchMethodSerializer<AndResearchMethod> SERIALIZER = ResearchMethodSerializer.simple(CODEC, STREAM_CODEC);
    public static final ResourceLocation ID = Researchd.rl("and");

    @Override
    public boolean canResearch(Player player, ResourceKey<Research> research) {
        for (ResearchMethod method : this.methods) {
            if (!method.canResearch(player, research)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResearchStart(Player player, ResourceKey<Research> research) {
        for (ResearchMethod method : this.methods) {
            method.onResearchStart(player, research);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchMethodSerializer<AndResearchMethod> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public ResearchMethodProgress getDefaultProgress() {
        return ResearchMethodProgress.empty(this, methods.size());
    }
}
