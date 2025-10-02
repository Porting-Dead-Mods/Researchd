package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RequestToJoinPayload(UUID toJoin, boolean remove) implements CustomPacketPayload {
    public static final Type<RequestToJoinPayload> TYPE = new Type<>(Researchd.rl("request_to_join_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestToJoinPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            RequestToJoinPayload::toJoin,
            ByteBufCodecs.BOOL,
            RequestToJoinPayload::remove,
            RequestToJoinPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                MinecraftServer server = sp.getServer();
                ServerLevel level = server.overworld();
                Player teamMemberPlayer = level.getPlayerByUUID(this.toJoin());
                if (teamMemberPlayer != null) {
                    ResearchTeam team = ResearchTeamHelper.getTeamByMember(teamMemberPlayer);
                    if (this.remove()) {
                        team.getSocialManager().removeReceivedInvite(sp.getUUID());
                    } else {
                        team.getSocialManager().addSentInvite(sp.getUUID());
                    }
                    ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
                    ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
                    ResearchTeamHelper.refreshPlayerManagement(team, level);
                } else {
                    sp.sendSystemMessage(Component.literal("The player you're trying to join does not exist!").withStyle(ChatFormatting.RED));
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}