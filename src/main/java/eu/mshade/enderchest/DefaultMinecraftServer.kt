package eu.mshade.enderchest

import eu.mshade.enderchest.itemrule.ArmorItemRule
import eu.mshade.enderchest.world.block.*
import eu.mshade.enderframe.MinecraftServer
import eu.mshade.enderframe.entity.Entity
import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.gamerule.GameRuleRepository
import eu.mshade.enderframe.item.ItemRuleRepository
import eu.mshade.enderframe.protocol.MinecraftProtocolRepository
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.WorldRepository
import eu.mshade.enderframe.world.block.BlockBehaviorRepository
import eu.mshade.enderframe.world.block.TickableBlockRepository
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class DefaultMinecraftServer : MinecraftServer {

    private val players = ConcurrentLinkedQueue<Player>()
    private val entities = ConcurrentLinkedQueue<Entity>()
    private val minecraftProtocolRepository = MinecraftProtocolRepository()
    private val blockBehaviorRepository = BlockBehaviorRepository()
    private val gameRuleRepository = GameRuleRepository()
    private val itemRuleRepository = ItemRuleRepository()
    private val tickableBlockRepository = TickableBlockRepository(blockBehaviorRepository)

    init {
        blockBehaviorRepository.register(ButtonBlockBehavior())
        blockBehaviorRepository.register(LeverBlockBehavior())
        blockBehaviorRepository.register(WoodBlockBehavior())
        blockBehaviorRepository.register(SlabBlockBehavior())
        blockBehaviorRepository.register(StairsBlockBehavior())
        blockBehaviorRepository.register(VineBlockBehavior())
        blockBehaviorRepository.register(BedBlockBehavior())
        blockBehaviorRepository.register(ChestBlockBehavior())
        blockBehaviorRepository.register(RedstoneLampBlockBehavior())
        blockBehaviorRepository.register(RedstoneWireBlockBehavior())
        blockBehaviorRepository.register(RepeaterBlockBehavior())

        itemRuleRepository.register(ArmorItemRule())

    }

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
        return WorldRepository.getWorlds()
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

    override fun getBlockBehaviors(): BlockBehaviorRepository {
        return blockBehaviorRepository
    }

    override fun getGameRules(): GameRuleRepository {
        return gameRuleRepository
    }

    override fun getItemRules(): ItemRuleRepository {
        return itemRuleRepository
    }

    override fun getTickableBlocks(): TickableBlockRepository {
        return tickableBlockRepository
    }
}