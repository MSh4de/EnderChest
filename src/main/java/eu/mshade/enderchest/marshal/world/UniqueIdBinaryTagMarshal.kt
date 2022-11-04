package eu.mshade.enderchest.marshal.world

import eu.mshade.enderframe.UniqueId
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagType
import eu.mshade.mwork.binarytag.IntBinaryTag
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag

object UniqueIdBinaryTagMarshal {

    fun serialize(uniqueId: UniqueId): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("offset", uniqueId.atomicId.get())
        val freeIdList = ListBinaryTag(BinaryTagType.INT)
        uniqueId.freeIdQueue.forEach { freeIdList.add(IntBinaryTag(it)) }
        compoundBinaryTag.putBinaryTag("freeIdQueue", freeIdList)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>): UniqueId {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val offset = compoundBinaryTag.getInt("offset")
        val freeIdList = compoundBinaryTag.getBinaryTag("freeIdQueue") as ListBinaryTag
        val freeIdQueue = freeIdList.value.map { it as IntBinaryTag }.map { it.value }.toMutableList()
        return UniqueId(offset, freeIdQueue)
    }

}