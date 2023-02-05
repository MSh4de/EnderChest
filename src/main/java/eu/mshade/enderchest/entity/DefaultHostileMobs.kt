package eu.mshade.enderchest.entity

import eu.mshade.enderframe.entity.*
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import java.util.*

class DefaultBlaze: Blaze {

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

class DefaultCreeper: Creeper {

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

class DefaultGhast: Ghast {

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

class DefaultGuardian: Guardian {

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

class DefaultMagmaCube: MagmaCube {

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

class DefaultSilverFish: Silverfish {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultSkeleton: Skeleton {

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

class DefaultSlime: Slime {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID, entityKey: EntityKey) : super(
        location,
        velocity,
        entityId,
        uuid,
        entityKey
    )

    constructor(location: Location, entityId: Int, entityKey: EntityKey) : super(location, entityId, entityKey)
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

class DefaultZombie: Zombie {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID, entityKey: EntityKey) : super(location, velocity, entityId, uuid, entityKey)
    constructor(location: Location, entityId: Int, entityKey: EntityKey) : super(location, entityId, entityKey)
    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}