package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchEffect;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import java.util.*;

public class Spaghetti {
	/**
	 * <span style="color:red;">CLIENT SIDE ONLY</span>
	 *
	 * <br>
	 * Returns the set of {@link ResearchNode} currently displayed on the screen.
	 */
	public static Set<ResearchNode> getNodesFromScreen() {
		return Minecraft.getInstance().screen instanceof ResearchScreen ? ((ResearchScreen) Minecraft.getInstance().screen).getResearchGraphWidget().getCurrentGraph().nodes() : new HashSet<>();
	}
}

