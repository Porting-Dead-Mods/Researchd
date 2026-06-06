package com.portingdeadmods.researchd.data;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInteractionType;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public final class ResearchdAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Researchd.MODID);

    // Stores the team UUID that owns the block entity (the placer's team at placement time).
    // Legacy worlds may still hold a raw player UUID here; ResearchdApi.getOrMigratePlacedByTeam
    // performs a silent in-place migration on first read.
    public static final Supplier<AttachmentType<UUID>> PLACED_BY_UUID = ATTACHMENTS.register("placed_by_uuid",
            () -> AttachmentType.builder(() -> UUID.fromString("00000000-0000-0000-0000-000000000000"))
                    .serialize(UUIDUtil.CODEC)
                    .build());

    public static final Supplier<AttachmentType<ResearchInteractionType>> RESEARCH_INTERACTION_TYPE = ATTACHMENTS.register("research_interaction_type",
            () -> AttachmentType.builder(() -> ResearchInteractionType.DEFAULT)
                    .serialize(ResearchInteractionType.CODEC)
                    .sync(ResearchInteractionType.STREAM_CODEC)
                    .build());

    public static final Supplier<AttachmentType<EditModeSettingsImpl>> EDIT_MODE_SETTINGS = ATTACHMENTS.register("edit_mode_settings",
            () -> AttachmentType.builder(() -> EditModeSettingsImpl.EMPTY)
                    .serialize(EditModeSettingsImpl.CODEC)
                    .sync(EditModeSettingsImpl.STREAM_CODEC)
                    .build());
}
