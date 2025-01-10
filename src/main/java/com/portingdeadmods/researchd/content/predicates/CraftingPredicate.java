package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPredicate;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.utils.Codecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record CraftingPredicate <T extends Recipe<?>> (RecipeHolder<T> recipe) implements ResearchPredicate {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        CraftingPredicateData data = player.getData(ResearchdAttachments.CRAFTING_PREDICATE.get());
        player.setData(ResearchdAttachments.CRAFTING_PREDICATE.get(), data.removeBlockedRecipe(this.recipe));
    }

    @Override
    public ResearchPredicateSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchPredicateSerializer<CraftingPredicate> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<CraftingPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codecs.RECIPE_HOLDER_CODEC.fieldOf("recipe").forGetter(CraftingPredicate::recipe)
        ).apply(instance, CraftingPredicate::new));

        private Serializer() {
        }

        @Override
        public MapCodec<CraftingPredicate> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CraftingPredicate> streamCodec() {
            return null;
        }
    }
}
