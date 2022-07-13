package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderframe.item.Material;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

public class DefaultMaterialMarshal implements BinaryTagMarshal<Material> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, Material material) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("material", material.toString());

        return compoundBinaryTag;
    }

    @Override
    public Material deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return null;
    }
}
