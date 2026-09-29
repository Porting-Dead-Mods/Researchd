package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.api.ResearchdApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// TODO(26.1 port, blocked dimensions): not in researchd.mixins.json, so Blocked dimensions are switched off until
// this hook is re-enabled.
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow
    public abstract boolean isCreative();

    private PlayerMixin() {
        super(null, null);
    }

    @Override
    public boolean canTeleport(Level oldLevel, Level newLevel) {
        Player self = (Player) (Object) this;
        return super.canTeleport(oldLevel, newLevel)
                && (this.isCreative()
                        || !ResearchdApi.isDimensionBlocked(
                                self, newLevel.dimensionTypeRegistration().getKey()));
    }
}
