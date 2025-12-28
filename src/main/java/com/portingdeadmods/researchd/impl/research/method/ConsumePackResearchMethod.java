package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.api.team.ValueEffectsHolder;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import com.portingdeadmods.researchd.registries.ResearchdValueEffects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A 1:1 to the Factorio research. All the listed packs start getting consumed at once.
 * For researching to start, all the required packs must be present in the machine's inventory.'
 *
 * @param packs All the packs that get used in the research
 * @param count The amount of packs that will be used
 * @param duration The duration in ticks for a *base speed* machine to use 1 packs o' packs.
 */
public record ConsumePackResearchMethod(List<ResourceKey<ResearchPack>> packs, int count, int duration) implements ResearchMethod {
    public static final ResourceLocation ID = Researchd.rl("consume_pack");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CONSUME_PACK.get();
    }

    @Override
    public void checkProgress(Level level, ResourceKey<Research> research, ResearchProgress.Task task, MethodContext context) {
        if (context instanceof SimpleMethodContext(ValueEffectsHolder team, ResearchLabControllerBE blockEntity) && blockEntity != null) {
            List<ResourceKey<ResearchPack>> packs = this.packs();
            blockEntity.currentResearchDuration = this.duration();

            if (!blockEntity.containsNecessaryPacks(packs)) return;
            blockEntity.decreaseNecessaryPackCount(packs);

            for (ResourceKey<ResearchPack> pack : packs) {
                blockEntity.researchPackUsage.put(pack, Math.max(blockEntity.researchPackUsage.get(pack) - ((1f / blockEntity.currentResearchDuration) / team.getEffectValue(ResearchdValueEffects.RESEARCH_LAB_PRODUCTIVITY)), 0f));
            }
            task.addProgress(1f / blockEntity.currentResearchDuration);
        }
    }

    @Override
    public boolean shouldCheckProgress() {
        return false;
    }

    @Override
    public float getMaxProgress() {
        return this.count;
    }

    @Override
    public ResearchProgress createProgress() {
        return ResearchProgress.single(this);
    }

    public List<ItemStack> asStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ResourceKey<ResearchPack> pack : this.packs) {
            ItemStack stack = ResearchPackImpl.asStack(pack);
            stacks.add(stack);
        }
        return stacks;
    }

    @Override
    public Serializer getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchMethodSerializer<ConsumePackResearchMethod> {
        public static final Serializer INSTANCE = new ConsumePackResearchMethod.Serializer();
        public static final MapCodec<ConsumePackResearchMethod> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(ResearchPackImpl.RESOURCE_KEY_CODEC).fieldOf("packs").forGetter(ConsumePackResearchMethod::packs),
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
