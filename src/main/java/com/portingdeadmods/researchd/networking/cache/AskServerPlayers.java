package com.portingdeadmods.researchd.networking.cache;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AskServerPlayers implements CustomPacketPayload {
    public static final AskServerPlayers UNIT = new AskServerPlayers();
    private AskServerPlayers() {};
    
    public static final Type<AskServerPlayers> TYPE = new Type<>(Researchd.rl("ask_server_players"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AskServerPlayers> STREAM_CODEC = StreamCodec.unit(UNIT);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void ping(AskServerPlayers payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            List<GameProfile> profiles = new UniqueArray<>();
            GameProfileCache cache;
            if (context.player() instanceof ServerPlayer sp) {
                cache = sp.server.getProfileCache();
                if (cache != null) {
                    for (GameProfileCache.GameProfileInfo profile : cache.profilesByName.values()) {
                        profiles.add(profile.getProfile());
                    }
                }
                PacketDistributor.sendToPlayer(sp, new ReceiveServerPlayers(profiles));
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}