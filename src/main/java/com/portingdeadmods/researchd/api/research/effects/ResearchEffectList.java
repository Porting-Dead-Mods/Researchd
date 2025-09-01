package com.portingdeadmods.researchd.api.research.effects;

import java.util.List;

public interface ResearchEffectList extends ResearchEffect {
    List<ResearchEffect> effects();
}
