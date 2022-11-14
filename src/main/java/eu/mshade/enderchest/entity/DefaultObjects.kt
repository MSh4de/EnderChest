package eu.mshade.enderchest.entity

import eu.mshade.enderframe.entity.ArmorStand
import eu.mshade.enderframe.entity.Arrow
import eu.mshade.enderframe.entity.EnderCrystal
import eu.mshade.enderframe.entity.EntityType
import eu.mshade.enderframe.entity.Firework
import eu.mshade.enderframe.entity.FurnaceMinecart
import eu.mshade.enderframe.entity.ItemFrame
import eu.mshade.enderframe.entity.Minecart
import eu.mshade.enderframe.world.Location
import eu.mshade.enderframe.world.Vector
import java.util.*

class DefaultEnderCrystal: EnderCrystal {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)
}

class DefaultArrow: Arrow {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)
}

class DefaultItemFrame: ItemFrame {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)
}

class DefaultFirework: Firework {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)
}

class DefaultArmorStand: ArmorStand {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(
        location,
        velocity,
        entityId,
        uuid
    )

    constructor(location: Location, entityId: Int) : super(location, entityId)
}
