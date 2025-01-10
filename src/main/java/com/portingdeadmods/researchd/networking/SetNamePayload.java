package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import net.minecraft.core.UUIDUtil;
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

public record SetNamePayload(String name) implements CustomPacketPayload {
    public static final Type<SetNamePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "manage_member_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetNamePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetNamePayload::name,
            SetNamePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void setNameAction(SetNamePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            UUID senderId = sender.getUUID();
            ResearchdSavedData savedData = ResearchdSavedData.get(sender.level());

            if (ResearchTeamUtil.getPermissionLevel(sender) == 2) {
                ResearchTeamUtil.getResearchTeam(sender).setName(payload.name());
                savedData.setDirty();
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}