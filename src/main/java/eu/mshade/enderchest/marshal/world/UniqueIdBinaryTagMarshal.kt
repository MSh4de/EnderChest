package eu.mshade.enderchest.marshal.world

import eu.mshade.enderframe.UniqueId
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal
import eu.mshade.mwork.binarytag.BinaryTagType
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.IntegerBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag

class UniqueIdBinaryTagMarshal: BinaryTagDynamicMarshal {

    fun serialize(uniqueId: UniqueId): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putInt("offset", uniqueId.atomicId.get())
        val freeIdList = ListBinaryTag(BinaryTagType.INTEGER)
        uniqueId.freeIdQueue.forEach { freeIdList.add(IntegerBinaryTag(it)) }
        compoundBinaryTag.putBinaryTag("freeIdQueue", freeIdList)
        return compoundBinaryTag
    }

    fun deserialize(binaryTag: BinaryTag<*>): UniqueId {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val offset = compoundBinaryTag.getInt("offset")
        val freeIdList = compoundBinaryTag.getBinaryTag("freeIdQueue") as ListBinaryTag
        val freeIdQueue = freeIdList.map { it as IntegerBinaryTag }.map { it.value }.toMutableList()
        return UniqueId(offset, freeIdQueue)
    }

}