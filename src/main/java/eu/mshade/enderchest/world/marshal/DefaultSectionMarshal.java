package eu.mshade.enderchest.world.marshal;

import eu.mshade.enderchest.world.DefaultSectionBuffer;
import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.enderframe.world.NibbleArray;
import eu.mshade.enderframe.world.SectionBuffer;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.binarytag.BinaryTag;
import eu.mshade.mwork.binarytag.entity.CompoundBinaryTag;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshal;
import eu.mshade.mwork.binarytag.marshal.BinaryTagMarshalBuffer;

import java.lang.reflect.Type;

public class DefaultSectionMarshal implements BinaryTagMarshalBuffer<SectionBuffer> {

    @Override
    public BinaryTag<?> serialize(BinaryTagMarshal binaryTagMarshal, Type type, SectionBuffer sectionBuffer, ParameterContainer parameterContainer) throws Exception {
        CompoundBinaryTag compoundBinaryTag = new CompoundBinaryTag();

        compoundBinaryTag.putInt("y", sectionBuffer.getY());
        compoundBinaryTag.putInt("realBlock", sectionBuffer.getRealBlock());
        compoundBinaryTag.putIntArray("blocks", sectionBuffer.getBlocks());
        compoundBinaryTag.putByteArray("data", sectionBuffer.getData().getRawData());
        compoundBinaryTag.putByteArray("blockLight", sectionBuffer.getBlockLight().getRawData());
        compoundBinaryTag.putByteArray("skyLight", sectionBuffer.getSkyLight().getRawData());

        return compoundBinaryTag;
    }

    @Override
    public SectionBuffer deserialize(BinaryTagMarshal binaryTagMarshal, Type type, BinaryTag<?> binaryTag, ParameterContainer parameterContainer) {
        ChunkBuffer chunkBuffer = parameterContainer.getContainer(ChunkBuffer.class);
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) binaryTag;

        return new DefaultSectionBuffer(chunkBuffer,
                compoundBinaryTag.getInt("y"),
                compoundBinaryTag.getInt("realBlock"),
                compoundBinaryTag.getIntArray("blocks"),
                new NibbleArray(compoundBinaryTag.getByteArray("data")),
                new NibbleArray(compoundBinaryTag.getByteArray("blockLight")),
                new NibbleArray(compoundBinaryTag.getByteArray("skyLight")));
    }
}
