package com.portingdeadmods.researchd.api.data;

import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import net.minecraft.resources.ResourceLocation;

/**
 * !This class is not a networking packet! <br> <br>
 *
 * This class should be used to send research progress whenever progress related to a method would be made. <br>
 * e.g. The ResearchLab would send a ResearchProgressPacket(progress, {@link ConsumePackResearchMethod#getClass()})
 *
 * @param progress The amount of progress made on the research method
 * @param methodId {@link ResearchMethod#id()}
 */
public record ResearchProgressPacket(float progress, ResourceLocation methodId) {
    // Pass
}
