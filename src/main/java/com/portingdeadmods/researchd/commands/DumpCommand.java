package com.portingdeadmods.researchd.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DumpCommand {
    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("dump")
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    source.sendSystemMessage(ResearchTeamHelper.getFormattedDump(source.getLevel()));
                    return 1;
                })
                .build();
    }
}
