import eu.mshade.enderframe.inventory.PlayerInventory;
import eu.mshade.enderframe.packetevent.MinecraftPacketMoveAndLookEvent;
import eu.mshade.enderframe.packetevent.MinecraftPacketMoveEvent;
import eu.mshade.enderframe.world.chunk.Chunk;

public class ChainedTick {

    public static void main(String[] args) {
        for (int i = 0; i < 46; i++) {
            int accurateSlot = PlayerInventory.accurateSlot(i);
            int index = PlayerInventory.indexFromAccurateSlot(accurateSlot);
            if (index != i) {
                System.out.println("realslot: " + i + " to " + accurateSlot);
            }
        }
        System.out.println(PlayerInventory.indexFromAccurateSlot(36));


        System.out.println(Chunk.key(100, 100));

    }

}
