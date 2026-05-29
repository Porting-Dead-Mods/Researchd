package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.graph.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public final class SpaghettiClient {
	/**
	 * <span style="color:red">CLIENT SIDE ONLY</span>
	 *
	 * <br>
	 * Returns the set of {@link ResearchNode} currently displayed on the screen.
	 */
	public static Set<ResearchNode> getNodesFromScreen() {
		return Minecraft.getInstance().screen instanceof ResearchScreen researchScreen ? new HashSet<>(researchScreen.getResearchGraphWidget().getCurrentGraph().nodes().values()) : new HashSet<>();
	}

	/**
	 * <span style="color:red">CLIENT SIDE ONLY</span>
	 *
	 * <br>
	 * Returns the current {@link ResearchScreen} if it is open, otherwise returns null.
	 */
	public @Nullable static ResearchScreen tryGetResearchScreen() {
		return Minecraft.getInstance().screen instanceof ResearchScreen ? (ResearchScreen) Minecraft.getInstance().screen : null;
	}

    public static Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

}

