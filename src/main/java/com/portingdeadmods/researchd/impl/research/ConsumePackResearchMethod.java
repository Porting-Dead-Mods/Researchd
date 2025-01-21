package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.client.research.ClientConsumePackResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ConsumePackResearchMethod(ResourceKey<ResearchPack> pack, int count) implements ResearchMethod {
    public static final ResourceLocation ID = Researchd.rl("consume_pack");

    @Override
    public boolean canResearch(Player player, ResourceKey<Research> research) {
        int amount = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            Optional<ResourceKey<ResearchPack>> researchPackResourceKey = item.get(ResearchdDataComponents.RESEARCH_PACK).researchPackKey();
            if (item.has(ResearchdDataComponents.RESEARCH_PACK) && researchPackResourceKey.isPresent() && researchPackResourceKey.get().compareTo(this.pack) == 0) {
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
            Optional<ResourceKey<ResearchPack>> researchPackResourceKey = item.get(ResearchdDataComponents.RESEARCH_PACK).researchPackKey();
            if (item.has(ResearchdDataComponents.RESEARCH_PACK) && researchPackResourceKey.isPresent() && researchPackResourceKey.get().compareTo(this.pack) == 0) {
                int toRemove = Math.min(this.count, item.getMaxStackSize());
                inventory.removeItem(i, toRemove);
            }
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public ItemStack asStack() {
        ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();
        stack.set(ResearchdDataComponents.RESEARCH_PACK, new ResearchPackComponent(Optional.of(pack)));
        return stack;
    }

    @Override
    public ClientResearchMethod getClientMethod() {
        return ClientConsumePackResearchMethod.INSTANCE;
    }

    @Override
    public ResearchMethodSerializer<ConsumePackResearchMethod> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchMethodSerializer<ConsumePackResearchMethod> {
        public static final Serializer INSTANCE = new ConsumePackResearchMethod.Serializer();
        public static final MapCodec<ConsumePackResearchMethod> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResearchPack.RESOURCE_KEY_CODEC.fieldOf("pack").forGetter(ConsumePackResearchMethod::pack),
                Codec.INT.fieldOf("count").forGetter(ConsumePackResearchMethod::count)
        ).apply(instance, ConsumePackResearchMethod::new));

        private Serializer() {
        }

        @Override
        public MapCodec<ConsumePackResearchMethod> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ConsumePackResearchMethod> streamCodec() {
            return null;
        }
    }
}
