package eu.mshade.enderchest.listener.packet;

import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.BlockPlaceEvent;
import eu.mshade.enderframe.item.*;
import eu.mshade.enderframe.packetevent.MinecraftPacketBlockPlaceEvent;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.*;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftPacketBlockPlaceListener implements EventListener<MinecraftPacketBlockPlaceEvent> {

    //logger with logback
    private static final Logger LOGGER = LoggerFactory.getLogger(MinecraftPacketBlockPlaceListener.class);

    private final BlockRuleRepository blockRuleRepository = new BlockRuleRepository();

    public MinecraftPacketBlockPlaceListener() {
        this.blockRuleRepository.register(MaterialCategory.LOG, new LogBlockRule());
        this.blockRuleRepository.register(MaterialCategory.STAIRS, new StairsBlockRule());
        this.blockRuleRepository.register(MaterialCategory.BUTTON, new ButtonBlockRule());
        this.blockRuleRepository.register(MaterialCategory.LEVER, new LeverBlockRule());
        this.blockRuleRepository.register(MaterialCategory.SLAB, new SlabBlockRule());
        this.blockRuleRepository.register(MaterialCategory.VINE, new VineBlockRule());
    }

    @Override
    public void onEvent(MinecraftPacketBlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = player.getLocation().getWorld();
        BlockFace blockFace = event.getBlockFace();
        Vector blockPosition;
        ItemStack itemStack = event.getItemStack();
        MaterialKey material = itemStack.getMaterial();
        boolean isSneaking = player.isSneaking();

        if (material == null) return;


        if (material.equals(Material.AIR)){
            blockPosition = event.getBlockPosition();
            Block block = world.getBlock(blockPosition);

/*            MetadataKeyValueBucket metadataKeyValueBucket = block.getMetadataKeyValueBucket();

            MetadataKeyValue<?> metadataKeyValue = metadataKeyValueBucket.getMetadataKeyValue(BlockMetadataType.INSTANCE.getEXTRA());
            CompoundBinaryTag compoundBinaryTag;
            if (metadataKeyValue == null){
                compoundBinaryTag = new CompoundBinaryTag();
                metadataKeyValueBucket.setMetadataKeyValue(new ExtraBlockMetadata(compoundBinaryTag));
            } else {
                compoundBinaryTag = (CompoundBinaryTag) metadataKeyValue.getMetadataValue();
            }

            compoundBinaryTag.putInt("counter", compoundBinaryTag.getInt("counter") + 1);*/

            System.out.println(block.getMetadataKeyValueBucket().toPrettyString(0));
            return;
        }

        if (blockFace != BlockFace.NONE) {

            blockPosition = event.getBlockPosition().clone().add(blockFace.getVector());

            Block block = material.toBlock();

            BlockRule blockRule = this.blockRuleRepository.getBlockRule(block.getMaterialKey().getTag());
            if (blockRule != null) {
                block = blockRule.apply(player.getLocation(), blockPosition, blockFace, event.getCursorPosition(), block);
            }

            if (block == null) block = Material.AIR.toBlock();




            BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(player, block, blockPosition);
            EnderFrame.get().getMinecraftEvents().publish(blockPlaceEvent);

            if (blockPlaceEvent.isCancelled()){
                player.getLocation().getChunk().join().notify(agent -> {
                    if (agent instanceof Player target) {
                        target.getMinecraftSession().sendBlockChange(blockPosition, world.getBlock(blockPosition));
                    }
                });

                return;
            }
            world.setBlock(blockPosition, block);

            Block finalBlock = block;
            player.getLocation().getChunk().join().notify(agent -> {
                if (agent instanceof Player target) {
                    target.getMinecraftSession().sendBlockChange(blockPosition, finalBlock);
                }
            });


        }


    }
}
