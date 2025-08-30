package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;

public interface CommandBase {
	LiteralCommandNode<CommandSourceStack> build();
}
