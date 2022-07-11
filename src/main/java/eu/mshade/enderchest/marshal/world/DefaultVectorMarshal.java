package eu.mshade.enderchest.marshal.world;

import eu.mshade.enderframe.world.Vector;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.BinaryTagDynamicMarshal;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultVectorMarshal implements BinaryTagDynamicMarshal {

    public BinaryTag<?> serialize(Vector vector)  {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putDouble("x", vector.getX());
        compoundBinaryTag.putDouble("y", vector.getY());
        compoundBinaryTag.putDouble("z", vector.getZ());

        return compoundBinaryTag;
    }

    public Vector deserialize(BinaryTag<?> binaryTag){
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        double x = compoundBinaryTag.getDouble("x");
        double y = compoundBinaryTag.getDouble("y");
        double z = compoundBinaryTag.getDouble("z");

        return new Vector(x,y,z);
    }
}
