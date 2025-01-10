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

public record ManageModeratorPayload(UUID moderator, boolean remove) implements CustomPacketPayload {
    public static final Type<ManageModeratorPayload> TYPE = new Type<>(Researchd.rl("manage_moderator_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManageModeratorPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ManageModeratorPayload::moderator,
            ByteBufCodecs.BOOL,
            ManageModeratorPayload::remove,
            ManageModeratorPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void manageModeratorAction(ManageModeratorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            UUID senderId = sender.getUUID();
            ResearchdSavedData savedData = ResearchdSavedData.get(sender.level());

            if (ResearchTeamUtil.getPermissionLevel(sender) == 2) {
                if (payload.remove()) {
                    ResearchTeamUtil.getResearchTeam(sender).removeModerator(payload.moderator());
                    savedData.setDirty();
                } else {
                    ResearchTeamUtil.getResearchTeam(sender).addModerator(payload.moderator());
                    savedData.setDirty();
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}