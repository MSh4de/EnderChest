package eu.mshade.enderchest.redstone.protocol.packet;

import eu.mshade.enderchest.redstone.protocol.RedstonePacketIn;
import eu.mshade.enderframe.motd.MotdComponent;

public class RedstonePacketInMotd extends RedstonePacketIn {

    private MotdComponent motdComponent;

    private RedstonePacketInMotd() {
    }

    public RedstonePacketInMotd(MotdComponent motdComponent) {
        this.motdComponent = motdComponent;
    }

    public MotdComponent getMotdComponent() {
        return motdComponent;
    }
}
