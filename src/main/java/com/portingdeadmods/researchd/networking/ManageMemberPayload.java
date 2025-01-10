package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
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

public record ManageMemberPayload(UUID member, boolean remove) implements CustomPacketPayload {
    public static final Type<ManageMemberPayload> TYPE = new Type<>(Researchd.rl("manage_member_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManageMemberPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ManageMemberPayload::member,
            ByteBufCodecs.BOOL,
            ManageMemberPayload::remove,
            ManageMemberPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void manageMemberAction(ManageMemberPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            UUID senderId = sender.getUUID();
            ResearchdSavedData savedData = ResearchdSavedData.get(sender.level());

            if (ResearchTeamUtil.getPermissionLevel(sender) >= 1) {
                if (payload.remove() == true) {
                    ResearchTeamUtil.getResearchTeam(sender).removeMember(payload.member());
                    savedData.setDirty();
                } else {
                    ResearchTeamUtil.getResearchTeam(sender).addInvite(payload.member());
                    savedData.setDirty();
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}