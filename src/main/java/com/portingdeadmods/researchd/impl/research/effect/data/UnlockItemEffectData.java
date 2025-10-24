package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.impl.research.effect.UnlockItemEffect;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;

public record UnlockItemEffectData(UniqueArray<ResourceKey<Item>> blockedItems) implements ResearchEffectData<UnlockItemEffect> {
    public static final UnlockItemEffectData EMPTY = new UnlockItemEffectData(new UniqueArray<>());

    public static final Codec<UnlockItemEffectData> CODEC = UniqueArray.CODEC(ResourceKey.codec(Registries.ITEM))
            .xmap(UnlockItemEffectData::new, UnlockItemEffectData::blockedItems);

	public static final StreamCodec<ByteBuf, UnlockItemEffectData> STREAM_CODEC = StreamCodec.composite(
			UniqueArray.STREAM_CODEC(ResourceKey.streamCodec(Registries.ITEM)),
			UnlockItemEffectData::blockedItems,
			UnlockItemEffectData::new
	);

    @Override
    public UnlockItemEffectData add(UnlockItemEffect effect, Level level) {
        UniqueArray<ResourceKey<Item>> items = new UniqueArray<>(this.blockedItems());
        items.add(effect.getItemKey());
        return new UnlockItemEffectData(items);
    }

    @Override
    public UnlockItemEffectData remove(UnlockItemEffect effect, Level level) {
        UniqueArray<ResourceKey<Item>> items = new UniqueArray<>(this.blockedItems());
        items.remove(effect.getItemKey());
        return new UnlockItemEffectData(items);
    }

    public boolean isBlocked(ItemStack stack) {
        return this.isBlocked(stack.getItem());
    }

    public boolean isBlocked(Item item) {
        return this.blockedItems.contains(item.builtInRegistryHolder().key());
    }

    @Override
    public UnlockItemEffectData getDefault(Level level) {
        Collection<UnlockItemEffect> unlockItemEffects = ResearchHelperCommon.getResearchEffects(UnlockItemEffect.class, level);
        UniqueArray<ResourceKey<Item>> blockedItems = new UniqueArray<>();

        for (UnlockItemEffect effect : unlockItemEffects) {
            blockedItems.add(effect.getItemKey());
        }

        return new UnlockItemEffectData(blockedItems);
    }

    @Override
    public UniqueArray<ResourceKey<Item>> getAll() {
        return this.blockedItems();
    }
}
