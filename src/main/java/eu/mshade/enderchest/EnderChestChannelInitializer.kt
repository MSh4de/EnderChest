package eu.mshade.enderchest

import eu.mshade.enderframe.protocol.MinecraftPacketAccuracy
import eu.mshade.enderframe.protocol.MinecraftPacketCodec
import eu.mshade.enderframe.protocol.MinecraftProtocol
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline
import eu.mshade.enderframe.protocol.temp.TempMinecraftProtocol.Companion.INSTANCE
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.timeout.ReadTimeoutHandler
import java.util.concurrent.TimeUnit

class EnderChestChannelInitializer : ChannelInitializer<Channel>() {

    private val minecraftProtocolPipeline = MinecraftProtocolPipeline.get()
    private val tempMinecraftProtocol: MinecraftProtocol = INSTANCE

    @Throws(Exception::class)
    override fun initChannel(ch: Channel) {
        minecraftProtocolPipeline.setProtocol(ch, tempMinecraftProtocol)
        minecraftProtocolPipeline.setMinecraftSession(ch, tempMinecraftProtocol.getMinecraftSession(ch))

        ch.pipeline()
            //.addLast("legacy_ping", new LegacyPingHandler())
            .addLast("encryption", VoidChannelHandlerAdapter)
            .addLast("accuracy", MinecraftPacketAccuracy())
            .addLast("compression", VoidChannelHandlerAdapter)
            .addLast("codecs", MinecraftPacketCodec())
            .addLast("timeout", ReadTimeoutHandler(30, TimeUnit.SECONDS))
            .addLast("handler", EnderChestChannelHandler(ch))
    }
}
