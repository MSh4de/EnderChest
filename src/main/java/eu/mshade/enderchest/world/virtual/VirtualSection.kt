package eu.mshade.enderchest.world.virtual

import eu.mshade.enderframe.UniqueId
import eu.mshade.enderframe.item.Material
import eu.mshade.enderframe.world.Dimension
import eu.mshade.enderframe.world.chunk.NibbleArray
import eu.mshade.enderframe.world.chunk.Palette
import eu.mshade.enderframe.world.chunk.Section

class VirtualSection: Section {

    var virtualSectionStatus = VirtualSectionStatus.SAME

    constructor(virtualChunk: VirtualChunk, y: Int): super(virtualChunk, y){
        if (chunk.world.dimension == Dimension.OVERWORLD) {
            skyLight.fill(15.toByte())
        }

        blockLight.fill(15.toByte())
        palette.setBlock(0, 4096, Material.AIR.toBlock())
    }

    constructor(virtualChunk: VirtualChunk, y: Int, virtualSectionStatus: VirtualSectionStatus, palette: Palette, blocks: IntArray, uniqueId: UniqueId, blockLight: NibbleArray, skyLight: NibbleArray): super(virtualChunk, y, palette, blocks, uniqueId, blockLight, skyLight){
        this.virtualSectionStatus = virtualSectionStatus
    }

}

enum class VirtualSectionStatus {
    SAME, DIFFERENT
}