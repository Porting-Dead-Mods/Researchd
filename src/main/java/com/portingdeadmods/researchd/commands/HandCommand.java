package com.portingdeadmods.researchd.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HandCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("hand")
                // Lists recipes for the item 1 by 1
                .then(Commands.literal("onebyone")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player = source.getPlayer();

                            if (player != null) {
                                ItemStack heldItem = player.getMainHandItem();

                                if (heldItem.isEmpty()) {
                                    source.sendSystemMessage(Component.literal("You must be holding an item to use this command."));
                                    return 0;
                                }

                                RecipeManager recipeManager = source.getLevel().getRecipeManager();

                                // Find recipes containing the held item
                                List<RecipeHolder<?>> containingRecipes = findRecipes(recipeManager, heldItem, "allContains", false, source);

                                // Find recipes resulting in the held item
                                List<RecipeHolder<?>> resultingRecipes = findRecipes(recipeManager, heldItem, "allResults", false, source);

                                if (containingRecipes.isEmpty() && resultingRecipes.isEmpty()) {
                                    source.sendSystemMessage(Component.literal("No recipes found for: " + heldItem.getHoverName().getString()));
                                } else {
                                    // Display recipes containing the item
                                    if (!containingRecipes.isEmpty()) {
                                        source.sendSystemMessage(Component.literal("Found " + containingRecipes.size() + " recipes containing " +
                                                heldItem.getHoverName().getString() + ":").withStyle(ChatFormatting.GOLD));

                                        for (RecipeHolder<?> recipeHolder : containingRecipes) {
                                            Recipe<?> recipe = recipeHolder.value();
                                            String recipeId = recipeHolder.id().toString();
                                            String recipeType = recipe.getType().toString();

                                            MutableComponent recipeComponent = Component.literal("  - (" + recipeType + ") ")
                                                    .append(Component.literal(recipeId).withStyle(style -> style
                                                                .withColor(ChatFormatting.GREEN)
                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "new RecipePredicate(ResourceLocation.parse(" + recipeId + "))"))
                                                                .withHoverEvent(new HoverEvent(
                                                                        HoverEvent.Action.SHOW_TEXT,
                                                                        Component.literal("Click to copy recipe ID")))));

                                            source.sendSystemMessage(recipeComponent);
                                        }
                                    }

                                    // Display recipes resulting in the item
                                    if (!resultingRecipes.isEmpty()) {
                                        source.sendSystemMessage(Component.literal("Found " + resultingRecipes.size() + " recipes resulting " +
                                                heldItem.getHoverName().getString() + ":").withStyle(ChatFormatting.GOLD));

                                        for (RecipeHolder<?> recipeHolder : resultingRecipes) {
                                            Recipe<?> recipe = recipeHolder.value();
                                            String recipeId = recipeHolder.id().toString();
                                            String recipeType = recipe.getType().toString();

                                            MutableComponent recipeComponent = Component.literal("  - (" + recipeType + ") ")
                                                    .append(Component.literal(recipeId).withStyle(style -> style
                                                            .withColor(ChatFormatting.GREEN)
                                                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "new RecipePredicate(ResourceLocation.parse(" + recipeId + "))"))
                                                            .withHoverEvent(new HoverEvent(
                                                                    HoverEvent.Action.SHOW_TEXT,
                                                                    Component.literal("Click to copy recipe ID")))));

                                            source.sendSystemMessage(recipeComponent);
                                        }
                                    }
                                }
                            }

                            return 1;
                        })
                )

                // Returns a paste-able List.of() of all the recipes containing or resulting in the held item
                .then(Commands.literal("all")
                        .executes(context -> findAndDisplayRecipes(context.getSource(), "all", false))
                        .then(Commands.argument("strict", BoolArgumentType.bool())
                                .executes(context -> findAndDisplayRecipes(
                                        context.getSource(),
                                        "all",
                                        BoolArgumentType.getBool(context, "strict")
                                ))
                        )
                )

                // Returns a paste-able List.of() of all the recipes resulting in the held item
                .then(Commands.literal("allResults")
                        .executes(context -> findAndDisplayRecipes(context.getSource(), "allResults", false))
                        .then(Commands.argument("strict", BoolArgumentType.bool())
                                .executes(context -> findAndDisplayRecipes(
                                        context.getSource(),
                                        "allResults",
                                        BoolArgumentType.getBool(context, "strict")
                                ))
                        )
                )

                // Returns a paste-able List.of() of all recipes containing the held item
                .then(Commands.literal("allContains")
                        .executes(context -> findAndDisplayRecipes(context.getSource(), "allContains", false))
                        .then(Commands.argument("strict", BoolArgumentType.bool())
                                .executes(context -> findAndDisplayRecipes(
                                        context.getSource(),
                                        "allContains",
                                        BoolArgumentType.getBool(context, "strict")
                                ))
                        )
                )
                .build();
    }

    private static int findAndDisplayRecipes(CommandSourceStack source, String mode, boolean strict) {
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            return 0;
        }

        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty()) {
            source.sendSystemMessage(Component.literal("You must be holding an item to use this command."));
            return 0;
        }

        RecipeManager recipeManager = source.getLevel().getRecipeManager();
        List<RecipeHolder<?>> matchingRecipes = findRecipes(recipeManager, heldItem, mode, strict, source);

        if (matchingRecipes.isEmpty()) {
            source.sendSystemMessage(Component.literal("No recipes found for: " + heldItem.getHoverName().getString()));
        } else {
            displayRecipes(source, player, matchingRecipes, heldItem, mode, strict);
        }

        return 1;
    }

    private static List<RecipeHolder<?>> findRecipes(
            RecipeManager recipeManager,
            ItemStack heldItem,
            String mode,
            boolean strict,
            CommandSourceStack source) {

        Predicate<RecipeHolder<?>> filter;

        switch (mode) {
            case "allResults":
                filter = recipeHolder -> {
                    Recipe<?> recipe = recipeHolder.value();
                    ItemStack result = recipe.getResultItem(source.getLevel().registryAccess());
                    return !result.isEmpty() &&
                            (strict ? ItemStack.isSameItemSameComponents(result, heldItem) :
                                    ItemStack.isSameItem(result, heldItem));
                };
                break;

            case "allContains":
                filter = recipeHolder -> {
                    Recipe<?> recipe = recipeHolder.value();
                    UniqueArray<ItemStack> ingredients = new UniqueArray<>();
                    recipe.getIngredients().forEach(ingredient -> {
                        ingredients.addAll(ingredient.getItems());
                    });

                    for (ItemStack ingredient : ingredients) {
                        if (strict ? ItemStack.isSameItemSameComponents(ingredient, heldItem) :
                                ItemStack.isSameItem(ingredient, heldItem)) {
                            return true;
                        }
                    }
                    return false;
                };
                break;

            case "all":
            default:
                filter = recipeHolder -> {
                    Recipe<?> recipe = recipeHolder.value();
                    ItemStack result = recipe.getResultItem(source.getLevel().registryAccess());

                    boolean resultMatches = !result.isEmpty() &&
                            (strict ? ItemStack.isSameItemSameComponents(result, heldItem) :
                                    ItemStack.isSameItem(result, heldItem));

                    if (resultMatches) return true;

                    UniqueArray<ItemStack> ingredients = new UniqueArray<>();
                    recipe.getIngredients().forEach(ingredient -> {
                        ingredients.addAll(ingredient.getItems());
                    });

                    for (ItemStack ingredient : ingredients) {
                        if (strict ? ItemStack.isSameItemSameComponents(ingredient, heldItem) :
                                ItemStack.isSameItem(ingredient, heldItem)) {
                            return true;
                        }
                    }
                    return false;
                };
                break;
        }

        return recipeManager.getRecipes().stream()
                .filter(filter)
                .toList();
    }

    private static void displayRecipes(
            CommandSourceStack source,
            ServerPlayer player,
            List<RecipeHolder<?>> matchingRecipes,
            ItemStack heldItem,
            String mode,
            boolean strict) {

        StringBuilder builder = new StringBuilder();

        StringBuilder components = new StringBuilder(" containing the following components:\n");
	    for (DataComponentType<?> component : heldItem.getComponents().keySet()) {
		    Object value = heldItem.get(component);
		    components.append("* " + component + " -> " + value + "\n");
	    }

	    String headerComment = switch (mode) {
	        case "allResults" -> "/** All the recipes resulting in " + BuiltInRegistries.ITEM.getKey(heldItem.getItem()) + (strict ? components.toString() : "");
	        case "allContains" -> "/** All the recipes containing " + BuiltInRegistries.ITEM.getKey(heldItem.getItem()) + (strict ? components.toString() : "");
	        default -> "/** All the recipes containing or requiring " + BuiltInRegistries.ITEM.getKey(heldItem.getItem()) + (strict  ? components.toString() : "");
        };

	    builder.append(headerComment).append("*/\n");
        builder.append("List.of(\n");

        for (RecipeHolder<?> recipeHolder : matchingRecipes) {
            builder.append("new RecipePredicate(ResourceLocation.parse(");
            String recipeId = recipeHolder.id().toString();
            builder.append('"').append(recipeId).append('"');
            builder.append(matchingRecipes.indexOf(recipeHolder) == matchingRecipes.size() - 1 ? "))\n" : ")),\n");
        }

        builder.append(")");

        String messageType = switch (mode) {
	        case "allResults" -> " recipes for: ";
	        case "allContains" -> " recipes containing: ";
	        default -> " recipes that result or contain: ";
        };

	    MutableComponent recipeIdsComponent = Component.literal("")
                .append(Component.literal("Found "))
                .append(Component.literal("" + matchingRecipes.size()).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(messageType))
                .append(Component.literal(heldItem.getHoverName().getString()).withStyle(ChatFormatting.GREEN))
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, builder.toString()))
                        .withHoverEvent(
                                new HoverEvent(
                                        net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                        Component.literal("Click to copy recipe ID")))
                );

        player.sendSystemMessage(recipeIdsComponent);
    }
}