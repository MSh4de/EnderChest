package eu.mshade.enderchest.entity

import eu.mshade.enderframe.entity.*
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import java.util.*

class DefaultBat: Bat {

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

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID, entityType: EntityType) : super(
        location,
        velocity,
        entityId,
        uuid,
        entityType
    )

    constructor(location: Location, entityId: Int, entityType: EntityType) : super(location, entityId, entityType)
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

class DefaultSnowman: Snowman {

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

class DefaultZombie: Zombie {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID, entityType: EntityType) : super(location, velocity, entityId, uuid, entityType)
    constructor(location: Location, entityId: Int, entityType: EntityType) : super(location, entityId, entityType)
    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {
        
    }
}

class DefaultZombiePigman: ZombiePigman {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {
        
    }
}