package eu.mshade.enderchest;

import eu.mshade.enderchest.protocol.PacketAccuracy;
import eu.mshade.enderchest.protocol.VoidHandler;
import eu.mshade.enderframe.protocol.PacketChannelInboundHandlerAdapter;
import eu.mshade.enderframe.protocol.PacketCodec;
import eu.mshade.enderframe.protocol.Protocol;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.temp.TempProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;


public class EnderChestChannelInitializer extends ChannelInitializer<Channel> {

    private final ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
    private final Protocol tempProtocol = TempProtocol.getInstance();
    @Override
    protected void initChannel(Channel ch) throws Exception {
        protocolPipeline.setProtocol(ch, tempProtocol);
        protocolPipeline.setSessionWrapper(ch, tempProtocol.getSessionWrapper(ch));
        ch.pipeline()
                //.addLast("legacy_ping", new LegacyPingHandler())
                .addLast("encryption", VoidHandler.INSTANCE)
                .addLast("accuracy", new PacketAccuracy())
                .addLast("compression", VoidHandler.INSTANCE)
                .addLast("codecs", new PacketCodec())
                .addLast("timeout", new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                //.addLast("readtimeout", new ReadTimeoutHandler(20))
                //.addLast("writeidletimeout", new IdleStateHandler(0, P, 0))
                .addLast("handler", new PacketChannelInboundHandlerAdapter(ch));
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
