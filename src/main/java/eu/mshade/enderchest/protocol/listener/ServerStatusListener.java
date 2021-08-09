package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.ServerListBuilder;
import eu.mshade.enderframe.ServerListPlayerBuilder;
import eu.mshade.enderframe.event.server.ServerStatusEvent;
import eu.mshade.enderframe.protocol.temp.packet.PacketOutStatus;
import eu.mshade.mwork.event.ParameterContainer;
import eu.mshade.mwork.event.EventListener;

public class ServerStatusListener implements EventListener<ServerStatusEvent> {
    /*
    @Override
    public void handle(ServerStatusEvent serverStatusEvent, DispatcherContainer dispatcherContainer) {
        EnderFrameSessionHandler bridge = dispatcherContainer.getContainer(EnderFrameSessionHandler.class);

        //System.out.println(ChatColor.BLACK+"Hello");

        ServerListBuilder serverListBuilder = ServerListBuilder.builder();
        serverListBuilder.setVersion("EnderChest 1.8.X §eMerci", 47);

        serverListBuilder.setPlayer(20, 0,
                ServerListPlayerBuilder.of("§4Hello"),
                ServerListPlayerBuilder.of("§cce"),
                ServerListPlayerBuilder.of("§6si"),
                ServerListPlayerBuilder.of("§eest"),
                ServerListPlayerBuilder.of("§2un"),
                ServerListPlayerBuilder.of("§atest")
        );


        bridge.sendPacket(new PacketOutStatus(serverListBuilder));
    }

     */

    @Override
    public void onEvent(ServerStatusEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);

        //System.out.println(ChatColor.BLACK+"Hello");

        ServerListBuilder serverListBuilder = ServerListBuilder.builder();
        serverListBuilder.setVersion("EnderChest 1.8.X §eMerci", 47);

        serverListBuilder.setPlayer(20, 0,
                ServerListPlayerBuilder.of("§4Hello"),
                ServerListPlayerBuilder.of("§cce"),
                ServerListPlayerBuilder.of("§6si"),
                ServerListPlayerBuilder.of("§eest"),
                ServerListPlayerBuilder.of("§2un"),
                ServerListPlayerBuilder.of("§atest")
        );


        enderFrameSessionHandler.sendPacket(new PacketOutStatus(serverListBuilder));
    }
}
