package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.graph.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public final class Spaghetti {
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

	public static void printAABB(AABB aabb) {
		Researchd.debug("AABB Debug", "minX: " + aabb.minX);
		Researchd.debug("AABB Debug","minY: " + aabb.minY);
		Researchd.debug("AABB Debug","minZ: " + aabb.minZ);
		Researchd.debug("AABB Debug","maxX: " + aabb.maxX);
		Researchd.debug("AABB Debug","maxY: " + aabb.maxY);
		Researchd.debug("AABB Debug","maxZ: " + aabb.maxZ);
	}

    public static Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

}

