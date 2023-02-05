package eu.mshade.enderchest.axolotl

import eu.mshade.axolotl.protocol.AxolotlProtocolPipeline
import eu.mshade.axolotl.protocol.temp.TempAxolotlProtocol
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer

class AxolotlChannelInitializer: ChannelInitializer<Channel>() {

    private val axolotlProtocol = TempAxolotlProtocol

    override fun initChannel(channel: Channel) {

        AxolotlProtocolPipeline.protocolByChannel[channel] = axolotlProtocol
        AxolotlProtocolPipeline.axolotlSessionByChannel[channel] = axolotlProtocol.getAxolotlSession(channel)

        channel.pipeline().addLast(AxolotlPacketAccuracy())
        channel.pipeline().addLast(AxolotlPacketCodec())
        channel.pipeline().addLast(AxolotlChannelInboundHandlerAdapter())
    }
}