package com.elysianrealm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Map;

public class ElysianReputationCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("reputation")
                .then(Commands.literal("show")
                    .executes(context -> showReputation(context.getSource(), context.getSource().getPlayerOrException()))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> showReputation(context.getSource(), EntityArgument.getPlayer(context, "player")))
                    )
                )
                .then(Commands.literal("add")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("faction", StringArgumentType.word())
                            .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> modifyReputation(
                                    context.getSource(), 
                                    EntityArgument.getPlayers(context, "player"), 
                                    StringArgumentType.getString(context, "faction"), 
                                    IntegerArgumentType.getInteger(context, "amount"), 
                                    false
                                ))
                            )
                        )
                    )
                )
                .then(Commands.literal("set")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("faction", StringArgumentType.word())
                            .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> modifyReputation(
                                    context.getSource(), 
                                    EntityArgument.getPlayers(context, "player"), 
                                    StringArgumentType.getString(context, "faction"), 
                                    IntegerArgumentType.getInteger(context, "amount"), 
                                    true
                                ))
                            )
                        )
                    )
                )
        );
    }

    private static int showReputation(CommandSourceStack source, ServerPlayer player) {
        ElysianFactionSavedData data = ElysianFactionSavedData.get(source.getLevel());
        Map<String, Integer> reps = data.getPlayerReputations(player.getUUID());

        source.sendSuccess(() -> Component.literal("§6--- Reputation for " + player.getScoreboardName() + " ---"), false);
        if (reps.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No reputation history found (defaults to 0)."), false);
        } else {
            for (Map.Entry<String, Integer> entry : reps.entrySet()) {
                String faction = entry.getKey();
                int value = entry.getValue();
                String factionCapitalized = faction.substring(0, 1).toUpperCase() + faction.substring(1);
                String color = value >= 0 ? "§a" : "§c";
                source.sendSuccess(() -> Component.literal(factionCapitalized + ": " + color + value), false);
            }
        }
        return 1;
    }

    private static int modifyReputation(CommandSourceStack source, Collection<ServerPlayer> players, String faction, int amount, boolean set) {
        ElysianFactionSavedData data = ElysianFactionSavedData.get(source.getLevel());
        for (ServerPlayer player : players) {
            if (set) {
                data.setReputation(player.getUUID(), faction, amount);
                source.sendSuccess(() -> Component.literal("Set " + player.getScoreboardName() + "'s reputation with " + faction + " to " + amount), true);
            } else {
                data.addReputation(player.getUUID(), faction, amount);
                source.sendSuccess(() -> Component.literal("Added " + amount + " reputation with " + faction + " to " + player.getScoreboardName()), true);
            }
        }
        return players.size();
    }
}
