package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public record RecipeUnlockEffect(ResourceLocation recipe) implements ResearchEffect {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        RecipeUnlockEffectData data = player.getData(ResearchdAttachments.RECIPE_PREDICATE.get());
        player.setData(ResearchdAttachments.RECIPE_PREDICATE.get(), data.remove(this, level));
    }

    @Override
    public ResourceLocation id() {
        return Researchd.rl("unlock_recipe");
    }

    public RecipeHolder<?> getRecipe(Level level) {
        return level.getRecipeManager().byKey(this.recipe).orElse(null);
    }

    @Override
    public ResearchEffectSerializer<RecipeUnlockEffect> getSerializer() {
        return RecipeUnlockEffect.Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchEffectSerializer<RecipeUnlockEffect> {
        public static final RecipeUnlockEffect.Serializer INSTANCE = new RecipeUnlockEffect.Serializer();
        public static final MapCodec<RecipeUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("recipe").forGetter(RecipeUnlockEffect::recipe)
        ).apply(instance, RecipeUnlockEffect::new));

        private Serializer() {
        }

        @Override
        public MapCodec<RecipeUnlockEffect> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RecipeUnlockEffect> streamCodec() {
            return null;
        }
    }
}
