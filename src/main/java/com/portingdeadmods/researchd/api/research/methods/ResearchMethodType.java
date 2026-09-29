package com.portingdeadmods.researchd.api.research.methods;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public record ResearchMethodType(Identifier id, ResearchIcon icon, boolean parentType) {
    public Component getName() {
        return Utils.registryTranslation(ResearchdRegistries.RESEARCH_METHOD_TYPE, this);
    }

    public static ResearchMethodType single(Identifier id, ResearchIcon icon) {
        return new ResearchMethodType(id, icon, false);
    }

    public static ResearchMethodType multiple(Identifier id, ResearchIcon icon) {
        return new ResearchMethodType(id, icon, true);
    }
}
