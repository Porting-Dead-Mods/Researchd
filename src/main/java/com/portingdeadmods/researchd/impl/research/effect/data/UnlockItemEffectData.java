package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.impl.research.effect.UnlockItemEffect;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record UnlockItemEffectData(Set<ResourceKey<Item>> blockedItems) implements ResearchEffectData<UnlockItemEffect> {
    public static final UnlockItemEffectData EMPTY = new UnlockItemEffectData(Collections.emptySet());

    public static final Codec<UnlockItemEffectData> CODEC = CodecUtils.set(ResourceKey.codec(Registries.ITEM))
            .xmap(UnlockItemEffectData::new, UnlockItemEffectData::blockedItems);

    @Override
    public UnlockItemEffectData add(UnlockItemEffect effect, Level level) {
        Set<ResourceKey<Item>> items = new HashSet<>(this.blockedItems());
        items.add(effect.getItemKey());
        return new UnlockItemEffectData(items);
    }

    @Override
    public UnlockItemEffectData remove(UnlockItemEffect effect, Level level) {
        Set<ResourceKey<Item>> items = new HashSet<>(this.blockedItems());
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
        Set<ResourceKey<Item>> blockedItems = new HashSet<>();

        for (UnlockItemEffect effect : unlockItemEffects) {
            blockedItems.add(effect.getItemKey());
        }

        return new UnlockItemEffectData(blockedItems);
    }

    @Override
    public Set<ResourceKey<Item>> getAll() {
        return this.blockedItems();
    }
}
