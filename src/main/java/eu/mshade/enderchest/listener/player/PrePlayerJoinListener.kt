package eu.mshade.enderchest.listener.player

import eu.mshade.enderchest.EnderChest
import eu.mshade.enderchest.entity.DefaultPlayer
import eu.mshade.enderframe.*
import eu.mshade.enderframe.entity.Entity
import eu.mshade.enderframe.entity.EntityTracker
import eu.mshade.enderframe.entity.metadata.EntityMetadataKey
import eu.mshade.enderframe.entity.metadata.SkinPartEntityMetadata
import eu.mshade.enderframe.event.PlayerJoinEvent
import eu.mshade.enderframe.event.PrePlayerJoinEvent
import eu.mshade.enderframe.inventory.Inventory
import eu.mshade.enderframe.inventory.InventoryTracker
import eu.mshade.enderframe.mojang.SkinPart
import eu.mshade.enderframe.protocol.MinecraftByteBuf
import eu.mshade.enderframe.protocol.MinecraftProtocolPipeline
import eu.mshade.enderframe.protocol.packet.MinecraftPacketOutSpawnPosition
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.chunk.Chunk
import eu.mshade.enderman.packet.play.MinecraftPacketOutChangeGameState
import eu.mshade.mwork.event.EventListener
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutionException

class PrePlayerJoinListener : EventListener<PrePlayerJoinEvent> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PrePlayerJoinListener::class.java)
    }

    override fun onEvent(event: PrePlayerJoinEvent) {
        val minecraftProtocolPipeline = MinecraftProtocolPipeline.get()
        val minecraftSession = event.minecraftSession

        minecraftSession.sendCompression(256)
        minecraftSession.sendLoginSuccess()

        val world = MinecraftServer.getWorld("world")
        val location = Location(world, 7.5, 0.0, 7.5, -90.0f, 0.0f)
        var chunk: Chunk? = null
        var highest = 0

        try {
            chunk = location.chunk.get()
            highest = chunk.getHighest(location.blockX, location.blockZ)
        } catch (e: InterruptedException) {
            LOGGER.error("Error while getting chunk", e)
        } catch (e: ExecutionException) {
            LOGGER.error("Error while getting chunk", e)
        }

        location.setY((highest + 1).toDouble())
        val player = DefaultPlayer(location, Entity.ID.freeId, minecraftSession)

        player.inetSocketAddress = minecraftSession.remoteAddress
        player.gameMode = GameMode.SURVIVAL

        minecraftProtocolPipeline.setPlayer(minecraftSession.channel, player)
        minecraftSession.sendJoinGame(world!!, false)

        player.sendChunk(chunk!!)

        minecraftSession.sendPluginMessage("MC|Brand") { protocolBuffer: MinecraftByteBuf ->
            protocolBuffer.writeString(
                "Enderchest"
            )
        }
        minecraftSession.sendServerDifficulty(world.difficulty)
        minecraftSession.sendPacket(
            MinecraftPacketOutSpawnPosition(
                Vector(
                    location.blockX,
                    location.blockY,
                    location.blockZ
                )
            )
        )
        //default value of flying speed as 0.05
        minecraftSession.sendAbilities(false, false, true, false, 0.05f, 0.1f)
        minecraftSession.sendPacket(MinecraftPacketOutChangeGameState(3, player.gameMode.id.toFloat()))
        minecraftSession.teleport(location)
        MinecraftServer.addPlayer(player)

        player.metadata.setMetadataKeyValue(SkinPartEntityMetadata(SkinPart(true, true, true, true, true, true, true)))
        val listOfPlayer = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER)

        MinecraftServer.getPlayers().forEach{
            listOfPlayer.withPlayer(it)
        }
        minecraftSession.sendPlayerInfo(listOfPlayer)

        val ownPlayer = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER)
        ownPlayer.withPlayer(player)
        MinecraftServer.getPlayers().forEach {
            if (it !== player) {
                it.minecraftSession.sendPlayerInfo(ownPlayer)
            }
        }

        EntityTracker.track(player)

        minecraftSession.sendMetadata(player, EntityMetadataKey.SKIN_PART)
        world.putEntity(player)

        val playerInventory: Inventory = player.inventory!!
        InventoryTracker.add(playerInventory)
        player.joinTickBus(EnderChest.tickBus)

        LOGGER.info(String.format("%s join server", player.gameProfile!!.name))
        val playerJoinEvent = PlayerJoinEvent(player)
        MinecraftServer.getMinecraftEvent().publishAsync(playerJoinEvent)
    }
}