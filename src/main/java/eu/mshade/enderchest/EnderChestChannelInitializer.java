package eu.mshade.enderchest;

import eu.mshade.enderframe.protocol.MinecraftPacketAccuracy;
import eu.mshade.enderframe.protocol.MinecraftPacketCodec;
import eu.mshade.enderframe.protocol.MinecraftProtocol;
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline;
import eu.mshade.enderframe.protocol.temp.TempMinecraftProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;


public class EnderChestChannelInitializer extends ChannelInitializer<Channel> {

    private final MinecraftProtocolPipeline minecraftProtocolPipeline = MinecraftProtocolPipeline.get();
    private final MinecraftProtocol tempMinecraftProtocol = TempMinecraftProtocol.Companion.getINSTANCE();
    @Override
    protected void initChannel(Channel ch) throws Exception {
        minecraftProtocolPipeline.setProtocol(ch, tempMinecraftProtocol);
        minecraftProtocolPipeline.setMinecraftSession(ch, tempMinecraftProtocol.getSessionWrapper(ch));
        ch.pipeline()
                //.addLast("legacy_ping", new LegacyPingHandler())
                .addLast("encryption", VoidChannelHandlerAdapter.INSTANCE)
                .addLast("accuracy", new MinecraftPacketAccuracy())
                .addLast("compression", VoidChannelHandlerAdapter.INSTANCE)
                .addLast("codecs", new MinecraftPacketCodec())
                .addLast("timeout", new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                //.addLast("readtimeout", new ReadTimeoutHandler(20))
                //.addLast("writeidletimeout", new IdleStateHandler(0, P, 0))
                .addLast("handler", new EnderChestChannelInboundHandlerAdapter(ch));
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
