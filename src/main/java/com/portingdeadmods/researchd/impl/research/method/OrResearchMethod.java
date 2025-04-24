package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record OrResearchMethod(List<ResearchMethod> methods) implements ResearchMethod {
    private static final MapCodec<OrResearchMethod> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResearchMethod.CODEC.listOf().fieldOf("methods").forGetter(OrResearchMethod::methods)
    ).apply(inst, OrResearchMethod::new));
    public static final ResearchMethodSerializer<OrResearchMethod> SERIALIZER = ResearchMethodSerializer.simple(CODEC, null);

    // TODO: In the far future we might want to implement a way to select the preferred research
    @Override
    public boolean canResearch(Player player, ResourceKey<Research> research) {
        for (ResearchMethod method : this.methods) {
            if (method.canResearch(player, research)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResearchStart(Player player, ResourceKey<Research> research) {
        for (ResearchMethod method : this.methods) {
            if (method.canResearch(player, research)) {
                method.onResearchStart(player, research);
            }
        }
    }

    @Override
    public ResourceLocation id() {
        return Researchd.rl("or");
    }

    @Override
    public ClientResearchMethod<?> getClientMethod() {
        return null;
    }

    @Override
    public ResearchMethodSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
