package com.portingdeadmods.researchd.compat.immersiveengineering;

import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import net.minecraft.resources.Identifier;

public class RdImmersiveEngineering {
    public static ResearchEffect unlockMultiblock(String multiblock) {
        return new UnlockIEMultiblockEffect(Identifier.parse(multiblock));
    }
}
