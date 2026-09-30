package com.portingdeadmods.researchd.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;

/** Server-side players for GameTests that need what only a {@link ServerPlayer} has. */
public final class TestPlayers {
    private TestPlayers() {}

    /**
     * A survival player with a connection that accepts vanilla packets, who never logs in. Logging in would sync
     * Researchd's payloads, which the fake connection never negotiated, so the player is not in the player list and
     * gets no default team: give them one with {@link TestTeams#create}.
     */
    public static ServerPlayer create(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        CommonListenerCookie cookie =
                CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation()) {
            @Override
            public GameType gameMode() {
                return GameType.SURVIVAL;
            }
        };
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        player.connection = new ServerGamePacketListenerImpl(level.getServer(), connection, player, cookie);
        return player;
    }
}
