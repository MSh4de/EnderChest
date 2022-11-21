import eu.mshade.enderframe.item.Material;
import eu.mshade.enderframe.world.chunk.ChunkStateStore;
import eu.mshade.enderframe.world.chunk.Palette;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class TestStore {

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeUTF("test");
        dataOutputStream.writeBoolean(true);
        dataOutputStream.writeByte(1);
        dataOutputStream.writeInt(112321);
        dataOutputStream.writeLong(133333333333333L);
        dataOutputStream.writeFloat(1.122121F);
        dataOutputStream.writeDouble(1.000999);
        dataOutputStream.writeShort(1478);
        dataOutputStream.writeChar('a');
        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));

        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

    }

}
