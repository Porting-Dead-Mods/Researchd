package com.portingdeadmods.researchd.data;

import com.portingdeadmods.portingdeadlibs.PDLRegistries;
import com.portingdeadmods.portingdeadlibs.api.data.saved.PDLSavedData;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ResearchdSavedData {
    public static final DeferredRegister<PDLSavedData<?>> SAVED_DATA = DeferredRegister.create(PDLRegistries.SAVED_DATA, Researchd.MODID);

    public static final Supplier<PDLSavedData<ResearchTeamMap>> TEAM_RESEARCH = SAVED_DATA.register("team_research",
            () -> PDLSavedData.builder(ResearchTeamMap.CODEC, () -> ResearchTeamMap.EMPTY)
                    .synced(ResearchTeamMap.STREAM_CODEC)
                    .postSync(ResearchTeamMap::onSync)
		            .global()
                    .build());

}
