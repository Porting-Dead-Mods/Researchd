package com.portingdeadmods.researchd.networking.cache;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.AllPlayersCache;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ReceiveServerPlayers(List<GameProfile> profiles) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, GameProfile> GAME_PROFILE_STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            GameProfile::getId,
            ByteBufCodecs.STRING_UTF8,
            GameProfile::getName,
            GameProfile::new
    );

    public static final Type<ReceiveServerPlayers> TYPE = new Type<>(Researchd.rl("receive_server_profiles"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ReceiveServerPlayers> STREAM_CODEC = StreamCodec.composite(
            GAME_PROFILE_STREAM_CODEC.apply(ByteBufCodecs.list()),
            ReceiveServerPlayers::profiles,
            ReceiveServerPlayers::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void pong(IPayloadContext context) {
        context.enqueueWork(() -> {
            for (GameProfile profile : profiles) {
                Minecraft.getInstance().getSkinManager().getOrLoad(profile).thenAccept(skin -> AllPlayersCache.add(profile.getId(), profile.getName(), skin));
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchFinishPayload", err);
            return null;
        });
    }
}

