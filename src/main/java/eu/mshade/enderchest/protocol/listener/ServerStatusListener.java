package eu.mshade.enderchest.listener;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.ServerListBuilder;
import eu.mshade.enderframe.ServerListPlayerBuilder;
import eu.mshade.enderframe.event.server.ServerStatusEvent;
import eu.mshade.enderframe.protocol.packet.PacketOutStatus;
import eu.mshade.mwork.dispatcher.DispatcherContainer;
import eu.mshade.mwork.dispatcher.DispatcherListener;

public class ServerStatusListener implements DispatcherListener<ServerStatusEvent> {

    @Override
    public void handle(ServerStatusEvent serverStatusEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameSessionHandler bridge = dispatcherContainer.getContainer(EnderFrameSessionHandler.class);

        ServerListBuilder serverListBuilder = ServerListBuilder.builder();
        serverListBuilder.setVersion("EnderChest 1.8.X", 47);

        serverListBuilder.setPlayer(20, 0,
                ServerListPlayerBuilder.of("Hello"),
                ServerListPlayerBuilder.of("ce"),
                ServerListPlayerBuilder.of("si"),
                ServerListPlayerBuilder.of("est"),
                ServerListPlayerBuilder.of("un"),
                ServerListPlayerBuilder.of("test")
        );


        bridge.sendPacket(new PacketOutStatus(serverListBuilder));
    }
}
