package com.portingdeadmods.researchd.api.data.team;

import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;

import java.util.UUID;

public record TeamMember(UUID player, ResearchTeamRole role) {
}
