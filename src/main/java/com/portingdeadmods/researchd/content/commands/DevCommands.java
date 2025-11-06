package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.ResearchInteractionType;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DevCommands {
    public static LiteralCommandNode<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("dev")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("recipes-dump")
                        .then(Commands.literal("results").then(Commands.argument("item", ItemArgument.item(context)).executes(ctx -> dumpRecipes(ctx, DumpRecipesMode.RESULTS))))
                        .then(Commands.literal("contains").then(Commands.argument("item", ItemArgument.item(context)).executes(ctx -> dumpRecipes(ctx, DumpRecipesMode.CONTAINS))))
                        .then(Commands.literal("all").then(Commands.argument("item", ItemArgument.item(context)).executes(ctx -> dumpRecipes(ctx, DumpRecipesMode.ALL))))
                )
                .then(Commands.literal("dimensions-dump")
                        .then(Commands.literal("current").executes(DevCommands::dumpCurrentDimension))
                        .then(Commands.literal("all").executes(DevCommands::dumpAllDimensions)))
                .then(Commands.literal("edit-mode")
                        .then(Commands.argument("enabled", BoolArgumentType.bool()).executes(DevCommands::enableEditMode)))
                .build();
    }

    private static int enableEditMode(CommandContext<CommandSourceStack> ctx) {
        boolean enabled = ctx.getArgument("enabled", boolean.class);
        ctx.getSource().getPlayer().setData(ResearchdAttachments.RESEARCH_INTERACTION_TYPE, enabled ? ResearchInteractionType.EDIT : ResearchInteractionType.DEFAULT);
        ctx.getSource().sendSuccess(() -> Component.literal("Set edit mode " + (enabled ? "Enabled" : "Disabled")), true);
        return 1;
    }

    private static int dumpCurrentDimension(CommandContext<CommandSourceStack> ctx) {
        Level level = ctx.getSource().getLevel();
        String dimensionId = level.dimension().location().toString();
        ctx.getSource().sendSystemMessage(Component.literal("Current Dimension: ")
                .append(Component.literal(dimensionId).withStyle(ChatFormatting.GREEN))
                .withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, dimensionId))
                        .withHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Component.literal("Click to copy dimension ID")))));
        return 1;
    }

    private static int dumpAllDimensions(CommandContext<CommandSourceStack> ctx) {
        List<ResourceLocation> levels = ctx.getSource().levels().stream().map(ResourceKey::location).toList();
        ctx.getSource().sendSystemMessage(Component.literal("Found ")
                .append(Component.literal(String.valueOf(levels.size())).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" Dimension" + (levels.size() == 1 ? "" : "s") + ":"))
                .withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, levels.toString()))
                        .withHoverEvent(
                                new HoverEvent(
                                        net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                        Component.literal("Click to copy all dimension IDs")))));
        for (ResourceLocation dimensionId : levels) {
            ctx.getSource().sendSystemMessage(Component.literal("- " + dimensionId).withStyle(ChatFormatting.GRAY)
                    .withStyle(Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, dimensionId.toString()))
                            .withHoverEvent(
                                    new HoverEvent(
                                            net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                            Component.literal("Click to copy dimension ID")))));
        }
        return 1;
    }

    private static int dumpRecipes(CommandContext<CommandSourceStack> ctx, DumpRecipesMode mode) {
        ItemInput item = ctx.getArgument("item", ItemInput.class);
        return findAndDisplayRecipes(ctx.getSource(), mode, item.getItem());
    }

    private static int findAndDisplayRecipes(CommandSourceStack source, DumpRecipesMode mode, Item item) {
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            return 0;
        }

        RecipeManager recipeManager = source.getLevel().getRecipeManager();
        List<RecipeHolder<?>> matchingRecipes = findRecipes(recipeManager, item, mode, source);

        if (matchingRecipes.isEmpty()) {
            source.sendSystemMessage(Component.literal("No recipes found for: ").append(item.getDefaultInstance().getHoverName()));
        } else {
            displayRecipes(source, player, matchingRecipes, item, source::sendSystemMessage, mode);
        }

        return 1;
    }

    private static List<RecipeHolder<?>> findRecipes(
            RecipeManager recipeManager,
            Item item,
            DumpRecipesMode mode,
            CommandSourceStack source) {

        Predicate<RecipeHolder<?>> filter = switch (mode) {
            case RESULTS -> recipeHolder -> {
                Recipe<?> recipe = recipeHolder.value();
                ItemStack result = recipe.getResultItem(source.getLevel().registryAccess());
                return !result.isEmpty() && result.is(item);
            };
            case CONTAINS -> recipeHolder -> {
                Recipe<?> recipe = recipeHolder.value();
                UniqueArray<ItemStack> ingredients = new UniqueArray<>();
                recipe.getIngredients().forEach(ingredient -> {
                    ingredients.addAll(ingredient.getItems());
                });

                for (ItemStack ingredient : ingredients) {
                    if (ingredient.is(item)) {
                        return true;
                    }
                }
                return false;
            };
            case ALL -> recipeHolder -> {
                Recipe<?> recipe = recipeHolder.value();
                ItemStack result = recipe.getResultItem(source.getLevel().registryAccess());

                boolean resultMatches = !result.isEmpty() && result.is(item);

                if (resultMatches) return true;

                UniqueArray<ItemStack> ingredients = new UniqueArray<>();
                recipe.getIngredients().forEach(ingredient -> {
                    ingredients.addAll(ingredient.getItems());
                });

                for (ItemStack ingredient : ingredients) {
                    if (ingredient.is(item)) {
                        return true;
                    }
                }
                return false;
            };
        };

        return recipeManager.getRecipes().stream()
                .filter(filter)
                .toList();
    }

    private static void displayRecipes(
            CommandSourceStack source,
            ServerPlayer player,
            List<RecipeHolder<?>> matchingRecipes,
            Item item,
            Consumer<Component> sendMessageFunc,
            DumpRecipesMode mode) {

        StringBuilder builder = new StringBuilder();

        String headerComment = switch (mode) {
            case RESULTS -> "/** All the recipes resulting in " + BuiltInRegistries.ITEM.getKey(item);
            case CONTAINS -> "/** All the recipes containing " + BuiltInRegistries.ITEM.getKey(item);
            default -> "/** All the recipes containing or requiring " + BuiltInRegistries.ITEM.getKey(item);
        };

        builder.append(headerComment).append("*/\n");
        builder.append("new AndResearchEffect(\n");
        builder.append("List.of(\n");

        for (RecipeHolder<?> recipeHolder : matchingRecipes) {
            builder.append("new RecipeUnlockEffect(ResourceLocation.parse(");
            String recipeId = recipeHolder.id().toString();
            builder.append('"').append(recipeId).append('"');
            builder.append(matchingRecipes.indexOf(recipeHolder) == matchingRecipes.size() - 1 ? "))\n" : ")),\n");
        }

        builder.append("))");

        String messageType = switch (mode) {
            case RESULTS -> " recipes for: ";
            case CONTAINS -> " recipes containing: ";
            default -> " recipes that result or contain: ";
        };

        MutableComponent recipeIdsComponent = Component.literal("")
                .append(Component.literal("Found "))
                .append(Component.literal("" + matchingRecipes.size()).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(messageType))
                .append(Component.literal(item.getDefaultInstance().getHoverName().getString()).withStyle(ChatFormatting.GREEN))
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, builder.toString()))
                        .withHoverEvent(
                                new HoverEvent(
                                        net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                        Component.literal("Click to copy recipes ID")))
                );

        sendMessageFunc.accept(recipeIdsComponent);
    }

    public enum DumpRecipesMode {
        RESULTS,
        CONTAINS,
        ALL,
    }
}
