package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderchest.world.WorldManager;
import eu.mshade.enderframe.metadata.world.WorldMetadataType;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.World;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;

public class LocationBinaryTagMarshal implements BinaryTagDynamicMarshal {

    public BinaryTag<?> serialize(Location location) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        World world = location.getWorld();
        String name = world.getMetadataKeyValueBucket().getValueOfMetadataKeyValue(WorldMetadataType.NAME, String.class);

        compoundBinaryTag.putString("world", name);
        compoundBinaryTag.putDouble("x", location.getX());
        compoundBinaryTag.putDouble("y", location.getY());
        compoundBinaryTag.putDouble("z", location.getZ());
        compoundBinaryTag.putFloat("yaw", location.getYaw());
        compoundBinaryTag.putFloat("pitch", location.getPitch());

        return compoundBinaryTag;
    }

    public Location deserialize(BinaryTag<?> binaryTag, WorldManager worldManager) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag)binaryTag;

        World world = worldManager.getWorld(compoundBinaryTag.getString("world"));
        double x = compoundBinaryTag.getDouble("x");
        double y = compoundBinaryTag.getDouble("y");
        double z = compoundBinaryTag.getDouble("z");
        float yaw = compoundBinaryTag.getFloat("yaw");
        float pitch = compoundBinaryTag.getFloat("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}
