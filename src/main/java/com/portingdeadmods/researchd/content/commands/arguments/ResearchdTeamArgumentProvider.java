package com.portingdeadmods.researchd.content.commands.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import net.minecraft.commands.CommandSourceStack;

public interface ResearchdTeamArgumentProvider {
    ResearchTeam getTeam(CommandSourceStack source) throws CommandSyntaxException;
}
