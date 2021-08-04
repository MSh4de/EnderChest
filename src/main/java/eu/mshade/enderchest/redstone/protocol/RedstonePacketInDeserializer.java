package eu.mshade.enderchest.redstone.protocol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import eu.mshade.mwork.MWork;
import org.json.JSONObject;

import java.io.IOException;

public class RedstonePacketInDeserializer extends JsonDeserializer<RedstonePacketIn> {

    @Override
    public RedstonePacketIn deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String packetType = node.get("packetType").asText();
        return (RedstonePacketIn) MWork.get().getObjectMapper().readValue(node.toString(), RedstonePacketType.valueOf(packetType).getRedstonePacketInType());
    }
}
