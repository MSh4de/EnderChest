package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.inventory.ClickType;
import eu.mshade.enderframe.inventory.Inventory;
import eu.mshade.enderframe.inventory.InventoryBufferStore;
import eu.mshade.enderframe.item.ItemStack;
import eu.mshade.enderframe.packetevent.PacketClickInventoryEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class PacketClickInventoryHandler implements EventListener<PacketClickInventoryEvent> {

    @Override
    public void onEvent(PacketClickInventoryEvent event, ParameterContainer eventContainer) {
        Channel channel = eventContainer.getContainer(Channel.class);
        Player player = ProtocolPipeline.get().getPlayer(channel);
        SessionWrapper sessionWrapper = player.getSessionWrapper();

        Inventory inventory = event.getInventory();
        InventoryBufferStore inventoryBufferStore = player.getInventoryBufferStore();
        ClickType clickType = event.getClickType();

        switch (clickType) {
            case LEFT -> {
                if(inventoryBufferStore.getPickedItemStack() == null) {
                    ItemStack pickedItemStack = inventory.getItemStack(event.getSlot());
                    inventoryBufferStore.setPickedItemStack(pickedItemStack);
                    inventory.deleteItemStack(event.getSlot());
                }else {
                    ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                    inventory.setItemStack(event.getSlot(), pickedItemStack);
                    inventoryBufferStore.setPickedItemStack(null);
                    System.out.println("SET ITEM "+pickedItemStack+" in slot "+event.getSlot());
                }
            }
            case RIGHT -> {
                if (inventoryBufferStore.getPickedItemStack() == null) {
                    ItemStack itemStack = inventory.getItemStack(event.getSlot());
                    if (itemStack != null){
                        int amountOfPickedIemStack = itemStack.getAmount() / 2;
                        int amountOfInventoryItem = amountOfPickedIemStack - (itemStack.getAmount() % 2);
                        itemStack.modifyAmount(integer -> amountOfInventoryItem);
                        inventoryBufferStore.setPickedItemStack(itemStack.clone().setAmount(amountOfPickedIemStack));
                    }
                }else {
                    ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                    pickedItemStack.modifyAmount(integer -> integer-1);
                    if (pickedItemStack.getAmount() <= 0){
                        inventoryBufferStore.setPickedItemStack(null);
                    }
                    ItemStack itemStack = inventory.getItemStack(event.getSlot());
                    if (itemStack == null) {
                        inventory.setItemStack(event.getSlot(), pickedItemStack.clone()
                                .setAmount(1));
                    } else {
                        itemStack.modifyAmount(integer -> integer+1);
                    }
                }
            }
        }
        //System.out.println(event);
        //System.out.println(Arrays.toString(inventory.getItemStacks()));

        if (player.getGameMode() == GameMode.CREATIVE && clickType == ClickType.MIDDLE) {
        }


        //System.out.println(event);
    }

}
