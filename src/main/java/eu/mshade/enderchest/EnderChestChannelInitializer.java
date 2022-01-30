package eu.mshade.enderchest;

import eu.mshade.enderchest.protocol.FramingHandler;
import eu.mshade.enderchest.protocol.VoidHandler;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.protocol.PacketCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;


public class EnderChestChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //.addLast("legacy_ping", new LegacyPingHandler())
                .addLast("encryption", VoidHandler.INSTANCE)
                .addLast("framing", new FramingHandler())
                .addLast("compression", VoidHandler.INSTANCE)
                .addLast("codecs", new PacketCodec())
                .addLast("readtimeout", new ReadTimeoutHandler(20))
                //.addLast("writeidletimeout", new IdleStateHandler(0, 15, 0))
                .addLast("handler", new EnderFrameSessionHandler(ch));
        /*
        pipeline.addLast("timeout", new ReadTimeoutHandler(0, TimeUnit.MILLISECONDS));
        pipeline.addLast("frame_decoder", new VarIntFrameDecoder());
        pipeline.addLast("frame_encoder", new VarIntLengthEncoder());
        //pipeline.addLast("splitter", new PacketSplitter());
        pipeline.addLast("decoder", new PacketDecoder());
        //pipeline.addLast("prepender", new PacketPrepender());
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", new EnderFrameSessionHandler(ch));

         */

    }
}
