package eu.mshade.enderchest;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;

@ChannelHandler.Sharable
public class VoidChannelHandlerAdapter extends ChannelHandlerAdapter {

    public static final VoidChannelHandlerAdapter INSTANCE = new VoidChannelHandlerAdapter();

}
