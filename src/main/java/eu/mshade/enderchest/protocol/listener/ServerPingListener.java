package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.packetevent.ServerPingEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.enderframe.protocol.temp.packet.PacketOutPong;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;

public class ServerPingListener implements EventListener<ServerPingEvent> {

    @Override
    public void onEvent(ServerPingEvent event, ParameterContainer parameterContainer) {
        Channel channel = parameterContainer.getContainer(Channel.class);
        SessionWrapper sessionWrapper = ProtocolPipeline.get().getSessionWrapper(channel);
        sessionWrapper.sendPacket(new PacketOutPong(event.getPayload()));
    }
}
