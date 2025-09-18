package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow public abstract boolean isCreative();

    private PlayerMixin() {
        super(null, null);
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        DimensionUnlockEffectData data = getData(ResearchdAttachments.DIMENSION_PREDICATE);
        return super.canChangeDimensions(oldLevel, newLevel)
                && (!data.blockedDimensions().contains(newLevel.dimensionTypeRegistration().getKey()) || this.isCreative());
    }
}
