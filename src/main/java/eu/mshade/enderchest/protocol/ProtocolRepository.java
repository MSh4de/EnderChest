package eu.mshade.enderchest.protocol;

import eu.mshade.enderframe.protocol.ProtocolFrame;
import eu.mshade.enderframe.protocol.ProtocolVersion;
import eu.mshade.mwork.MOptional;

import java.util.HashMap;
import java.util.Map;

public class ProtocolManager {

    private final Map<ProtocolVersion, ProtocolFrame> versionProtocolFrame = new HashMap<>();

    public void register(ProtocolVersion version, ProtocolFrame protocolFrame){
        versionProtocolFrame.put(version, protocolFrame);
    }

    public MOptional<ProtocolFrame> getProtocolFrameByVersion(ProtocolVersion version){
        return MOptional.ofNullable(versionProtocolFrame.get(version));
    }


}
