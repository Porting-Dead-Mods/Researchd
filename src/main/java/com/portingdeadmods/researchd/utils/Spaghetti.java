package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class Spaghetti {
	/**
	 * <span style="color:red">CLIENT SIDE ONLY</span>
	 *
	 * <br>
	 * Returns the set of {@link ResearchNode} currently displayed on the screen.
	 */
	public static Set<ResearchNode> getNodesFromScreen() {
		return Minecraft.getInstance().screen instanceof ResearchScreen ? ((ResearchScreen) Minecraft.getInstance().screen).getResearchGraphWidget().getCurrentGraph().nodes() : new HashSet<>();
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

	public static void printAABB(AABB aabb) {
		Researchd.debug("AABB Debug", "minX: " + aabb.minX);
		Researchd.debug("AABB Debug","minY: " + aabb.minY);
		Researchd.debug("AABB Debug","minZ: " + aabb.minZ);
		Researchd.debug("AABB Debug","maxX: " + aabb.maxX);
		Researchd.debug("AABB Debug","maxY: " + aabb.maxY);
		Researchd.debug("AABB Debug","maxZ: " + aabb.maxZ);
	}
}

