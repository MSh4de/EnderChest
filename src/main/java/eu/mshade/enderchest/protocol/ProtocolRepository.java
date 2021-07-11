package eu.mshade.enderchest.protocol;

import eu.mshade.enderframe.EnderFrameProtocol;
import eu.mshade.enderframe.protocol.ProtocolVersion;
import eu.mshade.mwork.MOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ProtocolRepository {

    private Logger logger = LoggerFactory.getLogger(ProtocolRepository.class);

    private final Map<ProtocolVersion, EnderFrameProtocol> versionProtocolFrame = new HashMap<>();

    public void register(EnderFrameProtocol enderFrameProtocol){
        logger.info(String.format("Register protocol %s", enderFrameProtocol.getProtocolVersion()));
        versionProtocolFrame.put(enderFrameProtocol.getProtocolVersion(), enderFrameProtocol);
    }

    public MOptional<EnderFrameProtocol> getProtocolFrameByVersion(ProtocolVersion version){
        return MOptional.ofNullable(versionProtocolFrame.get(version));
    }


}
