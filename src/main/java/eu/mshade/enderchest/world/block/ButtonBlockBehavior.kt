package eu.mshade.enderchest.world.block

import eu.mshade.enderframe.entity.Player
import eu.mshade.enderframe.item.MaterialKey
import eu.mshade.enderframe.world.Vector
import eu.mshade.enderframe.world.World
import eu.mshade.enderframe.world.block.*
import eu.mshade.enderframe.world.block.redstone.Redstone
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ButtonBlockBehavior : BlockBehavior {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ButtonBlockBehavior::class.java)
    }

    override fun place(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        material: MaterialKey
    ): Collection<Pair<Vector, Block>> {

        val blocks = mutableListOf<Pair<Vector, Block>>()
        val block = material.toBlock()
        val metadataKeyValueBucket = block.getMetadatas()
        metadataKeyValueBucket.setMetadataKeyValue(FaceBlockMetadata(blockFace))
        metadataKeyValueBucket.setMetadataKeyValue(PowerBlockMetadata(false))
        metadataKeyValueBucket.setMetadataKeyValue(TickableBlockMetadata(true))
        metadataKeyValueBucket.setMetadataKeyValue(RedstoneStateBlockMetadata(RedstoneState.EMITTING))

        blocks.add(Pair(blockPosition, block))

        return blocks
    }

    override fun interact(
        player: Player,
        blockPosition: Vector,
        blockFace: BlockFace,
        cursorPosition: Vector,
        block: Block,
        tickableBlocks: TickableBlockRepository
    ) {

        val location = player.getLocation()
        val world = location.world
        val chunk = location.chunk.join() ?: return

        val metadatas = block.getMetadatas()
        val powered = block.hasPower()

        if (powered) {
            return
        }

        metadatas.setMetadataKeyValue(PowerBlockMetadata(true))

        val blockUpdates = Redstone.spreadPower(blockPosition, block.getFace())
        val applySpreadPower = Redstone.filterSpreadPower(blockUpdates, world)


        world.setBlock(blockPosition, block)

        chunk.notify(Player::class.java) {
            it.minecraftSession.sendBlockChange(blockPosition, block)
        }

        tickableBlocks.join(world, blockPosition)

        applySpreadPower.forEach { (targetPosition, targetBlock) ->
            tickableBlocks.join(world, targetPosition, blockPosition)
        }

    }

    override fun updateBlock(
        world: World,
        position: Vector,
        previousPosition: Vector,
        tickableBlocks: TickableBlockRepository
    ) {

        val block = world.getBlock(position)

        val metadatas = block.getMetadatas()
        val powered = block.hasPower()
        val tick = block.getTick()


        if (!powered) return

        block.modifyTick { it + 1 }

        if (tick < 20) {
            world.setBlock(position, block)
            tickableBlocks.join(world, position)
            return
        }


        metadatas.setMetadataKeyValue(PowerBlockMetadata(false))
        block.resetTick()

        val chunk = world.getChunk(position.blockX shr 4, position.blockZ shr 4)?.join() ?: return


        world.setBlock(position, block)

        chunk.notify(Player::class.java) {
            it.minecraftSession.sendBlockChange(position, block)
        }

        val blockUpdates = Redstone.spreadPower(position, block.getFace())
        val applySpreadPower = Redstone.filterSpreadPower(blockUpdates, world)

        applySpreadPower.forEach { (targetPosition, targetBlock) ->

            tickableBlocks.join(world, targetPosition, position)

        }


    }

    override fun canApply(material: MaterialKey): Boolean {
        return material.namespacedKey.key.endsWith("button")
    }


}