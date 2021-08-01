package eu.mshade.enderchest.redstone.protocol.packet;

import eu.mshade.enderchest.redstone.protocol.RedstonePacketOut;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketType;

public class RedstonePacketOutMotd extends RedstonePacketOut {

    public RedstonePacketOutMotd() {
        super(RedstonePacketType.MOTD);
    }
}
