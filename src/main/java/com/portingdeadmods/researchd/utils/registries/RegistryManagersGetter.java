package com.portingdeadmods.researchd.utils.registries;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;

public interface RegistryManagersGetter {
    ReloadableRegistryManager<Research> researchd$getResearchesManager();

    ReloadableRegistryManager<ResearchPack> researchd$getResearchPackManager();
}
