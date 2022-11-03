package eu.mshade.enderchest.axolotl

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer

class AxolotlChannelInitializer: ChannelInitializer<Channel>() {

    override fun initChannel(channel: Channel) {
        channel.pipeline().addLast(AxolotlPacketCodec())
        channel.pipeline().addLast(AxolotlChannelInboundHandlerAdapter())
    }
}