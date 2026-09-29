package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.data.saved.TeamResearchEffectSavedData;
import com.portingdeadmods.researchd.impl.TeamResearchEffectDataMap;
import com.portingdeadmods.researchd.impl.research.effect.data.ItemUnlockEffectData;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;

public record ItemUnlockEffect(Optional<ItemStackTemplate> icon, Optional<String> name, Identifier item)
        implements ResearchEffect {
    private static final MapCodec<ItemUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ItemStackTemplate.CODEC.optionalFieldOf("icon").forGetter(ItemUnlockEffect::icon),
                    Codec.STRING.optionalFieldOf("name").forGetter(ItemUnlockEffect::name),
                    Identifier.CODEC.fieldOf("item").forGetter(ItemUnlockEffect::item))
            .apply(instance, ItemUnlockEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ItemUnlockEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC),
            ItemUnlockEffect::icon,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            ItemUnlockEffect::name,
            Identifier.STREAM_CODEC,
            ItemUnlockEffect::item,
            ItemUnlockEffect::new);

    public static final ResearchEffectSerializer<ItemUnlockEffect> SERIALIZER =
            ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final Identifier ID = Researchd.rl("unlock_item");

    public ItemUnlockEffect(ItemStack icon, String name, Identifier item) {
        this(
                Optional.ofNullable(icon).filter(stack -> !stack.isEmpty()).map(ItemStackTemplate::fromNonEmptyStack),
                Optional.ofNullable(name),
                item);
    }

    public ItemUnlockEffect(Identifier item) {
        this(Optional.empty(), Optional.empty(), item);
    }

    public ItemUnlockEffect(Item item) {
        this(BuiltInRegistries.ITEM.getKey(item));
    }

    @Override
    public void onUnlock(Level level, ResearchTeam team, ResourceKey<Research> research) {
        if (!level.isClientSide()) {
            TeamResearchEffectDataMap map = TeamResearchEffectSavedData.getData((ServerLevel) level);
            ItemUnlockEffectData data = map.computeIfAbsent(team.getId(), ResearchdEffectDataTypes.ITEM_UNLOCK, level);
            data.remove(this, level);
            map.setChanged();
            map.sync(team.getId(), data.type());
        }
    }

    @Override
    public void onLock(Level level, ResearchTeam team, ResourceKey<Research> research) {
        if (!level.isClientSide()) {
            TeamResearchEffectDataMap map = TeamResearchEffectSavedData.getData((ServerLevel) level);
            ItemUnlockEffectData data = map.computeIfAbsent(team.getId(), ResearchdEffectDataTypes.ITEM_UNLOCK, level);
            data.add(this, level);
            map.setChanged();
            map.sync(team.getId(), data.type());
        }
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.ITEM_UNLOCK.get();
    }

    public Item getItem() {
        return BuiltInRegistries.ITEM.getValue(this.item);
    }

    public ItemStack getDisplayStack() {
        return this.icon().map(ItemStackTemplate::create).orElseGet(() -> new ItemStack(this.getItem()));
    }

    public ResourceKey<Item> getItemKey() {
        return ResourceKey.create(Registries.ITEM, this.item());
    }

    @Override
    public ResearchEffectSerializer<ItemUnlockEffect> getSerializer() {
        return SERIALIZER;
    }
}
