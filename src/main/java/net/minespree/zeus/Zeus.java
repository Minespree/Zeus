package net.minespree.zeus;

import com.google.common.collect.Maps;
import io.playpen.core.coordinator.CoordinatorMode;
import io.playpen.core.coordinator.PlayPen;
import io.playpen.core.coordinator.network.Network;
import io.playpen.core.plugin.AbstractPlugin;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minespree.zeus.discord.impl.StatCommands;
import net.minespree.zeus.discord.ZeusChannel;
import net.minespree.zeus.discord.command.CommandManager;
import net.minespree.zeus.playpen.listeners.PlayPenListener;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @since 02/09/2017
 */
@Log4j2
public class Zeus extends AbstractPlugin {

    @Getter
    private static Zeus instance;
    @Getter
    private IDiscordClient client;
    @Getter
    private CommandManager commandManager;
    @Getter
    private ScheduledExecutorService service;

    private Map<ZeusChannel, IChannel> channels = Maps.newHashMap();

    @Override
    public boolean onStart() {
        if (PlayPen.get().getCoordinatorMode() != CoordinatorMode.NETWORK) {
            log.fatal("Discord plugin can only be used from network coordinator");
            return false;
        }

        String apiKey = getConfig().getString("apiKey");

        if (apiKey == null) {
            log.fatal("Config has not been setup yet.");
            return false;
        }

        try {
            ClientBuilder builder = new ClientBuilder();
            builder.withToken(apiKey);

            client = builder.login();
            client.getDispatcher().registerListener(new ZeusListener());
        } catch (DiscordException e) {
            log.fatal(e);
            return false;
        }

        instance = this;

        service = Executors.newSingleThreadScheduledExecutor();

        // TODO: Schedule constant checks for usages, frozen servers, etc.

        commandManager = new CommandManager();
        client.getDispatcher().registerListener(commandManager);
        commandManager.registerCommands(StatCommands.class);

        return Network.get().getEventManager().registerListener(new PlayPenListener());
    }

    public void sendMessage(ZeusChannel channel, String text) {
        Optional.ofNullable(channels.getOrDefault(channel, null)).ifPresent(c -> {
            c.sendMessage(text);
        });
    }

    public IChannel getChannel(ZeusChannel channel) {
        return channels.getOrDefault(channel, null);
    }

    public class ZeusListener {

        @EventSubscriber
        public void onReady(ReadyEvent event) {
            IGuild guild = event.getClient().getGuildByID(286173774825652225L);

            channels.put(ZeusChannel.PLAYPEN_NOTIFS, guild.getChannelByID(353930042171981844L));
            channels.put(ZeusChannel.IMPORTANT_UPDATES, guild.getChannelByID(369267940765466624L));
        }
    }
}
