import eu.mshade.enderframe.packetevent.PacketMoveAndLookEvent;
import eu.mshade.enderframe.packetevent.PacketMoveEvent;

public class ChainedTick {

    public static void main(String[] args) {

        System.out.println(PacketMoveEvent.class.isAssignableFrom(PacketMoveAndLookEvent.class));
        System.out.println(PacketMoveAndLookEvent.class.isAssignableFrom(PacketMoveEvent.class));
    }

}
