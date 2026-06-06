package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import com.portingdeadmods.researchd.impl.research.effect.ItemUnlockEffect;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;

public record ItemUnlockEffectData(UniqueArray<ResourceKey<Item>> blockedItems) implements ResearchEffectData<ItemUnlockEffect> {
    public static final ItemUnlockEffectData EMPTY = new ItemUnlockEffectData(new UniqueArray<>());

    public static final MapCodec<ItemUnlockEffectData> CODEC = UniqueArray.CODEC(ResourceKey.codec(Registries.ITEM))
            .xmap(ItemUnlockEffectData::new, ItemUnlockEffectData::blockedItems).fieldOf("blocked_items");

	public static final StreamCodec<? super RegistryFriendlyByteBuf, ItemUnlockEffectData> STREAM_CODEC = StreamCodec.composite(
			UniqueArray.STREAM_CODEC(ResourceKey.streamCodec(Registries.ITEM)),
			ItemUnlockEffectData::blockedItems,
			ItemUnlockEffectData::new
	);
    public static final ResearchEffectDataType<ItemUnlockEffectData> TYPE = ResearchEffectDataType.simple(ItemUnlockEffectData::new, CODEC, STREAM_CODEC);

    public ItemUnlockEffectData() {
        this(new UniqueArray<>());
    }

    @Override
    public void add(ItemUnlockEffect effect, Level level) {
        UniqueArray<ResourceKey<Item>> items = this.blockedItems();
        items.add(effect.getItemKey());
    }

    @Override
    public void remove(ItemUnlockEffect effect, Level level) {
        UniqueArray<ResourceKey<Item>> items = this.blockedItems();
        items.remove(effect.getItemKey());
    }

    public boolean isBlocked(ItemStack stack) {
        return this.isBlocked(stack.getItem());
    }

    public boolean isBlocked(Item item) {
        return this.blockedItems.contains(item.builtInRegistryHolder().key());
    }

    @Override
    public UniqueArray<ResourceKey<Item>> getAll() {
        return this.blockedItems();
    }

    @Override
    public ResearchEffectDataType<ItemUnlockEffectData> type() {
        return TYPE;
    }
}
