package com.portingdeadmods.researchd.impl.data;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.impl.capabilities.EntityResearchImpl;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ResearchedAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Researchd.MODID);

    public static final Supplier<AttachmentType<EntityResearchImpl>> ENTITY_RESEARCH = ATTACHMENTS.register("entity_research",
            () -> AttachmentType.builder(() -> EntityResearchImpl.EMPTY).serialize(EntityResearchImpl.CODEC).copyOnDeath().build());
}
