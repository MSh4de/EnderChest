package eu.mshade.enderchest.redstone.protocol;

public abstract class RedstonePacketIn extends RedstonePacket {


    public RedstonePacketIn() {
        super(RedstonePacketType.UNKNOW, 0);
    }
}
