package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record EnterTeamPayload(UUID memberOfTeam) implements CustomPacketPayload {
    public static final Type<EnterTeamPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "enter_team_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnterTeamPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            EnterTeamPayload::memberOfTeam,
            EnterTeamPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void enterTeamAction(EnterTeamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            UUID senderId = sender.getUUID();

            ResearchdSavedData savedData = ResearchdSavedData.get(level);
            ResearchTeam team = savedData.getTeamForUUID(payload.memberOfTeam());

            if (team != null && team.getReceivedInvites().contains(senderId)) {
                team.addMember(senderId);
                team.removeInvite(senderId);
                savedData.setDirty();
            }

        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}