package com.portingdeadmods.researchd.data;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.PDLSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.capabilities.EntityResearchImpl;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ResearchdSavedData {
    public static final DeferredRegister<PDLSavedData<?>> SAVED_DATA = DeferredRegister.create(ResearchdRegistries.SAVED_DATA, Researchd.MODID);

    // TODO: Now that it's synced should also make Player Research fetch from team research first :P
    public static final Supplier<PDLSavedData<ResearchTeamMap>> TEAM_RESEARCH = SAVED_DATA.register("team_research",
            () -> PDLSavedData.builder(ResearchTeamMap.CODEC, () -> ResearchTeamMap.EMPTY)
                    .synced(ResearchTeamMap.STREAM_CODEC)
                    .onSync(ResearchTeamMap::onSync)
                    .build());

}
