package eu.mshade.enderchest.redstone.protocol;

public abstract class RedstonePacket {

    private RedstonePacketType packetType;
    private int id;

    public RedstonePacket(RedstonePacketType packetType, int id) {
        this.packetType = packetType;
        this.id = id;
    }

    public RedstonePacketType getPacketType() {
        return packetType;
    }

    public int getId() {
        return id;
    }
}
