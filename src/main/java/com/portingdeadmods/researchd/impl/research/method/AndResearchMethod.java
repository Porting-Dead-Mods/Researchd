package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.impl.research.ResearchCompletionProgress;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record AndResearchMethod(List<ResearchMethod> methods) implements ResearchMethod {
    private static final MapCodec<AndResearchMethod> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResearchMethod.CODEC.listOf().fieldOf("methods").forGetter(AndResearchMethod::methods)
    ).apply(inst, AndResearchMethod::new));
    public static final ResearchMethodSerializer<AndResearchMethod> SERIALIZER = ResearchMethodSerializer.simple(CODEC, null);

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
        return Researchd.rl("and");
    }

    @Override
    public ClientResearchMethod<?> getClientMethod() {
        return null;
    }

    @Override
    public ResearchMethodSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public ResearchCompletionProgress getDefaultProgress() {
        return new ResearchCompletionProgress(methods.size());
    }
}
