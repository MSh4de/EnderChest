import eu.mshade.enderframe.packetevent.MinecraftPacketMoveAndLookEvent;
import eu.mshade.enderframe.packetevent.MinecraftPacketMoveEvent;

public class ChainedTick {

    public static void main(String[] args) {

        System.out.println(MinecraftPacketMoveEvent.class.isAssignableFrom(MinecraftPacketMoveAndLookEvent.class));
        System.out.println(MinecraftPacketMoveAndLookEvent.class.isAssignableFrom(MinecraftPacketMoveEvent.class));
    }

}
