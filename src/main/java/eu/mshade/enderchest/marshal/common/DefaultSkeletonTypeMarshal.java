package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderframe.entity.SkeletonType;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultSkeletonTypeMarshal implements BinaryTagMarshal<SkeletonType> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, SkeletonType skeletonType) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("skeletonType", skeletonType.toString());

        return compoundBinaryTag;
    }

    @Override
    public SkeletonType deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return SkeletonType.valueOf(compoundBinaryTag.getString("skeletonType"));
    }
}
