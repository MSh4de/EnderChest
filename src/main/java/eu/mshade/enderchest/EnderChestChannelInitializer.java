package eu.mshadeproduction.enderchest;

import eu.mshadeproduction.enderchest.protocol.VarIntFrameDecoder;
import eu.mshadeproduction.enderchest.protocol.VarIntLengthEncoder;
import eu.mshadeproduction.enderframe.EnderFrameBridge;
import eu.mshadeproduction.enderframe.protocol.PacketDecoder;
import eu.mshadeproduction.enderframe.protocol.PacketEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;


public class EnderChestChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("timeout", new ReadTimeoutHandler(/*server.getConfig().getReadTimeout()*/0,
                TimeUnit.MILLISECONDS));
        pipeline.addLast("frame_decoder", new VarIntFrameDecoder());
        pipeline.addLast("frame_encoder", new VarIntLengthEncoder());
        //pipeline.addLast("splitter", new PacketSplitter());
        pipeline.addLast("decoder", new PacketDecoder());
        //pipeline.addLast("prepender", new PacketPrepender());
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", new EnderFrameBridge(ch));
    }
}
