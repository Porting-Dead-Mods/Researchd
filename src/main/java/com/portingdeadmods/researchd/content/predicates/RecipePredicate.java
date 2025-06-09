package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.research.ClientResearchEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public record RecipePredicate(ResourceLocation recipe) implements ResearchEffect {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        RecipePredicateData data = player.getData(ResearchdAttachments.RECIPE_PREDICATE.get());
        player.setData(ResearchdAttachments.RECIPE_PREDICATE.get(), data.removeBlockedRecipe(this.getRecipe(level)));
    }

    @Override
    public ResourceLocation id() {
        return Researchd.rl("unlock_recipe");
    }

    public RecipeHolder<? extends CraftingRecipe> getRecipe(Level level) {
        return (RecipeHolder<? extends CraftingRecipe>) level.getRecipeManager().byKey(this.recipe).orElse(null);
    }

    @Override
    public ClientResearchEffect<RecipePredicate> getClientResearchEffect() {
        return null;
    }

    @Override
    public ResearchEffectSerializer<RecipePredicate> getSerializer() {
        return RecipePredicate.Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchEffectSerializer<RecipePredicate> {
        public static final RecipePredicate.Serializer INSTANCE = new RecipePredicate.Serializer();
        public static final MapCodec<RecipePredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("recipe").forGetter(RecipePredicate::recipe)
        ).apply(instance, RecipePredicate::new));

        private Serializer() {
        }

        @Override
        public MapCodec<RecipePredicate> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RecipePredicate> streamCodec() {
            return null;
        }
    }
}
