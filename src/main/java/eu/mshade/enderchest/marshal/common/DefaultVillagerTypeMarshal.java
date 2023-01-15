package eu.mshade.enderchest.marshal.common;

import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

public class DefaultVillagerTypeMarshal implements BinaryTagMarshal<VillagerType> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, VillagerType villagerType) {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("villagerType", villagerType.toString());

        return compoundBinaryTag;
    }

    @Override
    public VillagerType deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return VillagerType.valueOf(compoundBinaryTag.getString("villagerType"));
    }
}
