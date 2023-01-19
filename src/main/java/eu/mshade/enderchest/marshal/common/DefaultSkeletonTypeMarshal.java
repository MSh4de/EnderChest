package eu.mshade.enderchest.marshal.common;

<<<<<<< HEAD
=======
import eu.mshade.enderframe.entity.SkeletonType;
>>>>>>> rework-entities
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

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
        //return SkeletonType.valueOf(compoundBinaryTag.getString("skeletonType"));
        return null; //TODO LA PUTAIN DE TA GRAND MERE
    }
}
