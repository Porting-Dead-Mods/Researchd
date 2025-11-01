package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.resources.RegistryManagersGetter;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.Level;

public final class ResearchdManagers {
    public static ReloadableRegistryManager<Research> getResearchesManager(Level level) {
        if (!level.isClientSide()) {
            ReloadableServerResources resources = level.getServer().getServerResources().managers();
            return ((RegistryManagersGetter) resources).researchd$getResearchesManager();
        } else {
            return ((RegistryManagersGetter) level).researchd$getResearchesManager();
        }
    }

    public static ReloadableRegistryManager<ResearchPack> getResearchPacksManager(Level level) {
        if (!level.isClientSide()) {
            ReloadableServerResources resources = level.getServer().getServerResources().managers();
            return ((RegistryManagersGetter) resources).researchd$getResearchPackManager();
        } else {
            return ((RegistryManagersGetter) level).researchd$getResearchPackManager();
        }
    }

}
