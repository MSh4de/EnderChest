package eu.mshade.enderchest.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;

@ChannelHandler.Sharable
public class VoidHandler extends ChannelHandlerAdapter {

    public static final VoidHandler INSTANCE = new VoidHandler();

    private VoidHandler() {
    }


}
