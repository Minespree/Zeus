package net.minespree.zeus.playpen.listeners;

import com.google.common.base.Joiner;
import io.playpen.core.coordinator.PlayPen;
import io.playpen.core.coordinator.network.INetworkListener;
import io.playpen.core.coordinator.network.LocalCoordinator;
import io.playpen.core.coordinator.network.Server;
import io.playpen.core.plugin.EventManager;
import io.playpen.core.plugin.IPlugin;
import net.minespree.zeus.Zeus;
import net.minespree.zeus.discord.ZeusChannel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @since 15/10/2017
 */
public class PlayPenListener implements INetworkListener {

    private Zeus zeus;

    public PlayPenListener() {
        zeus = Zeus.getInstance();
    }

    @Override
    public void onNetworkStartup() {
        zeus.getService().schedule(() -> {
            IChannel channel = zeus.getChannel(ZeusChannel.IMPORTANT_UPDATES);
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(new Color(15, 153, 0));
            builder.withTimestamp(LocalDateTime.now());
            builder.withTitle("NC Startup");
            builder.appendDescription("The Network Coordinator has finalized booting.");

            channel.sendMessage("@everyone", builder.build());
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onNetworkShutdown() {
        IChannel channel = zeus.getChannel(ZeusChannel.IMPORTANT_UPDATES);
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(new Color(221, 23, 26));
        builder.withTimestamp(LocalDateTime.now());
        builder.withTitle("NC Shutdown");
        builder.appendDescription("The Network Coordinator has shutdown.");

        channel.sendMessage("@everyone", builder.build());
    }

    @Override
    public void onCoordinatorCreated(LocalCoordinator localCoordinator) {
    }

    @Override
    public void onCoordinatorSync(LocalCoordinator localCoordinator) {
    }

    @Override
    public void onRequestProvision(LocalCoordinator localCoordinator, Server server) {
        zeus.sendMessage(ZeusChannel.PLAYPEN_NOTIFS, "Provisioning " + server.getP3().getId() + " (" + server.getP3().getVersion() + ") on coordinator " + localCoordinator.getName() + " as server " + server.getName());
    }

    @Override
    public void onProvisionResponse(LocalCoordinator localCoordinator, Server server, boolean b) {
        zeus.sendMessage(ZeusChannel.PLAYPEN_NOTIFS, server.getName() + " has " + (b ? "**successfuly** provisioned." : "**failed** to provision."));
    }

    @Override
    public void onRequestDeprovision(LocalCoordinator localCoordinator, Server server) {
        zeus.sendMessage(ZeusChannel.PLAYPEN_NOTIFS, "Deprovisioning server " + server.getName());
    }

    @Override
    public void onServerShutdown(LocalCoordinator localCoordinator, Server server) {
        zeus.sendMessage(ZeusChannel.PLAYPEN_NOTIFS, "Server " + server.getName() + " has shut down.");
    }

    @Override
    public void onRequestShutdown(LocalCoordinator localCoordinator) {
        zeus.sendMessage(ZeusChannel.PLAYPEN_NOTIFS, "Shutting down coordinator " + localCoordinator.getName());
    }

    @Override
    public void onPluginMessage(IPlugin plugin, String id, Object... args) {
//        if("log".equalsIgnoreCase(id)) {
//            String result = plugin.getSchema().getId() + ": " + Joiner.on(' ').join(args);
//            sendMessage(result);
//        }
    }

    @Override
    public void onListenerRegistered(EventManager<INetworkListener> eventManager) {
        // nop
    }

    @Override
    public void onListenerRemoved(EventManager<INetworkListener> eventManager) {
        // nop
    }
}
