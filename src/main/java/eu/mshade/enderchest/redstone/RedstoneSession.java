package eu.mshade.enderchest.redstone;

import eu.mshade.enderchest.redstone.protocol.RedstonePacketIn;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketOut;
import eu.mshade.mwork.event.EventContainer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.CompletableFuture;

public class RedstoneSession extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private Redstone redstone;
    private EventContainer eventContainer = EventContainer.of()
            .putContainer(this);

    public RedstoneSession(Channel channel, Redstone redstone) {
        this.channel = channel;
        this.redstone = redstone;
        this.redstone.addRedstoneSession(this);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RedstonePacketIn) {
            RedstonePacketIn redstonePacketIn = (RedstonePacketIn) msg;
            CompletableFuture<RedstonePacketIn> awaitRedstonePacketIn = redstone.getAwaitRedstonePacketIn(redstonePacketIn.getId());
            if (awaitRedstonePacketIn != null) {
                awaitRedstonePacketIn.complete(redstonePacketIn);
            }else {
                redstone.getRedstonePacketEventBus().publish(redstonePacketIn, eventContainer);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.redstone.removeRedstoneSession(this);
    }

    public void sendPacket(RedstonePacketOut redstonePacketOut){
        channel.writeAndFlush(redstonePacketOut, channel.voidPromise());
    }
}
