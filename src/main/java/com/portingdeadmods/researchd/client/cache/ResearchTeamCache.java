package com.portingdeadmods.researchd.client.cache;

import com.portingdeadmods.researchd.impl.TeamResearchEffectDataMap;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ResearchTeamCache {
    public static ResearchTeamMap researchTeamMap;
    public static TeamResearchEffectDataMap teamResearchEffectDataMap;
}
