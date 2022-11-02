package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderframe.world.Rotation;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

public class DefaultRotationMarshal implements BinaryTagMarshal<Rotation> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Rotation rotation) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putFloat("yaw", rotation.getYaw());
        compoundBinaryTag.putFloat("pitch", rotation.getPitch());
        compoundBinaryTag.putFloat("roll", rotation.getRoll());

        return compoundBinaryTag;
    }

    @Override
    public Rotation deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        float yaw = compoundBinaryTag.getFloat("yaw");
        float pitch = compoundBinaryTag.getFloat("pitch");
        float roll = compoundBinaryTag.getFloat("roll");

        return new Rotation(yaw, pitch, roll);
    }
}
