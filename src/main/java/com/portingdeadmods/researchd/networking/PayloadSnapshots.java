package com.portingdeadmods.researchd.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class PayloadSnapshots {
    private PayloadSnapshots() {}

    /**
     * Copies live server state for a payload. Payloads are encoded later on the network thread, so one holding the
     * live object can be encoded while the server thread changes it, and fail with a ConcurrentModificationException.
     */
    public static <T> T copy(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, T value) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(), ServerLifecycleHooks.getCurrentServer().registryAccess());
        try {
            codec.encode(buf, value);
            return codec.decode(buf);
        } finally {
            buf.release();
        }
    }
}
