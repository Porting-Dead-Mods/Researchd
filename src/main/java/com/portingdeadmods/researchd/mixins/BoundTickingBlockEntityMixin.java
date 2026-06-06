package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdConfig;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import com.portingdeadmods.researchd.api.ResearchdApi;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

;

@Mixin(targets = "net/minecraft/world/level/chunk/LevelChunk$BoundTickingBlockEntity")
public abstract class BoundTickingBlockEntityMixin {

    @Shadow @Final private BlockEntity blockEntity;

    @WrapMethod(method = "tick")
    private void researchd$pushOwnerContext(Operation<Void> original) {
        Level level = this.blockEntity.getLevel();
        UUID teamId = (level == null || level.isClientSide)
                ? null
                : ResearchdApi.getOrMigratePlacedByTeam(this.blockEntity, level);

        if (teamId == null) {
            original.call();
            return;
        }

        if (ResearchdConfig.Common.consoleDebug) {
            Researchd.debug("Recipe Filter", "BE push ", this.blockEntity.getClass().getSimpleName(), "@", this.blockEntity.getBlockPos(), " team=", teamId);
        }
        RecipeFilterContext.push(teamId, level);
        try {
            original.call();
        } finally {
            RecipeFilterContext.pop();
        }
    }
}
