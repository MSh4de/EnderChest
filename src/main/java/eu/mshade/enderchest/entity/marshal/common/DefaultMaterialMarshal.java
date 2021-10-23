package eu.mshade.enderchest.entity.marshal.common;

import eu.mshade.enderframe.item.Material;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultMaterialMarshal implements BinaryTagMarshalBuffer<Material> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, Material material, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("material", material.toString());

        return compoundBinaryTag;
    }

    @Override
    public Material deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return Material.valueOf(compoundBinaryTag.getString("material"));
    }
}
