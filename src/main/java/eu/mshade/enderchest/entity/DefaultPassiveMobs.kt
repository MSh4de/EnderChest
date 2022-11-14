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

class DefaultChicken: Chicken {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultCow: Cow {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultHorse: Horse {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultMooshroom: Mooshroom {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultOcelot: Ocelot {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultPig: Pig {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultRabbit: Rabbit {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
    constructor(location: Location, entityId: Int) : super(location, entityId)

    override fun tick() {

    }
}

class DefaultSheep: Sheep {

    constructor(location: Location, velocity: Vector, entityId: Int, uuid: UUID) : super(location, velocity, entityId, uuid)
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

class DefaultSquid: Squid {

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