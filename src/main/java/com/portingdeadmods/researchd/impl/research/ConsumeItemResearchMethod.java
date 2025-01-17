package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ConsumeItemResearchMethod(Ingredient toConsume, int count) implements ResearchMethod {
    public static final ConsumeItemResearchMethod EMPTY = new ConsumeItemResearchMethod(Ingredient.EMPTY, 0);

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
    public ResearchMethodSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchMethodSerializer<ConsumeItemResearchMethod> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<ConsumeItemResearchMethod> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("item").forGetter(ConsumeItemResearchMethod::toConsume),
                Codec.INT.fieldOf("count").forGetter(ConsumeItemResearchMethod::count)
        ).apply(instance, ConsumeItemResearchMethod::new));

        private Serializer() {
        }

        @Override
        public MapCodec<ConsumeItemResearchMethod> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ConsumeItemResearchMethod> streamCodec() {
            return null;
        }
    }
}
