package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.inventory.*;
import eu.mshade.enderframe.item.ItemStack;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.packetevent.PacketClickInventoryEvent;
import eu.mshade.mwork.event.EventListener;

import java.util.ArrayList;
import java.util.List;

public class PacketClickInventoryHandler implements EventListener<PacketClickInventoryEvent> {

    @Override
    public void onEvent(PacketClickInventoryEvent event) {
        Player player = event.getPlayer();

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
                            if (pickedItemStack.getAmount() != 64 || itemStack.getAmount() != 64) {
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
                    inventoryBufferStore.addPlacedItemStack(slot, inventory instanceof PlayerInventory);
            case START_DRAG_LEFT -> inventoryBufferStore.setLastClickType(ClickType.START_DRAG_LEFT);
            case END_DRAG_LEFT -> {
                if (inventoryBufferStore.getLastClickType() != ClickType.START_DRAG_LEFT) return;
                ItemStack pickedItemStack = inventoryBufferStore.getPickedItemStack();
                if (pickedItemStack == null) return;

                int size = inventoryBufferStore.getPLacedItemStacks().size();
                int amountOfPickedIemStack = pickedItemStack.getAmount() % size;
                int amountOfInventoryItem = (pickedItemStack.getAmount() / size);
                pickedItemStack.setAmount(amountOfPickedIemStack);
                if (amountOfPickedIemStack == 0) inventoryBufferStore.setPickedItemStack(null);


                for (InventoryBufferStore.PlacedItemStack placedItemStack : inventoryBufferStore.getPLacedItemStacks()) {
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
                    Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getInventory() : player.getOpenedInventory());

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

                if (inventoryBufferStore.getPLacedItemStacks().size() == 1) {
                    InventoryBufferStore.PlacedItemStack lastPlacedItemStack = inventoryBufferStore.getLastPlacedItemStack();
                    Inventory inventoryTarget = (lastPlacedItemStack.isPlayerInventory() ? player.getInventory() : player.getOpenedInventory());
                    if (inventoryTarget.equals(inventory)) {
                        ItemStack itemStack = inventoryTarget.getItemStack(lastPlacedItemStack.getSlot());
                        inventoryTarget.setItemStack(lastPlacedItemStack.getSlot(), pickedItemStack);
                        inventoryBufferStore.setPickedItemStack(itemStack);
                    }
                } else {

                    for (InventoryBufferStore.PlacedItemStack placedItemStack : inventoryBufferStore.getPLacedItemStacks()) {
                        Inventory inventoryTarget = (placedItemStack.isPlayerInventory() ? player.getInventory() : player.getOpenedInventory());
                        ItemStack itemStack = inventoryTarget.getItemStack(placedItemStack.getSlot());
                        if (itemStack == null) {
                            inventoryTarget.setItemStack(placedItemStack.getSlot(), pickedItemStack.clone());
                        } else {
                            itemStack.setAmount(pickedItemStack.getAmount());
                        }
                    }
                }
                inventoryBufferStore.clearPLacedItemStacks();
            }
            case MIDDLE -> {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    ItemStack itemStack = inventory.getItemStack(slot);
                    if (itemStack != null && !itemStack.getMaterial().equals(Material.AIR)) {
                        inventoryBufferStore.setLastPlacedItemStack(new InventoryBufferStore.PlacedItemStack(slot, inventory instanceof PlayerInventory));
                        inventoryBufferStore.setPickedItemStack(itemStack
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

        List<InventoryBufferStore.PlacedItemStack> placedItemStacks = new ArrayList<>();
        int size = (inventory instanceof PlayerInventory ? 4 * 9 : inventory.getItemStacks().length);

        for (int i = 0; i < size; i++) {
            ItemStack itemStackTarget = inventory.getItemStack(i);
            if (itemStackTarget != null && itemStackTarget.getMaterial().equals(pickedItemStack.getMaterial()) && itemStackTarget.getAmount() != pickedItemStack.getMaterial().getMaxStackSize()) {
                placedItemStacks.add(new InventoryBufferStore.PlacedItemStack(i));
            }
        }

        for (int i = 0; i < size; i++) {
            ItemStack itemStackTarget = inventory.getItemStack(i);
            if (itemStackTarget != null && itemStackTarget.getMaterial().equals(pickedItemStack.getMaterial()) && itemStackTarget.getAmount() == pickedItemStack.getMaterial().getMaxStackSize()) {
                placedItemStacks.add(new InventoryBufferStore.PlacedItemStack(i));
            }
        }

        for (InventoryBufferStore.PlacedItemStack placedItemStack : placedItemStacks) {
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