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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record TransferOwnershipPayload(UUID uuid) implements CustomPacketPayload {
    public static final Type<TransferOwnershipPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "manage_member_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TransferOwnershipPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            TransferOwnershipPayload::uuid,
            TransferOwnershipPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void transferOwnershipAction(TransferOwnershipPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            UUID senderId = sender.getUUID();
            ResearchdSavedData savedData = ResearchdSavedData.get(sender.level());

            if (ResearchTeamUtil.getPermissionLevel(sender) == 2) {
                if (ResearchTeamUtil.arePlayersSameTeam(level, senderId, payload.uuid())) {
                    // Set the new leader
                    ResearchTeamUtil.getResearchTeam(sender).setLeader(payload.uuid());

                    // If he's moderator remove him from the mod list
                    if (ResearchTeamUtil.getPermissionLevel(level, payload.uuid()) == 1) {
                        ResearchTeamUtil.getResearchTeam(sender).removeModerator(payload.uuid());
                    }

                    // Set the old leader as moderator
                    ResearchTeamUtil.getResearchTeam(sender).addModerator(senderId);
                    savedData.setDirty();
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}