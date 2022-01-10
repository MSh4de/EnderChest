package eu.mshade.enderchest.emerald;

import eu.mshade.mwork.ParameterContainer;
import eu.mshade.shulker.Shulker;
import eu.mshade.shulker.protocol.ShulkerPacket;
import eu.mshade.shulker.protocol.ShulkerPacketContainer;
import io.netty.channel.*;

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
        if (msg instanceof ShulkerPacketContainer) {
            ShulkerPacketContainer<?> shulkerPacketContainer = (ShulkerPacketContainer) msg;
            shulker.getShulkerPacketEventBus().publish(shulkerPacketContainer, parameterContainer);
        }
    }

    public void sendPacket(ShulkerPacketContainer<?> shulkerPacketContainer){
        this.channel.writeAndFlush(shulkerPacketContainer, channel.voidPromise());
    }

    public void sendPacketAndClose(ShulkerPacketContainer<?> shulkerPacketContainer){
        if (isConnected())
            channel.writeAndFlush(shulkerPacketContainer).addListener(ChannelFutureListener.CLOSE);
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
