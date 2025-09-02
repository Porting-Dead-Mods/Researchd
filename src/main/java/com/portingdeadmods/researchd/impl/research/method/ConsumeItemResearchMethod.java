package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public record ConsumeItemResearchMethod(Ingredient toConsume, int count) implements ResearchMethod {
    public static final ConsumeItemResearchMethod EMPTY = new ConsumeItemResearchMethod(Ingredient.EMPTY, 0);
    public static final ResourceLocation ID = Researchd.rl("consume_item");

    @Override
    public boolean canResearch(Player player, ResourceKey<Research> research) {
        int amount = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (this.toConsume.test(item)) {
                amount += item.getCount();
                if (amount >= this.count) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onResearchStart(Player player, ResourceKey<Research> research) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (this.toConsume.test(item)) {
                int toRemove = Math.min(this.count, item.getMaxStackSize());
                inventory.removeItem(i, toRemove);
            }
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Serializer getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public ResearchMethodProgress getDefaultProgress() {
        return new ResearchMethodProgress(this, this.count);
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
