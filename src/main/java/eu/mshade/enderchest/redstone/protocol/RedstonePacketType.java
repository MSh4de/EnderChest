package eu.mshade.enderchest.redstone.protocol;

import eu.mshade.enderchest.redstone.protocol.packet.RedstonePacketInMotd;
import eu.mshade.enderchest.redstone.protocol.packet.RedstonePacketInUnknow;

public enum RedstonePacketType {

    UNKNOW(RedstonePacketInUnknow.class),
    MOTD(RedstonePacketInMotd.class);

    private Class<? extends RedstonePacketIn> redstonePacketInType;

    RedstonePacketType(Class<? extends RedstonePacketIn> redstonePacketInType) {
        this.redstonePacketInType = redstonePacketInType;
    }

    public Class<?> getRedstonePacketInType() {
        return redstonePacketInType;
    }
}
