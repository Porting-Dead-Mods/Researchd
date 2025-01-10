package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.content.predicates.DimensionPredicateData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    private PlayerMixin() {
        super(null, null);
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        DimensionPredicateData data = getData(ResearchdAttachments.DIMENSION_PREDICATE);
        return super.canChangeDimensions(oldLevel, newLevel)
                && !data.blockedDimensions().contains(newLevel.dimensionTypeRegistration().getKey());
    }
}
