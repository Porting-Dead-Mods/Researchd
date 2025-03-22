package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.client.Minecraft;

import java.util.HashSet;
import java.util.Set;

public class Spaghetti {
	public static Set<ResearchNode> getNodesFromScreen() {
		return Minecraft.getInstance().screen instanceof ResearchScreen ? ((ResearchScreen) Minecraft.getInstance().screen).getResearchGraphWidget().getCurrentGraph().nodes() : new HashSet<>();
	}
}
