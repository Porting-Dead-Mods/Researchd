package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A 1:1 to the Factorio research. All the listed packs start getting consumed at once.
 * For researching to start, all the required packs must be present in the machine's inventory.'
 *
 * @param packs All the packs that get used in the research
 * @param count The amount of packs that will be used
 * @param duration The duration in ticks for a *base speed* machine to use 1 packs o' packs.
 */
public record ConsumePackResearchMethod(List<ResourceKey<SimpleResearchPack>> packs, int count, int duration) implements ResearchMethod {
    public static final ResourceLocation ID = Researchd.rl("consume_pack");

    @Override
    public boolean canResearch(Player player, ResourceKey<Research> research) {
        return true; // Not player based
    }

    @Override
    public void onResearchStart(Player player, ResourceKey<Research> research) {
        return; // Not player based
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public List<ItemStack> asStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ResourceKey<SimpleResearchPack> pack : this.packs) {
            ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();
            stack.set(ResearchdDataComponents.RESEARCH_PACK, new ResearchPackComponent(Optional.of(pack)));
            stacks.add(stack);
        }
        return stacks;
    }

    @Override
    public Serializer getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public ResearchMethodProgress getDefaultProgress() {
        return ResearchMethodProgress.empty(this, this.count);
    }

    public static final class Serializer implements ResearchMethodSerializer<ConsumePackResearchMethod> {
        public static final Serializer INSTANCE = new ConsumePackResearchMethod.Serializer();
        public static final MapCodec<ConsumePackResearchMethod> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(SimpleResearchPack.RESOURCE_KEY_CODEC).fieldOf("packs").forGetter(ConsumePackResearchMethod::packs),
                Codec.INT.fieldOf("count").forGetter(ConsumePackResearchMethod::count),
                Codec.INT.fieldOf("duration").forGetter(ConsumePackResearchMethod::duration)
        ).apply(instance, ConsumePackResearchMethod::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ConsumePackResearchMethod> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_PACK_KEY).apply(ByteBufCodecs.list()),
                ConsumePackResearchMethod::packs,
                ByteBufCodecs.INT,
                ConsumePackResearchMethod::count,
                ByteBufCodecs.INT,
                ConsumePackResearchMethod::duration,
                ConsumePackResearchMethod::new
        );

        private Serializer() {
        }

        @Override
        public @NotNull MapCodec<ConsumePackResearchMethod> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ConsumePackResearchMethod> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
