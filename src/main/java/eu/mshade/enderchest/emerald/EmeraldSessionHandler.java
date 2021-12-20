package eu.mshade.enderchest.emerald;

import eu.mshade.mwork.ParameterContainer;
import eu.mshade.shulker.Shulker;
import eu.mshade.shulker.protocol.ShulkerPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EmeraldSessionHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private Shulker shulker;
    private ParameterContainer parameterContainer = ParameterContainer.of()
            .putContainer(this);

    public EmeraldSessionHandler(Channel channel, Shulker shulker) {
        this.channel = channel;
        this.shulker = shulker;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ShulkerPacket) {
            ShulkerPacket shulkerPacket = (ShulkerPacket) msg;
            shulker.getShulkerPacketEventBus().publish(shulkerPacket, parameterContainer);
        }
    }

    public void sendPacket(ShulkerPacket shulkerPacket){
        this.channel.writeAndFlush(shulkerPacket);
    }

    public void sendPacketAndClose(ShulkerPacket shulkerPacket){
        if (isConnected())
            channel.writeAndFlush(shulkerPacket).addListener(ChannelFutureListener.CLOSE);
    }

    public void close(){
        this.channel.close();
    }

    public boolean isConnected() {
        return channel.isActive();
    }


    public Channel getChannel() {
        return channel;
    }
}
