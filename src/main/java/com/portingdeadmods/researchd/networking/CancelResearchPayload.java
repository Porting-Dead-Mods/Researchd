package com.portingdeadmods.researchd.networking;
/*
import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CancelResearchPayload(int index) implements CustomPacketPayload {
    public static final Type<CancelResearchPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "cancel_research_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CancelResearchPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CancelResearchPayload::index,
            CancelResearchPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void setNameAction(CancelResearchPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            UUID senderId = sender.getUUID();
            ResearchdSavedData savedData = ResearchdSavedData.get(sender.level());

            TODO: After research queue is implemented

        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
} */