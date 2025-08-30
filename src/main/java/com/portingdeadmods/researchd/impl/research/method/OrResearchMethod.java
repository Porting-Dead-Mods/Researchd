package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodList;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.client.impl.methods.ClientOrResearchMethod;
import com.portingdeadmods.researchd.data.helper.ResearchCompletionProgress;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record OrResearchMethod(List<ResearchMethod> methods) implements ResearchMethodList {
    private static final MapCodec<OrResearchMethod> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResearchMethod.CODEC.listOf().fieldOf("methods").forGetter(OrResearchMethod::methods)
    ).apply(inst, OrResearchMethod::new));
    public static final ResearchMethodSerializer<OrResearchMethod> SERIALIZER = ResearchMethodSerializer.simple(CODEC, null);
    public static final ResourceLocation ID = Researchd.rl("or");

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
        return ID;
    }

    @Override
    public ResearchMethodSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public ResearchCompletionProgress getDefaultProgress() {
        return ResearchCompletionProgress.one();
    }
}
