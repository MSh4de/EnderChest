package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.PacketCloseInventoryEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PacketCloseInventoryHandler implements EventListener<PacketCloseInventoryEvent> {

    @Override
    public void onEvent(PacketCloseInventoryEvent event) {
        Player player = event.getPlayer();

        player.getInventoryBufferStore().setPickedItemStack(null);

        //sessionWrapper.sendOpenInventory(player.getOpenedInventory());
        //sessionWrapper.sendItemStacks(player.getOpenedInventory());

        //sessionWrapper.sendItemStacks(player.getPlayerInventory());

    }
}
