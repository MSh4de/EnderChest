package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.commands.CommandContext;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.PlayerChatEvent;
import eu.mshade.enderframe.mojang.NamespacedKey;
import eu.mshade.enderframe.mojang.chat.ChatColor;
import eu.mshade.enderframe.packetevent.MinecraftPacketChatMessageEvent;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.event.EventListener;

import java.util.Arrays;

public class MinecraftPacketChatMessageListener implements EventListener<MinecraftPacketChatMessageEvent> {


    @Override
    public void onEvent(MinecraftPacketChatMessageEvent event) {
        Player player = event.getPlayer();
        MinecraftSession minecraftSession = player.getMinecraftSession();
        Location location = player.getLocation();

        var message = event.getMessage();
        if (message.startsWith("/")) {
            var commandManager = EnderFrame.get().getCommandManager();
            var args = message.substring(1).split(" ");
            var commandName = args[0];

            if (NamespacedKey.isValidFormat(commandName) && commandManager.getCommand(NamespacedKey.fromString(commandName)) != null) {
                commandManager.executeCommand(
                        NamespacedKey.fromString(commandName),
                        new CommandContext(
                                commandManager.getCommand(NamespacedKey.fromString(commandName)),
                                Arrays.copyOfRange(args, 1, args.length),
                                player
                        )
                );
            } else {
                var namespacedKeys = commandManager.getCommands().keySet();
                namespacedKeys.stream().filter(
                        key -> key.getKey().equals(commandName)
                ).findFirst().ifPresentOrElse(
                        key -> commandManager.executeCommand(
                                key,
                                new CommandContext(
                                        commandManager.getCommand(key),
                                        Arrays.copyOfRange(args, 1, args.length),
                                        player
                                )
                        ),
                        //TODO: configurable message
                        () -> minecraftSession.sendMessage(ChatColor.RED + "Unknown command")
                );
            }
            return;
        }

        EnderFrame.get().getMinecraftEvents().publishAsync(new PlayerChatEvent(player, event.getMessage()));
    }
}
