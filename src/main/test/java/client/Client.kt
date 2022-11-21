package client

import eu.mshade.axolotl.protocol.AxolotlProtocolVersion
import eu.mshade.enderchest.axolotl.AxolotlPacketAccuracy
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel


fun main() {
    val workerGroup = NioEventLoopGroup()

    val bootstrap = Bootstrap()
    bootstrap.group(workerGroup)
    bootstrap.channel(NioSocketChannel::class.java)
    bootstrap.handler(
        object : ChannelInitializer<Channel>() {
            override fun initChannel(ch: Channel) {
                ch.pipeline().addLast(AxolotlPacketAccuracy())
                ch.pipeline().addLast(ClientPacketCodec())
                ch.pipeline().addLast(ClientChannelInboundHandlerAdapter())
            }
        },
    )

    val channel = bootstrap.connect("localhost", 25656).sync().channel()


    println("Connected to server $channel")
    channel.writeAndFlush(createPacketHandshake(AxolotlProtocolVersion.STONE))/*    workerGroup.scheduleAtFixedRate({


            *//*        val current = LocalDateTime.now()

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                val packet = CompoundBinaryTag()
                val textComponent = TextComponent.of("Hello World")
                textComponent.addExtras(TextComponent.of(" Welcome to protocol Axolotl $formatted")
                    .withColor(ChatColor.GOLD)
                    .withHoverEvent(TextHoverEvent.from("Hover me ! $formatted", TextHoverEventType.SHOW_TEXT)))

                packet.putBinaryTag("message", TextComponentMarshal.serialize(textComponent))
                packet.putString("position", TextPosition.HOT_BAR.name)
                packet.putString("player", "864c2e77-d6c9-3b61-be1c-6ab21cd21c58")
                packet.putInt("packetId", AxolotlPacketType.CHAT_MESSAGE_IN.getIdentifier())



                channel.writeAndFlush(packet)*//*
    }, 0, 50, TimeUnit.MILLISECONDS)*/


}


fun createPacketHandshake(axolotlPacketInHandshake: AxolotlProtocolVersion): BinaryTag<*> {
    val compoundBinaryTag = CompoundBinaryTag()
    compoundBinaryTag.putInt("version", axolotlPacketInHandshake.getVersion())
    compoundBinaryTag.putInt("packetId", 0x00)
    return compoundBinaryTag
}