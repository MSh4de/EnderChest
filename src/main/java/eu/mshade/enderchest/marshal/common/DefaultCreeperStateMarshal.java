package eu.mshade.enderchest.marshal.common;

import eu.mshade.enderframe.entity.CreeperState;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagDriver;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.BinaryTagMarshal;

import java.lang.reflect.Type;

public class DefaultCreeperStateMarshal implements BinaryTagMarshal<CreeperState> {

    @Override
    public BinaryTag<?> serialize(BinaryTagDriver binaryTagDriver, CreeperState creeperState) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();
        compoundBinaryTag.putString("creeperState", creeperState.toString());

        return compoundBinaryTag;
    }

    @Override
    public CreeperState deserialize(BinaryTagDriver binaryTagDriver, BinaryTag<?> binaryTag) throws Exception {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;
        return CreeperState.valueOf(compoundBinaryTag.getString("creeperState"));
    }
}
