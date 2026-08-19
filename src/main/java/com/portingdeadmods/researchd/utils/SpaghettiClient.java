package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.graph.ResearchNode;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
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
		return Minecraft.getInstance().screen instanceof ResearchScreen researchScreen ? researchScreen : null;
	}

    public static Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

	/**
	 * <span style="color:red">CLIENT SIDE ONLY</span>
	 *
	 * <br>
	 * Echoes a message reported through {@link com.portingdeadmods.researchd.Researchd#error} into chat.
	 */
	public static void sendErrorToChat(String message) {
		Minecraft mc = Minecraft.getInstance();
		mc.execute(() -> {
			if (mc.player != null) {
				mc.player.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Errors.DATA_INCONSISTENCY, message));
			}
		});
	}

}

