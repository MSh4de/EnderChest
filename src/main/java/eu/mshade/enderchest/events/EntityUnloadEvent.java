package eu.mshade.enderchest.events;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.mwork.ParameterContainer;

public class EntityUnloadEvent implements EnderchestEvent {

    private final Entity entity;
    private final ParameterContainer parameterContainer;

    public EntityUnloadEvent(Entity entity, ParameterContainer parameterContainer) {
        this.entity = entity;
        this.parameterContainer = parameterContainer;
    }
}
