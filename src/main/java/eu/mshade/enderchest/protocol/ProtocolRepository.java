package eu.mshade.enderchest.protocol;

import eu.mshade.enderframe.protocol.MinecraftProtocolVersion;
import eu.mshade.enderframe.protocol.Protocol;
import eu.mshade.mwork.MOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ProtocolRepository {

    private Logger logger = LoggerFactory.getLogger(ProtocolRepository.class);

    private final Map<MinecraftProtocolVersion, Protocol> versionProtocolFrame = new HashMap<>();

    public void register(Protocol protocol){
        logger.info(String.format("Register protocol %s", protocol.getMinecraftProtocolVersion()));
        versionProtocolFrame.put(protocol.getMinecraftProtocolVersion(), protocol);
    }

    public MOptional<Protocol> getProtocolFrameByVersion(MinecraftProtocolVersion version){
        return MOptional.ofNullable(versionProtocolFrame.get(version));
    }


}
