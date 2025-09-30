package com.portingdeadmods.researchd.api.research;

import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ResearchIcon} is a utility interface containing
 * the id as well as the object that renders for the icon.
 * <p>
 * The actual rendering of the icon is handled on the client
 * side through a {@link com.portingdeadmods.researchd.api.client.ClientResearchIcon}
 * class
 * <p>
 * The default Research Icon implementation is {@link com.portingdeadmods.researchd.impl.research.ItemResearchIcon}
 * which is used in {@link com.portingdeadmods.researchd.impl.research.SimpleResearch}
 */
public interface ResearchIcon {
    /**
     * @return The id of this type of Research Icon.
     * Usually this is just a constant in the Research
     * Icon class
     */
    ResourceLocation id();
}
