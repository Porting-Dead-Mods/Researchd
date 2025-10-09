package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.compat.KubeJSCompat;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.compat.kubejs.example.KubeJSExample;
import com.portingdeadmods.researchd.resources.ExampleDatapack;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ExampleCommands {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("example")
                .then(Commands.literal("kubejs")
                        .requires(p -> KubeJSCompat.isKubeJSLoaded())
                        .executes(ExampleCommands::createKubeJSExample))
                .then(Commands.literal("datapack")
                        .executes(ExampleCommands::createDatapackExample)
                        // Optional "pack-name" param
                        .then(Commands.argument("pack-name", StringArgumentType.string())
                                .executes(ExampleCommands::createDatapackExample)
                        // Optional "pack-description" param
                        .then(Commands.argument("pack-description", StringArgumentType.string())
                                .executes(ExampleCommands::createDatapackExample))))
                .build();
    }


    private static int createDatapackExample(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String name = getArgOrDefault(ctx, "pack-name", String.class, "researchd_examples_pack");
        String description = getArgOrDefault(ctx, "pack-description", String.class, "Auto-created researchd example pack");
        Result<Path, Exception> result = ExampleDatapack.createExample(ctx.getSource().getServer().getWorldPath(LevelResource.DATAPACK_DIR), name, description);
        if (result instanceof Result.Ok(Path value)) {
            String filePath = value.toString();
            String gameDirPath = FMLPaths.GAMEDIR.get().toString();
            Researchd.LOGGER.debug("Game dir: {}, datapack: {}", gameDirPath, filePath);
            StringBuilder shortPath = new StringBuilder(filePath);
            shortPath.insert(0, "..");
            source.sendSuccess(() -> Component.literal("Successfully created example datapack at ")
                    .append(Component.literal(shortPath.toString()).withStyle(Style.EMPTY
                            .withColor(ChatFormatting.GOLD)
                            .withUnderlined(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Open directory")))))
                    .append(Component.literal(" - (run '/datapack enable \"file/%s\"' to enable pack".formatted(name)).withStyle(ChatFormatting.GRAY)), true);
            // Detects the datapack
            source.getServer().getPackRepository().reload();
            return 1;
        } else {
            Exception error = result.error();
            source.sendFailure(Component.literal("Failed to create example datapack: ").append(error.getMessage()).withStyle(ChatFormatting.RED));
        }
        return 0;
    }

    private static int createKubeJSExample(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (ResearchdCompatHandler.isKubeJSLoaded()) {
            Result<Path, Exception> result = KubeJSExample.createExample();
            if (result instanceof Result.Ok(Path value)) {
                String filePath = value.toString();
                String gameDirPath = FMLPaths.GAMEDIR.get().normalize().toAbsolutePath().toString();
                StringBuilder shortPath = new StringBuilder(filePath.substring(gameDirPath.length()));
                shortPath.insert(0, "...");
                source.sendSuccess(() -> Component.literal("Successfully created KubeJS example file at ")
                        .append(Component.literal(shortPath.toString()).withStyle(Style.EMPTY
                                .withColor(ChatFormatting.GOLD)
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath.substring(0, filePath.length() - "research_examples.js".length())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Open containing directory")))))
                        .append(Component.literal(" - (run '/reload' for the example to work)").withStyle(ChatFormatting.GRAY)), true);
                return 1;
            } else {
                Exception error = result.error();
                source.sendFailure(Component.literal("Failed to create KubeJS example file: ").append(error.getMessage()).withStyle(ChatFormatting.RED));
            }
        }
        return 0;
    }

    private static <T> T getArgOrDefault(CommandContext<CommandSourceStack> ctx, String name, Class<T> clazz, T defaultValue) {
        T value;
        try {
            value = ctx.getArgument(name, clazz);
        } catch (Exception e) {
            value = defaultValue;
        }
        return value;
    }

}
