package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.research.ClientResearchEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.content.predicates.CraftingPredicateData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.utils.Codecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

// TODO: Validate the recipe
public record CraftingUnlockResearchEffect(ResourceLocation recipe) implements ResearchEffect {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        CraftingPredicateData data = player.getData(ResearchdAttachments.CRAFTING_PREDICATE.get());
        player.setData(ResearchdAttachments.CRAFTING_PREDICATE.get(), data.removeBlockedRecipe(this.getRecipe(level)));
    }

    @Override
    public ResourceLocation id() {
        return Researchd.rl("crafting_recipe_unlock");
    }

    public RecipeHolder<? extends CraftingRecipe> getRecipe(Level level) {
        return (RecipeHolder<? extends CraftingRecipe>) level.getRecipeManager().byKey(this.recipe).orElse(null);
    }

    @Override
    public ClientResearchEffect<?> getClientResearchEffect() {
        return null;
    }

    @Override
    public ResearchEffectSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchEffectSerializer<CraftingUnlockResearchEffect> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<CraftingUnlockResearchEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("recipe").forGetter(CraftingUnlockResearchEffect::recipe)
        ).apply(instance, CraftingUnlockResearchEffect::new));

        private Serializer() {
        }

        @Override
        public MapCodec<CraftingUnlockResearchEffect> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CraftingUnlockResearchEffect> streamCodec() {
            return null;
        }
    }
}
