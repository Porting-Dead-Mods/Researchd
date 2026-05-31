package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectManager;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class ResearchEffectHelperCommon {
    public static <T extends ResearchEffectData<?>> @Nullable T getEffectDataForPlayer(Player player, ResearchEffectDataType<T> type) {
        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(player.level());
        ResearchEffectManager researchEffectManager = ResearchdApi.getResearchEffectManager(player.level());
        ResearchTeam team = teamManager.getTeamByPlayer(player);

        return researchEffectManager.getEffectData(team.getId(), type);
    }

    public static <T extends ResearchEffectData<?>> @Nullable T getEffectDataForPlayer(Player player, Supplier<ResearchEffectDataType<T>> type) {
        return ResearchEffectHelperCommon.getEffectDataForPlayer(player, type.get());
    }
}
