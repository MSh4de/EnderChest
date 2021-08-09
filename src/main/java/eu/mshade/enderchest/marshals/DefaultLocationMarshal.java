package eu.mshade.enderchest.marshals;

import eu.mshade.enderchest.world.WorldManager;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultLocationMarshal implements BinaryTagMarshalBuffer<Location> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Location location, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putString("worldName", location.getWorld().getWorldLevel().getName());
        compoundBinaryTag.putDouble("x", location.getX());
        compoundBinaryTag.putDouble("y", location.getY());
        compoundBinaryTag.putDouble("z", location.getZ());
        compoundBinaryTag.putFloat("yaw", location.getYaw());
        compoundBinaryTag.putFloat("pitch", location.getPitch());

        return compoundBinaryTag;
    }

    @Override
    public Location deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        WorldManager worldManager = parameterContainer.getContainer(WorldManager.class);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag)binaryTag;

        WorldBuffer world = worldManager.getWorldBuffer(compoundBinaryTag.getString("worldName"));
        double x = compoundBinaryTag.getDouble("x");
        double y = compoundBinaryTag.getDouble("y");
        double z = compoundBinaryTag.getDouble("z");
        float yaw = compoundBinaryTag.getFloat("yaw");
        float pitch = compoundBinaryTag.getFloat("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}
