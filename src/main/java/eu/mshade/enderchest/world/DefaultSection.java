package eu.mshade.enderchest.world;

import eu.mshade.enderframe.UniqueId;
import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.metadata.MetadataKeyValueBucket;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.Dimension;
import eu.mshade.enderframe.world.chunk.NibbleArray;
import eu.mshade.enderframe.world.chunk.Palette;
import eu.mshade.enderframe.world.chunk.Section;

import java.util.Arrays;
import java.util.Objects;

public class DefaultSection extends Section {

    public DefaultSection(Chunk chunk, int y) {
        super(chunk, y);
        if (chunk.getWorld().getDimension() == Dimension.OVERWORLD) {
            skyLight.fill((byte) 15);
        }

        blockLight.fill((byte) 15);
        palette.setBlock(0, 4096, Material.AIR.toBlock());
    }

    public DefaultSection(Chunk chunk, int y, int realBlock, Palette palette, int[] blocks, UniqueId uniqueId, NibbleArray blockLight, NibbleArray skyLight) {
        super(chunk, y, palette, blocks, uniqueId, blockLight, skyLight);
        this.realBlock = realBlock;
    }


    @Override
    public String toString() {
        return "DefaultSectionBuffer{" +
                "chunkBuffer=" + chunk +
                ", y=" + y +
                ", realBlock=" + realBlock +
                '}';
    }


}
