package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.*;
import eu.mshade.enderframe.packetevent.MinecraftPacketBlockPlaceEvent;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.block.*;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketBlockPlaceListener implements EventListener<MinecraftPacketBlockPlaceEvent> {

    //logger with logback
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketBlockPlaceListener.class);

    private final BlockRuleRepository blockRuleRepository = new BlockRuleRepository();

    public PacketBlockPlaceListener() {
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


            System.out.println(world.getBlock(blockPosition).getMetadataKeyValueBucket().toPrettyString(0));
            return;
        }

        if (blockFace != BlockFace.NONE) {

            blockPosition = event.getBlockPosition().clone().add(blockFace.getVector());

            Block block = material.toBlock();

            BlockRule blockRule = this.blockRuleRepository.getBlockRule(block.getMaterialKey().getMaterialCategoryKey());
            if (blockRule != null) {
                block = blockRule.apply(player.getLocation(), blockPosition, blockFace, event.getCursorPosition(), block);
            }

            if (block == null) block = Material.AIR.toBlock();


/*            MetadataKeyValueBucket metadataKeyValueBucket = block.getMetadataKeyValueBucket();
            CompoundBinaryTag extra = new CompoundBinaryTag();
            extra.putString("bvn","Wesh l'equipe comment ça va ??");
            extra.putString("owner", player.getName());
            extra.putLong("timeSet", System.currentTimeMillis());

            metadataKeyValueBucket.setMetadataKeyValue(new ExtraBlockMetadata(extra));*/



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
