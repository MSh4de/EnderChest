package eu.mshade.enderchest.redstone;

import eu.mshade.enderchest.protocol.FramingHandler;
import eu.mshade.enderchest.protocol.VoidHandler;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketCodec;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.protocol.PacketCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class RedstoneChannelInitializer extends ChannelInitializer<Channel> {

    private Redstone redstone;

    public RedstoneChannelInitializer(Redstone redstone) {
        this.redstone = redstone;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(0, TimeUnit.MILLISECONDS))
                //.addLast("legacy_ping", new LegacyPingHandler())
                .addLast("codecs", new RedstonePacketCodec())
                .addLast("handler", new RedstoneSession(ch, redstone));
    }
}
