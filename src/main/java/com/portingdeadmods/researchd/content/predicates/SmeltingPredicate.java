package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.client.research.ClientResearchEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.utils.Codecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public record SmeltingPredicate<T extends Recipe<?>> (RecipeHolder<T> recipe) implements ResearchEffect {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        SmeltingPredicateData data = player.getData(ResearchdAttachments.SMELTING_PREDICATE.get());
        player.setData(ResearchdAttachments.SMELTING_PREDICATE.get(), data.removeBlockedRecipe(this.recipe));
    }

    @Override
    public ResourceLocation id() {
        return null;
    }

    @Override
    public ClientResearchEffect<?> getClientResearchEffect() {
        return null;
    }

    @Override
    public ResearchEffectSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchEffectSerializer<SmeltingPredicate> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SmeltingPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codecs.RECIPE_HOLDER_CODEC.fieldOf("recipe").forGetter(SmeltingPredicate::recipe)
        ).apply(instance, SmeltingPredicate::new));

        private Serializer() {
        }

        @Override
        public MapCodec<SmeltingPredicate> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmeltingPredicate> streamCodec() {
            return null;
        }
    }
}
