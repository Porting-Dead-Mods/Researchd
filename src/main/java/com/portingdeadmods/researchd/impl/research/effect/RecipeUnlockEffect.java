package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.data.saved.TeamResearchEffectSavedData;
import com.portingdeadmods.researchd.impl.TeamResearchEffectDataMap;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public record RecipeUnlockEffect(
        Optional<ItemStackTemplate> icon, Optional<String> name, Set<ResourceKey<Recipe<?>>> recipes)
        implements ResearchEffect {
    private static final MapCodec<RecipeUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ItemStackTemplate.CODEC.optionalFieldOf("icon").forGetter(RecipeUnlockEffect::icon),
                    Codec.STRING.optionalFieldOf("name").forGetter(RecipeUnlockEffect::name),
                    CodecUtils.set(ResourceKey.codec(Registries.RECIPE))
                            .fieldOf("recipes")
                            .forGetter(RecipeUnlockEffect::recipes))
            .apply(instance, RecipeUnlockEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, RecipeUnlockEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC),
            RecipeUnlockEffect::icon,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            RecipeUnlockEffect::name,
            ResourceKey.streamCodec(Registries.RECIPE).apply(ByteBufCodecs.collection(HashSet::new)),
            RecipeUnlockEffect::recipes,
            RecipeUnlockEffect::new);

    public static final ResearchEffectSerializer<RecipeUnlockEffect> SERIALIZER =
            ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final Identifier ID = Researchd.rl("unlock_recipe");

    public RecipeUnlockEffect(ItemStack icon, String name, Identifier... recipes) {
        this(
                Optional.ofNullable(icon).filter(stack -> !stack.isEmpty()).map(ItemStackTemplate::fromNonEmptyStack),
                Optional.ofNullable(name),
                recipeKeys(recipes));
    }

    public RecipeUnlockEffect(Identifier... recipes) {
        this(Optional.empty(), Optional.empty(), recipeKeys(recipes));
    }

    private static Set<ResourceKey<Recipe<?>>> recipeKeys(Identifier... recipes) {
        return Stream.of(recipes)
                .map(id -> ResourceKey.create(Registries.RECIPE, id))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void onUnlock(Level level, ResearchTeam team, ResourceKey<Research> research) {
        if (!level.isClientSide()) {
            TeamResearchEffectDataMap map = TeamResearchEffectSavedData.getData((ServerLevel) level);
            RecipeUnlockEffectData data =
                    map.computeIfAbsent(team.getId(), ResearchdEffectDataTypes.RECIPE_UNLOCK, level);
            data.remove(this, level);
            map.setChanged();
            map.sync(team.getId(), data.type());
        }
    }

    @Override
    public void onLock(Level level, ResearchTeam team, ResourceKey<Research> research) {
        if (!level.isClientSide()) {
            TeamResearchEffectDataMap map = TeamResearchEffectSavedData.getData((ServerLevel) level);
            RecipeUnlockEffectData data =
                    map.computeIfAbsent(team.getId(), ResearchdEffectDataTypes.RECIPE_UNLOCK, level);
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
        return ResearchEffectTypes.RECIPE_UNLOCK.get();
    }

    public Set<RecipeHolder<?>> getRecipes(Level level) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.recipes.size());
        if (level instanceof ServerLevel serverLevel) {
            for (ResourceKey<Recipe<?>> recipe : this.recipes) {
                serverLevel.recipeAccess().byKey(recipe).ifPresent(recipes::add);
            }
        }
        return recipes;
    }

    @Override
    public ResearchEffectSerializer<RecipeUnlockEffect> getSerializer() {
        return RecipeUnlockEffect.SERIALIZER;
    }
}
