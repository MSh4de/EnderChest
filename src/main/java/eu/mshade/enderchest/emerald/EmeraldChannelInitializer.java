package eu.mshade.enderchest.emerald;

import eu.mshade.mwork.MWork;
import eu.mshade.shulker.Shulker;
import eu.mshade.shulker.protocol.ShulkerPacketAccuracy;
import eu.mshade.shulker.protocol.ShulkerPacketCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class EmeraldChannelInitializer extends ChannelInitializer<Channel> {

    private Shulker shulker;
    private MWork mWork;

    public EmeraldChannelInitializer(Shulker shulker, MWork mWork) {
        this.shulker = shulker;
        this.mWork = mWork;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(0, TimeUnit.MILLISECONDS))
                .addLast("accuracy", new ShulkerPacketAccuracy())
                .addLast("codecs", new ShulkerPacketCodec(mWork))
                .addLast("handler", new EmeraldSessionHandler(ch, shulker));

    }
}
