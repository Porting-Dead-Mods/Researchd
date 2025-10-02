package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodProgress;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.api.team.TeamMember;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record ConsumeItemResearchMethod(Ingredient toConsume, int count) implements ResearchMethod {
    public static final ConsumeItemResearchMethod EMPTY = new ConsumeItemResearchMethod(Ingredient.EMPTY, 0);
    public static final ResourceLocation ID = Researchd.rl("consume_item");

    @Override
    public void checkProgress(Level level, ResourceKey<Research> research, ResearchMethodProgress<?> progress, MethodContext context) {
        for (TeamMember member : context.team().getMembers()) {
            List<ItemStack> matchingItems = new ArrayList<>(8);
            int found = 0;

            Player player = level.getPlayerByUUID(member.player());
            if (player != null) {
                int containerSize = player.getInventory().getContainerSize();
                for (int i = 0; i < containerSize; i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (this.toConsume.test(stack)) {
                        matchingItems.add(stack);
                        found = Math.min(this.count, found + stack.getCount());
                    }

                    if (found >= this.count) {
                        int shrunkCount = 0;
                        List<ItemStack> itemsByLowestCount = matchingItems.stream().sorted(Comparator.comparingInt(ItemStack::getCount)).toList().reversed();
                        for (ItemStack itemStack : itemsByLowestCount) {
                            int shrinkBy = Math.min(itemStack.getCount(), this.count() - shrunkCount);
                            shrunkCount += shrinkBy;
                            itemStack.shrink(shrinkBy);
                            if (shrunkCount >= this.count()) {
                                progress.addProgress(this.getMaxProgress());
                                break;
                            }
                        }
                        break;
                    }

                }
            }
        }
    }

    @Override
    public float getMaxProgress() {
        return 1f;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Serializer getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchMethodSerializer<ConsumeItemResearchMethod> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<ConsumeItemResearchMethod> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("item").forGetter(ConsumeItemResearchMethod::toConsume),
                Codec.INT.fieldOf("count").forGetter(ConsumeItemResearchMethod::count)
        ).apply(instance, ConsumeItemResearchMethod::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeItemResearchMethod> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                ConsumeItemResearchMethod::toConsume,
                ByteBufCodecs.INT,
                ConsumeItemResearchMethod::count,
                ConsumeItemResearchMethod::new
        );

        private Serializer() {
        }

        @Override
        public @NotNull MapCodec<ConsumeItemResearchMethod> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ConsumeItemResearchMethod> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
