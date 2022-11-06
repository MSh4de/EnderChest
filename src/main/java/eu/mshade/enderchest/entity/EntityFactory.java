package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class EntityFactory {

    private static EntityFactory entityFactory;

    private EntityFactory() {
        entityFactory = this;
        /*
        registerFactoryEntity(EntityType.SNOWMAN, parameterContainer ->
                new DefaultSnowman(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.MOOSHROOM, parameterContainer ->
                new DefaultMooshroom(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SQUID, parameterContainer ->
                new DefaultSquid(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ENDER_DRAGON, parameterContainer ->
                new DefaultEnderDragon(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.GIANT, parameterContainer ->
                new DefaultGiantZombieEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SILVERFISH, parameterContainer ->
                new DefaultSilverfish(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.END_CRYSTAL, parameterContainer ->
                new DefaultEnderCrystal(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ITEM_FRAME, parameterContainer ->
                new DefaultItemFrameEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.FIREWORK_ROCKET, parameterContainer ->
                new DefaultFireworkEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ARROW, parameterContainer ->
                new DefaultArrow(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ITEM, parameterContainer ->
                new DefaultItemEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.FURNACE_MINECART, parameterContainer ->
                new DefaultFurnaceMinecartEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.MINECART, parameterContainer ->
                new DefaultMinecart(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.BOAT, parameterContainer ->
                new DefaultBoat(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.GUARDIAN, parameterContainer ->
                new DefaultGuardianEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.WITHER, parameterContainer ->
                new DefaultWither(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.IRON_GOLEM, parameterContainer ->
                new DefaultIronGolemEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.WITCH, parameterContainer ->
                new DefaultWitch(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SKELETON, parameterContainer ->
                new DefaultSkeleton(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.MAGMA_CUBE, parameterContainer ->
                new DefaultMagmaCubeEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SLIME, parameterContainer ->
                new DefaultSlime(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.GHAST, parameterContainer ->
                new DefaultGhastEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.CREEPER, parameterContainer ->
                new DefaultCreeper(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.CAVE_SPIDER, parameterContainer ->
                new DefaultCaveSpider(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SPIDER, parameterContainer ->
                new DefaultSpider(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.PIG_ZOMBIE, parameterContainer ->
                new DefaultZombiePigman(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ENDERMAN, parameterContainer ->
                new DefaultEnderman(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.VILLAGER, parameterContainer ->
                new DefaultVillager(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.SHEEP, parameterContainer ->
                new DefaultSheep(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.RABBIT, parameterContainer ->
                new DefaultRabbit(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.WOLF, parameterContainer ->
                new DefaultWolf(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.OCELOT, parameterContainer ->
                new DefaultOcelot(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.BAT, parameterContainer ->
                new DefaultBat(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.HORSE, parameterContainer ->
                new DefaultHorseEntity(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.PIG, parameterContainer ->
                new DefaultPig(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ARMOR_STAND, parameterContainer ->
            new DefaultArmorStand(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.CHICKEN, parameterContainer ->
                new DefaultChicken(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.BLAZE, parameterContainer ->
                new DefaultBlaze(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));
        registerFactoryEntity(EntityType.ZOMBIE, parameterContainer ->
                new DefaultZombie(parameterContainer.getContainer(Location.class), parameterContainer.getContainer(Integer.class)));


         */
    }

    public static EntityFactory get() {
        return entityFactory == null ? new EntityFactory() : entityFactory;
    }

}
