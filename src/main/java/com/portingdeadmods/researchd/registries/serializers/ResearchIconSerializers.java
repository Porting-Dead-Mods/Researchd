package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchIconSerializer;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import com.portingdeadmods.researchd.impl.research.icons.SpriteResearchIcon;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchIconSerializers {
    public static final DeferredRegister<ResearchIconSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_ICON_SERIALIZER, Researchd.MODID);

    static {
        SERIALIZERS.register("item_research_icon", () -> ItemResearchIcon.SERIALIZER);
        SERIALIZERS.register("sprite_research_icon", () -> SpriteResearchIcon.SERIALIZER);
    }
}
