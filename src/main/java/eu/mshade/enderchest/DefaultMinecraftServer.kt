package eu.mshade.enderchest

import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.entity.Entity
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.gamerule.GameRuleRepository
import eu.mshade.enderframe.protocol.MinecraftProtocolRepository
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.BlockRuleRepository
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class DefaultMinecraftServer: MinecraftServer {

    private val players = ConcurrentLinkedQueue<Player>()
    private val entities = ConcurrentLinkedQueue<Entity>()
    private val minecraftProtocolRepository = MinecraftProtocolRepository()
    private val blockRuleRepository = BlockRuleRepository()
    private val gameRuleRepository = GameRuleRepository()


    override fun addPlayer(player: Player) {
        players.add(player)
    }

    override fun removePlayer(player: Player) {
        players.remove(player)
    }

    override fun getPlayer(playerName: String): Player? {
        return players.firstOrNull { it.name == playerName }
    }

    override fun getPlayer(uniqueId: UUID): Player? {
        return players.firstOrNull { it.uniqueId == uniqueId }
    }

    override fun getOnlinePlayers(): Collection<Player> {
        return players
    }

    override fun getWorlds(): Collection<World> {
        TODO("Not yet implemented")
    }

    override fun getWorld(worldName: String): World? {
        TODO("Not yet implemented")
    }

    override fun getEntity(uniqueId: UUID): Entity? {
        return entities.firstOrNull { it.uniqueId == uniqueId }
    }

    override fun getEntities(): Collection<Entity> {
        return entities
    }

    override fun getMinecraftProtocols(): MinecraftProtocolRepository {
        return minecraftProtocolRepository
    }

    override fun getBlockRules(): BlockRuleRepository {
        return blockRuleRepository
    }

    override fun getGameRules(): GameRuleRepository {
        return gameRuleRepository
    }
}