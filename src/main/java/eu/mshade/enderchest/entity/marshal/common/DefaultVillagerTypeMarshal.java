package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderframe.entity.VillagerType;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultVillagerTypeMarshal implements BinaryTagMarshalBuffer<VillagerType> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, VillagerType villagerType, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("villagerType", villagerType.toString());

        return compoundBinaryTag;
    }

    @Override
    public VillagerType deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return VillagerType.valueOf(compoundBinaryTag.getString("villagerType"));
    }
}
