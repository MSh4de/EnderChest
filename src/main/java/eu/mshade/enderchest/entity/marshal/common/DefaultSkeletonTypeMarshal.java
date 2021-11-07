package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderframe.entity.SkeletonType;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultSkeletonTypeMarshal implements BinaryTagMarshalBuffer<SkeletonType> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, SkeletonType skeletonType, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("skeletonType", skeletonType.toString());

        return compoundBinaryTag;
    }

    @Override
    public SkeletonType deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return SkeletonType.valueOf(compoundBinaryTag.getString("skeletonType"));
    }
}
