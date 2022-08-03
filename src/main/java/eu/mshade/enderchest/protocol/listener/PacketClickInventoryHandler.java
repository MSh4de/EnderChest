package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.inventory.*;
import eu.mshade.enderframe.item.ItemStack;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.packetevent.PacketClickInventoryEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class PacketClickInventoryHandler implements EventListener<PacketClickInventoryEvent> {

    @Override
    public void onEvent(PacketClickInventoryEvent event, ParameterContainer eventContainer) {
        Channel channel = eventContainer.getContainer(Channel.class);
        Player player = ProtocolPipeline.get().getPlayer(channel);
        SessionWrapper sessionWrapper = player.getSessionWrapper();

        Inventory inventory = event.getInventory();
        InventoryBufferStore inventoryBufferStore = player.getInventoryBufferStore();
        ClickType clickType = event.getClickType();
        int slot = event.getSlot();


        switch (clickType) {
            case LEFT -> {
                if (inventoryBufferStore.getPickedItemStack() == null) {
                    ItemStack pickedItemStack = inventory.getItemStack(slot);
                    inventoryBufferStore.setPickedItemStack(pickedItemStack);
                    inventory.deleteItemStack(slot);
                } else {
                    ItemStack itemStack = inventory.getItemStack(slot);
                    ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                    if (itemStack != null) {
                        if (itemStack.getMaterial().equals(pickedItemStack.getMaterial())) {
                            if (itemStack.getAmount() != 64 && pickedItemStack.getAmount() != 64) {
                                int totalAmount = itemStack.getAmount() + pickedItemStack.getAmount();
                                int remainAmount = totalAmount % 64;
                                if (totalAmount >= 64) {
                                    itemStack.setAmount(64);
                                    inventoryBufferStore.setPickedItemStack(pickedItemStack.clone().setAmount(remainAmount));
                                } else {
                                    itemStack.setAmount(totalAmount);
                                    inventoryBufferStore.setPickedItemStack(null);
                                }
                            }
                        } else {
                            inventoryBufferStore.setPickedItemStack(itemStack);
                            inventory.setItemStack(slot, pickedItemStack);
                        }
                    } else {
                        inventoryBufferStore.setPickedItemStack(null);
                        inventory.setItemStack(slot, pickedItemStack);
                    }
                }
            }
            case RIGHT -> {
                if (inventoryBufferStore.getPickedItemStack() == null) {
                    ItemStack itemStack = inventory.getItemStack(slot);
                    if (itemStack != null) {
                        int amountOfPickedIemStack = itemStack.getAmount() / 2;
                        int amountOfInventoryItem = amountOfPickedIemStack - (itemStack.getAmount() % 2);
                        itemStack.modifyAmount(integer -> amountOfInventoryItem);
                        inventoryBufferStore.setPickedItemStack(itemStack.clone().setAmount(amountOfPickedIemStack));
                    }
                } else {
                    ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                    pickedItemStack.modifyAmount(integer -> integer - 1);
                    if (pickedItemStack.getAmount() <= 0) {
                        inventoryBufferStore.setPickedItemStack(null);
                    }
                    ItemStack itemStack = inventory.getItemStack(slot);
                    if (itemStack == null) {
                        inventory.setItemStack(slot, pickedItemStack.clone()
                                .setAmount(1));
                    } else {
                        itemStack.modifyAmount(integer -> integer + 1);
                    }
                }
            }
            case DOUBLE_CLICK -> {
                if (inventoryBufferStore.getPickedItemStack() == null) return;
                ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                if (player.getOpenedInventory() != null) {
                    Inventory openedInventory = player.getOpenedInventory();
                    onDoubleClick(pickedItemStack, openedInventory);
                }

                if (pickedItemStack.getAmount() != 64) {
                    onDoubleClick(pickedItemStack, player.getPlayerInventory());
                }


            }
            case ADD_ITEM_FROM_DRAG ->
                    inventoryBufferStore.addPlacedItemStack(slot, inventory instanceof PlayerInventory);
            case START_DRAG_LEFT -> inventoryBufferStore.setLastClickType(ClickType.START_DRAG_LEFT);
            case END_DRAG_LEFT -> {
                if (inventoryBufferStore.getLastClickType() != ClickType.START_DRAG_LEFT) return;
                ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();

                int size = inventoryBufferStore.getPLacedItemStacks().size();
                int amountOfPickedIemStack = pickedItemStack.getAmount() % size;
                int amountOfInventoryItem = (pickedItemStack.getAmount() / size);
                pickedItemStack.setAmount(amountOfPickedIemStack);
                if (amountOfPickedIemStack == 0) inventoryBufferStore.setPickedItemStack(null);


                for (InventoryBufferStore.PlacedItemStack placedItemStack : inventoryBufferStore.getPLacedItemStacks()) {
                    Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getPlayerInventory() : player.getOpenedInventory());

                    ItemStack itemStack = inventoryTarget.getItemStack(placedItemStack.getSlot());
                    if (itemStack == null) {
                        inventoryTarget.setItemStack(placedItemStack.getSlot(), pickedItemStack
                                .clone()
                                .setAmount(amountOfInventoryItem));
                    } else {
                        itemStack.modifyAmount(integer -> integer + amountOfInventoryItem);
                    }

                }

                inventoryBufferStore.clearPLacedItemStacks();
            }
            case START_DRAG_RIGHT -> inventoryBufferStore.setLastClickType(ClickType.START_DRAG_RIGHT);
            case END_DRAG_RIGHT -> {
                if (inventoryBufferStore.getLastClickType() != ClickType.START_DRAG_RIGHT) return;
                ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                int size = inventoryBufferStore.getPLacedItemStacks().size();
                pickedItemStack.modifyAmount(integer -> integer - size);
                if (pickedItemStack.getAmount() <= 0) inventoryBufferStore.setPickedItemStack(null);

                for (InventoryBufferStore.PlacedItemStack placedItemStack : inventoryBufferStore.getPLacedItemStacks()) {
                    Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getPlayerInventory() : player.getOpenedInventory());

                    ItemStack itemStack = inventoryTarget.getItemStack(placedItemStack.getSlot());
                    if (itemStack == null) {
                        inventoryTarget.setItemStack(placedItemStack.getSlot(), pickedItemStack.clone()
                                .setAmount(1));
                    } else {
                        itemStack.modifyAmount(integer -> integer + 1);
                    }

                }

                inventoryBufferStore.clearPLacedItemStacks();
            }
            case START_DRAG_MIDDLE -> inventoryBufferStore.setLastClickType(ClickType.START_DRAG_MIDDLE);
            case END_DRAG_MIDDLE -> {
                if (inventoryBufferStore.getLastClickType() != ClickType.START_DRAG_MIDDLE) return;
                ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                inventoryBufferStore.setPickedItemStack(null);

                for (InventoryBufferStore.PlacedItemStack placedItemStack : inventoryBufferStore.getPLacedItemStacks()) {
                    Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getPlayerInventory() : player.getOpenedInventory());
                    ItemStack itemStack = inventoryTarget.getItemStack(placedItemStack.getSlot());
                    if (itemStack == null) {
                        inventoryTarget.setItemStack(placedItemStack.getSlot(), pickedItemStack.clone());
                    } else {
                        itemStack.setAmount(pickedItemStack.getAmount());
                    }
                }
                inventoryBufferStore.clearPLacedItemStacks();
            }
            case MIDDLE -> {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    ItemStack itemStack = inventory.getItemStack(slot);
                    if (itemStack != null && !itemStack.getMaterial().equals(Material.AIR)) {
                        inventoryBufferStore.setPickedItemStack(itemStack
                                .clone()
                                .setAmount(64));
                    }
                }
            }
            case NUMBER_KEY -> {
                int key = event.getKey();
                PlayerInventory playerInventory = player.getPlayerInventory();
                ItemStack itemStackFromHotBar = playerInventory.getItemStack(key);
                ItemStack itemStackFromInventory = inventory.getItemStack(slot);

                if (inventory instanceof PlayerInventory) {
                    inventory.setItemStack(key, itemStackFromInventory);
                    inventory.setItemStack(slot, itemStackFromHotBar);
                } else {
                    if (itemStackFromInventory == null) {
                        inventory.setItemStack(slot, itemStackFromHotBar);
                        playerInventory.deleteItemStack(key);
                    } else if (itemStackFromHotBar == null) {
                        playerInventory.setItemStack(key, itemStackFromInventory);
                        inventory.deleteItemStack(slot);
                    } else {

                        int firstEmptySlot = playerInventory.findFirstEmptySlot();
                        if (firstEmptySlot == -1) return;

                        inventory.deleteItemStack(event.getSlot());

                        moveItemStackIntoInventory(itemStackFromInventory, playerInventory);
/*                        if (itemStackFromHotBar.getMaterial().equals(itemStackFromInventory.getMaterial())) {
                            moveItemStackIntoInventory(itemStackFromInventory, playerInventory, inventory);
                        }else {
                            moveItemStackIntoInventory(itemStackFromInventory, playerInventory, inventory);
                            playerInventory.setItemStack(playerInventory.findFirstEmptySlot(), itemStackFromHotBar);
                        }*/
                    }
                }
            }
        }


    }

    private void moveItemStackIntoInventory(ItemStack itemStackFromInventory, PlayerInventory playerInventory){
        ItemStack copyItemStackFromInventory = itemStackFromInventory.clone();
        ItemStack foundItemStack;
        do {
            foundItemStack = playerInventory.findItemStack(itemStackFromInventory.getMaterial(), itemStack -> itemStack.getAmount() < 64);
            if (foundItemStack != null) {
                int remainAmount = 64 - foundItemStack.getAmount();
                int giveAmount = Math.min(remainAmount, copyItemStackFromInventory.getAmount());
                copyItemStackFromInventory.modifyAmount(integer -> integer - giveAmount);
                foundItemStack.modifyAmount(integer -> integer + giveAmount);
            }
        } while (foundItemStack != null && copyItemStackFromInventory.getAmount() != 0);

        if (copyItemStackFromInventory.getAmount() != 0) {
            playerInventory.setItemStack(playerInventory.findFirstEmptySlot(), copyItemStackFromInventory);
        }
    }


    private void onDoubleClick(ItemStack pickedItemStack, Inventory inventory) {

        List<InventoryBufferStore.PlacedItemStack> placedItemStacks = new ArrayList<>();

        for (int i = 0; i < inventory.getItemStacks().length; i++) {
            ItemStack itemStackTarget = inventory.getItemStack(i);
            if (itemStackTarget != null && itemStackTarget.getMaterial().equals(pickedItemStack.getMaterial()) && itemStackTarget.getAmount() != 64) {
                placedItemStacks.add(new InventoryBufferStore.PlacedItemStack(i));
            }
        }

        for (int i = 0; i < inventory.getItemStacks().length; i++) {
            ItemStack itemStackTarget = inventory.getItemStack(i);
            if (itemStackTarget != null && itemStackTarget.getMaterial().equals(pickedItemStack.getMaterial()) && itemStackTarget.getAmount() == 64) {
                placedItemStacks.add(new InventoryBufferStore.PlacedItemStack(i));
            }
        }

        for (InventoryBufferStore.PlacedItemStack placedItemStack : placedItemStacks) {
            int slot = placedItemStack.getSlot();
            ItemStack itemStack = inventory.getItemStack(slot);
            int remainAmount = 64 - pickedItemStack.getAmount();
            if (itemStack.getAmount() <= remainAmount) {
                pickedItemStack.modifyAmount(integer -> integer + itemStack.getAmount());
                inventory.deleteItemStack(slot);
            } else {
                itemStack.modifyAmount(integer -> integer - remainAmount);
                pickedItemStack.modifyAmount(integer -> integer + remainAmount);
            }

            if (pickedItemStack.getAmount() == 64) break;
        }

    }
}