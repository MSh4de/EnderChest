package eu.mshade.enderchest.entity

import eu.mshade.enderframe.entity.EnderDragon
import eu.mshade.enderframe.entity.GiantZombie
import eu.mshade.enderframe.entity.Wither
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import java.util.*

class DefaultEnderDragon: EnderDragon {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultWither: Wither {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultGiantZombie: GiantZombie {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}