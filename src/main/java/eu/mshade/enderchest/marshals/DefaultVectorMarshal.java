package eu.mshade.enderchest.marshals;

import eu.mshade.enderframe.world.Vector;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultVectorMarshal implements BinaryTagMarshalBuffer<Vector> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Vector vector, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putDouble("x",vector.getX());
        compoundBinaryTag.putDouble("y",vector.getY());
        compoundBinaryTag.putDouble("z",vector.getZ());

        return compoundBinaryTag;
    }

    @Override
    public Vector deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        double x = compoundBinaryTag.getDouble("x");
        double y = compoundBinaryTag.getDouble("y");
        double z = compoundBinaryTag.getDouble("z");
        return new Vector(x,y,z);
    }
}
