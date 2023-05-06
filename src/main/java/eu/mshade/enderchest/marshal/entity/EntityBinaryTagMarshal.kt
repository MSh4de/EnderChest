package eu.mshade.enderchest.marshal.entity

import eu.mshade.enderchest.entity.DefaultSheep
import eu.mshade.enderchest.marshal.world.LocationBinaryTagMarshal
import eu.mshade.enderchest.marshal.world.VectorBinaryTagMarshal
import eu.mshade.enderframe.entity.Entity
import eu.mshade.enderframe.entity.EntityKey
import eu.mshade.enderframe.entity.EntityType
import eu.mshade.enderframe.metadata.MetadataKeyValueBufferRegistry
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag
import java.util.*

object EntityBinaryTagMarshal {

    val entityTypes = mutableMapOf<EntityKey, (location: Location, velocity: Vector, entityId: Int, uuid: UUID) -> Entity>()

    init {
        entityTypes[EntityType.SHEEP] = {location, velocity, entityId, uuid -> DefaultSheep(location, velocity, entityId, uuid)}
    }

    fun serialize(entity: Entity, metadataKeyValueBinaryTagMarshal: MetadataKeyValueBufferRegistry): CompoundBinaryTag {
        val compound = CompoundBinaryTag()

        compound.putString("type", entity.entityKey.getName())
        compound.putBinaryTag("location", LocationBinaryTagMarshal.serialize(entity.location))
        compound.putBinaryTag("metadataKeyValueBucket", metadataKeyValueBinaryTagMarshal.serialize(entity.metadataKeyValueBucket))
        compound.putBinaryTag("velocity", VectorBinaryTagMarshal.serialize(entity.velocity))
        return compound
    }

    fun deserialize(compoundBinaryTag: CompoundBinaryTag, metadataKeyValueBinaryTagMarshal: MetadataKeyValueBufferRegistry): Entity? {
        return null
    }
}