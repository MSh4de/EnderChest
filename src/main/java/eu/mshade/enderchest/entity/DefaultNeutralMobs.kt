package eu.mshade.enderchest.entity

import eu.mshade.enderframe.entity.*
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import java.util.*

class DefaultCaveSpider: CaveSpider {

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

class DefaultEnderman: Enderman {

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

class DefaultIronGolem: IronGolem {

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

class DefaultSpider: Spider {

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

class DefaultWolf: Wolf {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultZombifiedPiglin: ZombifiedPiglin {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}