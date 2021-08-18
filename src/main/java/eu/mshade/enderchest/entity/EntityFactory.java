package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityType;
import eu.mshade.enderframe.world.Location;
import eu.mshade.mwork.MFunction;
import eu.mshade.mwork.ParameterContainer;

import java.util.HashMap;
import java.util.Map;

public class EntityFactory {

    private static EntityFactory entityFactory;
    private final Map<EntityType, MFunction<ParameterContainer, Entity>> factoryEntities = new HashMap<>();

    private EntityFactory() {
        entityFactory = this;

        registerFactoryEntity(EntityType.ZOMBIE, parameterContainer ->
                new DefaultZombie(parameterContainer.getContainer(Integer.class), parameterContainer.getContainer(Location.class)));
        registerFactoryEntity(EntityType.BLAZE, parameterContainer ->
                new DefaultBlaze(parameterContainer.getContainer(Integer.class), parameterContainer.getContainer(Location.class)));
    }

    public static EntityFactory get() {
        return entityFactory == null ? new EntityFactory() : entityFactory;
    }

    public void registerFactoryEntity(EntityType entityType, MFunction<ParameterContainer, Entity> factoryEntity){
        this.factoryEntities.put(entityType, factoryEntity);
    }

    public Entity factoryEntity(EntityType entityType, ParameterContainer parameterContainer) throws Exception {
        return this.factoryEntities.get(entityType).apply(parameterContainer);
    }
}
