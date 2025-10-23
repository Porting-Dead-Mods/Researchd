package com.portingdeadmods.researchd.impl.research.method;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Research method that completes once the team collectively holds the required amount of the target ingredient.
 */
public record CheckItemPresenceResearchMethod(Ingredient target, int count) implements ResearchMethod {
    public static final CheckItemPresenceResearchMethod EMPTY = new CheckItemPresenceResearchMethod(Ingredient.EMPTY, 0);
    public static final ResourceLocation ID = Researchd.rl("check_item_presence");

    @Override
    public void checkProgress(Level level, ResourceKey<Research> research, ResearchProgress.Task task, MethodContext context) {
        if (this.count <= 0 || this.target == Ingredient.EMPTY) {
            task.addProgress(this.getMaxProgress() - task.getProgress());
            return;
        }

        int totalFound = 0;
        for (TeamMember member : context.team().getMembers()) {
            Player player = level.getPlayerByUUID(member.player());
            if (player == null) {
                continue;
            }

            totalFound += this.countMatchingItems(player);
            if (totalFound >= this.count) {
                break;
            }
        }

        float targetProgress = Math.min(1.0F, (float) totalFound / (float) this.count) * this.getMaxProgress();
        float delta = targetProgress - task.getProgress();
        if (delta > 0.0F) {
            task.addProgress(delta);
        }
    }

    private int countMatchingItems(Player player) {
        int matches = 0;
        ItemStack stack;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            stack = player.getInventory().getItem(slot);
            if (this.target.test(stack)) {
                matches += stack.getCount();
            }
        }
        return matches;
    }

    @Override
    public float getMaxProgress() {
        return 1.0F;
    }

    @Override
    public ResearchProgress createProgress() {
        return ResearchProgress.single(this);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Serializer getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchMethodSerializer<CheckItemPresenceResearchMethod> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<CheckItemPresenceResearchMethod> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("item").forGetter(CheckItemPresenceResearchMethod::target),
                Codec.INT.fieldOf("count").forGetter(CheckItemPresenceResearchMethod::count)
        ).apply(instance, CheckItemPresenceResearchMethod::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CheckItemPresenceResearchMethod> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                CheckItemPresenceResearchMethod::target,
                ByteBufCodecs.INT,
                CheckItemPresenceResearchMethod::count,
                CheckItemPresenceResearchMethod::new
        );

        private Serializer() {
        }

        @Override
        public @NotNull MapCodec<CheckItemPresenceResearchMethod> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CheckItemPresenceResearchMethod> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
