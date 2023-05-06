package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.inventory.*;
import eu.mshade.enderframe.item.ItemStack;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.packetevent.MinecraftPacketClickInventoryEvent;
import eu.mshade.mwork.event.EventListener;

import java.util.ArrayList;
import java.util.List;

public class MinecraftPacketClickInventoryListener implements EventListener<MinecraftPacketClickInventoryEvent> {

    @Override
    public void onEvent(MinecraftPacketClickInventoryEvent event) {
        Player player = event.getPlayer();

        Inventory inventory = event.getInventory();
        InventoryClickStore inventoryClickStore = player.getInventoryBufferStore();
        ClickType clickType = event.getClickType();
        int slot = event.getSlot();



        switch (clickType) {
            case LEFT -> {
                if (inventoryClickStore.getPickedItemStack() == null) {
                    ItemStack pickedItemStack = inventory.getItemStack(slot);
                    if (pickedItemStack != null) {
                        inventoryClickStore.setPickedItemStack(pickedItemStack);
                        inventory.deleteItemStack(slot);
                    }
                } else {
                    ItemStack itemStack = inventory.getItemStack(slot);
                    ItemStack pickedItemStack = inventoryClickStore.getPickedItemStack();
                    if (itemStack != null) {
                        if (itemStack.getMaterial().equals(pickedItemStack.getMaterial())) {
                            if (pickedItemStack.getAmount() != 64 || itemStack.getAmount() != 64) {
                                int totalAmount = itemStack.getAmount() + pickedItemStack.getAmount();
                                int remainAmount = totalAmount % 64;
                                if (totalAmount >= 64) {
                                    itemStack.setAmount(64);
                                    inventoryClickStore.setPickedItemStack(pickedItemStack.clone().setAmount(remainAmount));
                                } else {
                                    itemStack.setAmount(totalAmount);
                                    inventoryClickStore.setPickedItemStack(null);
                                }
                            }
                        } else {
                            inventoryClickStore.setPickedItemStack(itemStack);
                            inventory.setItemStack(slot, pickedItemStack);
                        }
                    } else {
                        inventoryClickStore.setPickedItemStack(null);
                        inventory.setItemStack(slot, pickedItemStack);
                    }
                }
            }
            case RIGHT -> {
                if (inventoryClickStore.getPickedItemStack() == null) {
                    ItemStack itemStack = inventory.getItemStack(slot);
                    if (itemStack != null) {
                        int amountOfPickedIemStack = itemStack.getAmount() / 2;
                        int amountOfInventoryItem = amountOfPickedIemStack - (itemStack.getAmount() % 2);
                        itemStack.modifyAmount(integer -> amountOfInventoryItem);
                        inventoryClickStore.setPickedItemStack(itemStack.clone().setAmount(amountOfPickedIemStack));
                    }
                } else {
                    ItemStack pickedItemStack = inventoryClickStore.getPickedItemStack();
                    pickedItemStack.modifyAmount(integer -> integer - 1);
                    if (pickedItemStack.getAmount() <= 0) {
                        inventoryClickStore.setPickedItemStack(null);
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
                if (inventoryClickStore.getPickedItemStack() == null) return;
                ItemStack pickedItemStack = inventoryClickStore.getPickedItemStack();
                int maxStackSize = pickedItemStack.getMaterial().getMaxStackSize();
                if (player.getOpenedInventory() != null) {
                    if (pickedItemStack.getAmount() == maxStackSize) return;
                    Inventory openedInventory = player.getOpenedInventory();
                    onDoubleClick(pickedItemStack, openedInventory);
                }

                if (pickedItemStack.getAmount() == maxStackSize) return;
                onDoubleClick(pickedItemStack, player.getInventory());

            }
            case ADD_ITEM_FROM_DRAG ->
                    inventoryClickStore.addPlacedItemStack(slot, inventory instanceof PlayerInventory);
            case START_DRAG_LEFT -> inventoryClickStore.setLastClickType(ClickType.START_DRAG_LEFT);
            case END_DRAG_LEFT -> {
                if (inventoryClickStore.getLastClickType() != ClickType.START_DRAG_LEFT) return;
                ItemStack pickedItemStack = inventoryClickStore.getPickedItemStack();
                if (pickedItemStack == null) return;

                int size = inventoryClickStore.getPLacedItemStacks().size();
                int amountOfPickedIemStack = pickedItemStack.getAmount() % size;
                int amountOfInventoryItem = (pickedItemStack.getAmount() / size);
                pickedItemStack.setAmount(amountOfPickedIemStack);
                if (amountOfPickedIemStack == 0) inventoryClickStore.setPickedItemStack(null);


                for (InventoryClickStore.PlacedItemStack placedItemStack : inventoryClickStore.getPLacedItemStacks()) {
                    Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getInventory() : player.getOpenedInventory());

                    ItemStack itemStack = inventoryTarget.getItemStack(placedItemStack.getSlot());
                    if (itemStack == null) {
                        inventoryTarget.setItemStack(placedItemStack.getSlot(), pickedItemStack
                                .clone()
                                .setAmount(amountOfInventoryItem));
                    } else {
                        itemStack.modifyAmount(integer -> integer + amountOfInventoryItem);
                    }

                }

                inventoryClickStore.clearPLacedItemStacks();
            }
            case START_DRAG_RIGHT -> inventoryClickStore.setLastClickType(ClickType.START_DRAG_RIGHT);
            case END_DRAG_RIGHT -> {
                if (inventoryClickStore.getLastClickType() != ClickType.START_DRAG_RIGHT) return;
                ItemStack pickedItemStack = inventoryClickStore.getPickedItemStack();
                int size = inventoryClickStore.getPLacedItemStacks().size();
                pickedItemStack.modifyAmount(integer -> integer - size);
                if (pickedItemStack.getAmount() <= 0) inventoryClickStore.setPickedItemStack(null);

                for (InventoryClickStore.PlacedItemStack placedItemStack : inventoryClickStore.getPLacedItemStacks()) {
                    Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getInventory() : player.getOpenedInventory());

                    ItemStack itemStack = inventoryTarget.getItemStack(placedItemStack.getSlot());
                    if (itemStack == null) {
                        inventoryTarget.setItemStack(placedItemStack.getSlot(), pickedItemStack.clone()
                                .setAmount(1));
                    } else {
                        itemStack.modifyAmount(integer -> integer + 1);
                    }

                }

                inventoryClickStore.clearPLacedItemStacks();
            }
            case START_DRAG_MIDDLE -> inventoryClickStore.setLastClickType(ClickType.START_DRAG_MIDDLE);
            case END_DRAG_MIDDLE -> {
                if (inventoryClickStore.getLastClickType() != ClickType.START_DRAG_MIDDLE) return;
                ItemStack pickedItemStack = inventoryClickStore.getPickedItemStack();
                inventoryClickStore.setPickedItemStack(null);

                if (inventoryClickStore.getPLacedItemStacks().size() == 1) {
                    InventoryClickStore.PlacedItemStack lastPlacedItemStack = inventoryClickStore.getLastPlacedItemStack();
                    Inventory inventoryTarget = (lastPlacedItemStack.isPlayerInventory() ? player.getInventory() : player.getOpenedInventory());
                    if (inventoryTarget.equals(inventory)) {
                        ItemStack itemStack = inventoryTarget.getItemStack(lastPlacedItemStack.getSlot());
                        inventoryTarget.setItemStack(lastPlacedItemStack.getSlot(), pickedItemStack);
                        inventoryClickStore.setPickedItemStack(itemStack);
                    }
                } else {
                    for (InventoryClickStore.PlacedItemStack placedItemStack : inventoryClickStore.getPLacedItemStacks()) {
                        Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getInventory() : player.getOpenedInventory());
                        ItemStack itemStack = inventoryTarget.getItemStack(placedItemStack.getSlot());
                        if (itemStack == null) {
                            inventoryTarget.setItemStack(placedItemStack.getSlot(), pickedItemStack.clone());
                        } else {
                            itemStack.setAmount(pickedItemStack.getAmount());
                        }
                    }
                }
                inventoryClickStore.clearPLacedItemStacks();
            }
            case MIDDLE -> {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    ItemStack itemStack = inventory.getItemStack(slot);
                    if (itemStack != null && !itemStack.getMaterial().equals(Material.AIR)) {
                        inventoryClickStore.setLastPlacedItemStack(new InventoryClickStore.PlacedItemStack(slot, inventory instanceof PlayerInventory));
                        inventoryClickStore.setPickedItemStack(itemStack
                                .clone()
                                .setAmount(itemStack.getMaterial().getMaxStackSize()));
                    }
                }
            }
            case NUMBER_KEY -> {
                int key = event.getKey();
                PlayerInventory playerInventory = player.getInventory();
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

                        if (itemStackFromHotBar.getMaterial().equals(itemStackFromInventory.getMaterial())) {
                            moveItemStackIntoInventory(key, itemStackFromInventory, playerInventory);
                        } else {
                            playerInventory.setItemStack(key, itemStackFromInventory);
                            moveItemStackIntoInventory(0, itemStackFromHotBar, playerInventory);
                        }
                    }
                }
            }
            case SHIFT_LEFT, SHIFT_RIGHT -> {
                ItemStack itemStack = inventory.getItemStack(slot);
                if (itemStack == null) return;
                inventory.deleteItemStack(slot);
                moveItemStack(itemStack, inventory, player);
            }
            case CREATIVE -> {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    inventory.setItemStack(slot, event.getItemStack());
                }
            }
        }


    }

    private void moveItemStackIntoInventory(int start, ItemStack itemStack, PlayerInventory playerInventory) {
        ItemStack copyItemStack = itemStack.clone();
        ItemStack foundItemStack;

        for (int i = start; i < 9 * 4; i++) {
            foundItemStack = playerInventory.getItemStack(i);
            dispatchItemIntoOtherItem(copyItemStack, foundItemStack);
        }

        if (copyItemStack.getAmount() != 0) {
            int firstEmptySlot = playerInventory.findFirstEmptySlot(start);
            playerInventory.setItemStack(firstEmptySlot, copyItemStack);
        }
    }

    private void moveItemStack(ItemStack itemStack, Inventory inventory, Player player) {
        ItemStack copyItemStack = itemStack.clone();
        if (inventory instanceof PlayerInventory) {
            for (int i = 0; i < player.getOpenedInventory().getItemStacks().length; i++) {
                ItemStack foundItemStack = player.getOpenedInventory().getItemStack(i);
                dispatchItemIntoOtherItem(copyItemStack, foundItemStack);
            }

            if (copyItemStack.getAmount() != 0) {
                int firstEmptySlot = player.getOpenedInventory().findFirstEmptySlot();
                player.getOpenedInventory().setItemStack(firstEmptySlot, copyItemStack);
            }
        } else {
            int length = player.getInventory().getItemStacks().length - 1;
            for (int i = length; i > 9; i--) {
                ItemStack foundItemStack = player.getInventory().getItemStacks()[i];
                dispatchItemIntoOtherItem(copyItemStack, foundItemStack);
            }

            if (copyItemStack.getAmount() != 0) {
                int firstEmptySlot = findFirstEmptySlotRawInventory(player.getInventory());
                player.getInventory().getItemStacks()[firstEmptySlot] = copyItemStack;
            }

        }
    }

    private void dispatchItemIntoOtherItem(ItemStack copyItemStack, ItemStack foundItemStack) {
        if (foundItemStack == null || !foundItemStack.getMaterial().equals(copyItemStack.getMaterial())) return;
        int freeSpace = foundItemStack.getMaterial().getMaxStackSize() - foundItemStack.getAmount();
        int giveAmount = Math.min(freeSpace, copyItemStack.getAmount());
        copyItemStack.modifyAmount(integer -> integer - giveAmount);
        foundItemStack.modifyAmount(integer -> integer + giveAmount);
    }

    private int findFirstEmptySlotRawInventory(PlayerInventory playerInventory) {
        ItemStack[] itemStacks = playerInventory.getItemStacks();
        int length = itemStacks.length - 1;
        for (int i = length; i > 9; i--) {
            if (itemStacks[i] == null) return i;
        }
        return -1;
    }


    private void onDoubleClick(ItemStack pickedItemStack, Inventory inventory) {

        List<InventoryClickStore.PlacedItemStack> placedItemStacks = new ArrayList<>();
        int size = (inventory instanceof PlayerInventory ? 4 * 9 : inventory.getItemStacks().length);

        for (int i = 0; i < size; i++) {
            ItemStack itemStackTarget = inventory.getItemStack(i);
            if (itemStackTarget != null && itemStackTarget.getMaterial().equals(pickedItemStack.getMaterial()) && itemStackTarget.getAmount() != pickedItemStack.getMaterial().getMaxStackSize()) {
                placedItemStacks.add(new InventoryClickStore.PlacedItemStack(i));
            }
        }

        for (int i = 0; i < size; i++) {
            ItemStack itemStackTarget = inventory.getItemStack(i);
            if (itemStackTarget != null && itemStackTarget.getMaterial().equals(pickedItemStack.getMaterial()) && itemStackTarget.getAmount() == pickedItemStack.getMaterial().getMaxStackSize()) {
                placedItemStacks.add(new InventoryClickStore.PlacedItemStack(i));
            }
        }

        for (InventoryClickStore.PlacedItemStack placedItemStack : placedItemStacks) {
            int slot = placedItemStack.getSlot();
            ItemStack itemStack = inventory.getItemStack(slot);
            int remainAmount = pickedItemStack.getMaterial().getMaxStackSize() - pickedItemStack.getAmount();
            if (itemStack.getAmount() <= remainAmount) {
                pickedItemStack.modifyAmount(integer -> integer + itemStack.getAmount());
                inventory.deleteItemStack(slot);
            } else {
                itemStack.modifyAmount(integer -> integer - remainAmount);
                pickedItemStack.modifyAmount(integer -> integer + remainAmount);
            }

            if (pickedItemStack.getAmount() == pickedItemStack.getMaterial().getMaxStackSize()) break;
        }

    }
}