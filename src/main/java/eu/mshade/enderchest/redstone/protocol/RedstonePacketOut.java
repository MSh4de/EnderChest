package eu.mshade.enderchest.redstone.protocol;

public abstract class RedstonePacketOut extends RedstonePacket{

    private static int ID = 0;

    public RedstonePacketOut(RedstonePacketType packetType) {
        super(packetType, ID++);
    }

}
