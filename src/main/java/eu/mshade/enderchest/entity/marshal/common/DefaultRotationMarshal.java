package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderframe.world.Rotation;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultRotationMarshal implements BinaryTagMarshalBuffer<Rotation> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Rotation rotation, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putFloat("yaw", rotation.getYaw());
        compoundBinaryTag.putFloat("pitch", rotation.getPitch());
        compoundBinaryTag.putFloat("roll", rotation.getRoll());

        return compoundBinaryTag;
    }

    @Override
    public Rotation deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        float yaw = compoundBinaryTag.getFloat("yaw");
        float pitch = compoundBinaryTag.getFloat("pitch");
        float roll = compoundBinaryTag.getFloat("roll");

        return new Rotation(yaw, pitch, roll);
    }
}
