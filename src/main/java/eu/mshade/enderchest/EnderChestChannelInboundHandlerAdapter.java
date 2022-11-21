package eu.mshade.enderchest;

import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.event.PlayerQuitEvent;
import eu.mshade.enderframe.protocol.MinecraftPacketIn;
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline;
import eu.mshade.enderman.packet.play.MinecraftPacketInKeepAlive;
import eu.mshade.enderman.packet.play.move.MinecraftPacketInPlayerGround;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EnderChestChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

    private final Channel channel;

    public EnderChestChannelInboundHandlerAdapter(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof MinecraftPacketIn minecraftPacketIn){
            if (!(minecraftPacketIn instanceof MinecraftPacketInKeepAlive || minecraftPacketIn instanceof MinecraftPacketInPlayerGround)){
                System.out.println(minecraftPacketIn);

            }
            MinecraftProtocolPipeline minecraftProtocolPipeline = MinecraftProtocolPipeline.get();
            minecraftProtocolPipeline.getProtocol(channel).getEventBus().publish(minecraftPacketIn);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        MinecraftProtocolPipeline minecraftProtocolPipeline = MinecraftProtocolPipeline.get();
        if (minecraftProtocolPipeline.getPlayer(channel) != null) {
            EnderFrame.get().getEnderFrameEventBus().publish(new PlayerQuitEvent(minecraftProtocolPipeline.getMinecraftSession(channel)));
        }
        minecraftProtocolPipeline.flush(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
