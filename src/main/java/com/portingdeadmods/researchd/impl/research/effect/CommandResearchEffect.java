package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.regex.Pattern;

/**
 * Executes commands when a research is unlocked/locked.
 */
public record CommandResearchEffect(String onUnlockCommand, String onLockCommand) implements ResearchEffect {
    public static final ResourceLocation ID = Researchd.rl("command");

    public static final String PLAYER_NAME_PLACEHOLDER = "{{RESEARCH_PLAYER_NAME}}";
    public static final String TEAM_NAME_PLACEHOLDER = "{{RESEARCH_TEAM_NAME}}";
    public static final String RESEARCH_ID_PLACEHOLDER = "{{RESEARCH_ID}}";
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{[A-Z_]+}}");

    private static final MapCodec<CommandResearchEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("on_unlock", "").forGetter(CommandResearchEffect::onUnlockCommand),
            Codec.STRING.optionalFieldOf("on_lock", "").forGetter(CommandResearchEffect::onLockCommand)
    ).apply(instance, CommandResearchEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, CommandResearchEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            CommandResearchEffect::onUnlockCommand,
            ByteBufCodecs.STRING_UTF8,
            CommandResearchEffect::onLockCommand,
            CommandResearchEffect::new
    );

    public static final ResearchEffectSerializer<CommandResearchEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);

    @Override
    public void onUnlock(Level level, ResearchTeam team, ResourceKey<Research> research) {
        this.execute(this.onUnlockCommand, level, team, research);
    }

    @Override
    public void onLock(Level level, ResearchTeam team, ResourceKey<Research> research) {
        this.execute(this.onLockCommand, level, team, research);
    }

    private void execute(String command, Level level, ResearchTeam team, ResourceKey<Research> research) {
        if (command.isBlank() || level.isClientSide()) return;

        MinecraftServer server = level.getServer();
        String parsed = command
                .replace(TEAM_NAME_PLACEHOLDER, team.getName())
                .replace(RESEARCH_ID_PLACEHOLDER, research.location().toString());
        CommandSourceStack source = server.createCommandSourceStack().withSuppressedOutput();
        if (parsed.contains(PLAYER_NAME_PLACEHOLDER)) {
            for (TeamMember member : team.getMembers()) {
                ServerPlayer player = server.getPlayerList().getPlayer(member.player());
                if (player != null) {
                    server.getCommands().performPrefixedCommand(source, parsed.replace(PLAYER_NAME_PLACEHOLDER, player.getGameProfile().getName()));
                }
            }
        } else {
            server.getCommands().performPrefixedCommand(source, parsed);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.COMMAND.get();
    }

    @Override
    public ResearchEffectSerializer<CommandResearchEffect> getSerializer() {
        return SERIALIZER;
    }
}
