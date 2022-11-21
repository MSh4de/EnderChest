package eu.mshade.enderchest.marshal.mojang

import eu.mshade.enderframe.mojang.GameProfile
import eu.mshade.enderframe.mojang.Property
import eu.mshade.mwork.binarytag.BinaryTag
import eu.mshade.mwork.binarytag.BinaryTagType
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import eu.mshade.mwork.binarytag.entity.ListBinaryTag
import java.util.*

object GameProfileBinaryTagMarshal {

    fun serialize(gameProfile: GameProfile): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putString("uuid", gameProfile.id.toString())
        compoundBinaryTag.putString("name", gameProfile.name)
        val propertiesBinaryTag = ListBinaryTag(BinaryTagType.COMPOUND)
        gameProfile.properties.forEach { (_, property) ->
            propertiesBinaryTag.add(serializeProperty(property))
        }
        compoundBinaryTag.putBinaryTag("properties", propertiesBinaryTag)
        return compoundBinaryTag
    }


    fun deserialize(binaryTag: BinaryTag<*>): GameProfile {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        val propertiesBinaryTag = compoundBinaryTag.getBinaryTag("properties") as ListBinaryTag

        val gameProfile =
            GameProfile(UUID.fromString(compoundBinaryTag.getString("uuid")), compoundBinaryTag.getString("name"))
        propertiesBinaryTag.value.forEach {
            gameProfile.setProperty(deserializeProperty(it))
        }
        return gameProfile
    }


    private fun serializeProperty(property: Property): BinaryTag<*> {
        val compoundBinaryTag = CompoundBinaryTag()
        compoundBinaryTag.putString("name", property.name)
        compoundBinaryTag.putString("value", property.value)
        compoundBinaryTag.putString("signature", property.signature)
        return compoundBinaryTag
    }

    private fun deserializeProperty(binaryTag: BinaryTag<*>): Property {
        val compoundBinaryTag = binaryTag as CompoundBinaryTag
        return Property(compoundBinaryTag.getString("name"), compoundBinaryTag.getString("value"), compoundBinaryTag.getString("signature"))
    }

}