package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.packetevent.MinecraftPacketCloseInventoryEvent;
import eu.mshade.mwork.event.EventListener;

public class PacketCloseInventoryHandler implements EventListener<MinecraftPacketCloseInventoryEvent> {

    @Override
    public void onEvent(MinecraftPacketCloseInventoryEvent event) {
        Player player = event.getPlayer();

        player.getInventoryBufferStore().setPickedItemStack(null);

        //sessionWrapper.sendOpenInventory(player.getOpenedInventory());
        //sessionWrapper.sendItemStacks(player.getOpenedInventory());

        //sessionWrapper.sendItemStacks(player.getPlayerInventory());

    }
}
