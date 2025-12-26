package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Client-side version of {@link ResearchIcon} used for rendering the icon.
 * Stores the Research Icon itself as well.
 * <p>
 * Get instantiated for each Research.
 * @param <I> The original Research Icon
 */
public interface ClientResearchIcon<I extends ResearchIcon> {
    /**
     * @return The original Research Icon
     */
    I icon();

    /**
     * Render the icon
     *
     * @param panelLeft Left start position of the current panel the icon is rendered on
     * @param panelTop Top start position of the current panel the icon is rendered on
     */
    void render(GuiGraphics guiGraphics, int panelLeft, int panelTop, int mouseX, int mouseY, float scale, float partialTicks);

    static <I extends ResearchIcon> ClientResearchIcon<I> getClientIcon(ResearchIcon icon) {
        return (ClientResearchIcon<I>) ResearchdClient.RESEARCH_ICONS.get(icon.id()).apply(icon);
    }

}
