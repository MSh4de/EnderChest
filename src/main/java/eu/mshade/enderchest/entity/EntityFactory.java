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
        
        registerFactoryEntity(EntityType.END_CRYSTAL, parameterContainer ->
                new DefaultEnderCrystalEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ITEM_FRAME, parameterContainer ->
                new DefaultItemFrameEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.FIREWORK_ROCKET, parameterContainer ->
                new DefaultFireworkEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ARROW, parameterContainer ->
                new DefaultArrowEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ITEM, parameterContainer ->
                new DefaultItemEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.FURNACE_MINECART, parameterContainer ->
                new DefaultFurnaceMinecartEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.MINECART, parameterContainer ->
                new DefaultMinecartEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.BOAT, parameterContainer ->
                new DefaultBoatEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.GUARDIAN, parameterContainer ->
                new DefaultGuardianEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.WITHER, parameterContainer ->
                new DefaultWitherEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.IRON_GOLEM, parameterContainer ->
                new DefaultIronGolemEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.WITCH, parameterContainer ->
                new DefaultWitchEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SKELETON, parameterContainer ->
                new DefaultSkeletonEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.MAGMA_CUBE, parameterContainer ->
                new DefaultMagmaCubeEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SLIME, parameterContainer ->
                new DefaultSlimeEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.GHAST, parameterContainer ->
                new DefaultGhastEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.CREEPER, parameterContainer ->
                new DefaultCreeperEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.CAVE_SPIDER, parameterContainer ->
                new DefaultCaveSpiderEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SPIDER, parameterContainer ->
                new DefaultSpiderEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.PIG_ZOMBIE, parameterContainer ->
                new DefaultZombiePigmanEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ENDERMAN, parameterContainer ->
                new DefaultEndermanEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.VILLAGER, parameterContainer ->
                new DefaultVillagerEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SHEEP, parameterContainer ->
                new DefaultSheepEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.RABBIT, parameterContainer ->
                new DefaultRabbitEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.WOLF, parameterContainer ->
                new DefaultWolfEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.OCELOT, parameterContainer ->
                new DefaultOcelotEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.BAT, parameterContainer ->
                new DefaultBatEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.HORSE, parameterContainer ->
                new DefaultHorseEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.PIG, parameterContainer ->
                new DefaultPigEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ARMOR_STAND, parameterContainer ->
            new DefaultArmorStandEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.CHICKEN, parameterContainer ->
                new DefaultChickenEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.BLAZE, parameterContainer ->
                new DefaultBlazeEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ZOMBIE, parameterContainer ->
                new DefaultZombieEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
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
