package com.portingdeadmods.researchd.impl.research.icons;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.serializers.ResearchIconSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

public record TextResearchIcon(Component text) implements ResearchIcon {
    public static final ResearchIconSerializer<TextResearchIcon> SERIALIZER =
            ResearchIconSerializer.simple(ComponentSerialization.CODEC
                    .xmap(TextResearchIcon::new, TextResearchIcon::text)
                    .fieldOf("text"));
    public static final Identifier ID = Researchd.rl("text_research_icon");
    public static final TextResearchIcon EMPTY = new TextResearchIcon(Component.empty());

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public ResearchIconSerializer<TextResearchIcon> getSerializer() {
        return SERIALIZER;
    }
}
