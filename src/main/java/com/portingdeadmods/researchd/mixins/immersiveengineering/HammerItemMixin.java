package com.portingdeadmods.researchd.mixins.immersiveengineering;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.common.items.HammerItem;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.compat.immersiveengineering.UnlockIEMultiblockEffectData;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(targets = "blusunrize.immersiveengineering.common.items.HammerItem", remap = false)
public class HammerItemMixin {
    @Unique
    private MultiblockHandler.IMultiblock researchd$currentMultiblock;

    @Inject(
            method = "onItemUseFirst",
            at = @At(
                    value = "INVOKE",
                    target = "Lblusunrize/immersiveengineering/api/multiblocks/MultiblockHandler$IMultiblock;isBlockTrigger(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/Level;)Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false,
            require = 0
    )
    private void researchd$captureMultiblock(
            ItemStack stack, UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, Level world, BlockPos pos, Player player, Direction side, HammerItem.MultiblockRestriction restriction, Direction multiblockSide, Iterator var9, MultiblockHandler.IMultiblock mb
    ) {
        researchd$currentMultiblock = mb;
    }

    @ModifyVariable(
            method = "onItemUseFirst",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            ordinal = 0,
            name = "isAllowed",
            remap = false,
            require = 0
    )
    private boolean researchd$checkMultiblockResearch(
            boolean isAllowed,
            ItemStack stack,
            UseOnContext context
    ) {
        Player player = context.getPlayer();
        if (player == null || researchd$currentMultiblock == null) {
            return isAllowed;
        }

        UnlockIEMultiblockEffectData data = ResearchdApi.getEffectDataForPlayer(player, ResearchdEffectDataTypes.IE_MULTIBLOCK_UNLOCK);
        if (data != null && data.isBlocked(researchd$currentMultiblock.getUniqueName())) {
            return false;
        }

        return isAllowed;
    }
}
