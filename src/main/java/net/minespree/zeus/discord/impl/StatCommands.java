package net.minespree.zeus.discord.impl;

import io.playpen.core.coordinator.network.LocalCoordinator;
import io.playpen.core.coordinator.network.Network;
import net.minespree.zeus.discord.command.Command;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 15/10/2017
 */
public class StatCommands {

    @Command(command = "stats")
    public static void test(IMessage message) {
        IMessage reply = message.reply("Gathering data... :satellite:");

        EmbedBuilder builder = new EmbedBuilder();

        builder.withTitle("Coordinator(s) statistics...");
        builder.withDesc("Breakdown of Coordinator statistics (per coordinator)");
        builder.withColor(new Color(129, 63, 129));

        Map<String, Integer> totalResources = new HashMap<>();
        Map<String, Integer> usedResources = new HashMap<>();
        for (LocalCoordinator coord : Network.get().getCoordinators().values()) {
            if (!coord.isEnabled()) continue;
            Map<String, Integer> localResources = coord.getResources();
            Map<String, Integer> usedLocalResources = coord.getAvailableResources();
            StringBuilder result = new StringBuilder();
            for(Map.Entry<String, Integer> res : localResources.entrySet()) {
                totalResources.put(res.getKey(), totalResources.getOrDefault(res.getKey(), 0) + res.getValue());

                result.append("• ").append(res.getKey()).append(": ");
                if(usedLocalResources.containsKey(res.getKey())) {
                    Integer value = usedLocalResources.get(res.getKey());
                    value = res.getValue() - value;
                    result.append(value).append(" / ");
                    usedResources.put(res.getKey(), usedResources.getOrDefault(res.getKey(), 0) + value);
                } else {
                    result.append("? / ");
                }

                result.append(res.getValue()).append(" used.\n");
            }
            builder.appendField("> " + coord.getName(), result.toString(), true);
        }

        reply.edit(":satellite_orbital: Fetched statistics...", builder.build());

        builder = new EmbedBuilder();
        builder.withTitle("Total Resources...");
        builder.withDesc("Breakdown of total resources");
        builder.withColor(new Color(129, 63, 129));

        StringBuilder result = new StringBuilder();
        for(Map.Entry<String, Integer> res : totalResources.entrySet()) {
            result.append("• ").append(res.getKey()).append(": ");

            if(usedResources.containsKey(res.getKey())) {
                result.append(usedResources.get(res.getKey())).append(" / ");
            } else {
                result.append("? / ");
            }

            result.append(res.getValue()).append(" used.\n");
        }
        builder.appendField("> Usages", result.toString(), true);
        message.getChannel().sendMessage(builder.build());
    }

}
