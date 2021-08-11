package eu.mshade.enderchest.events;

import eu.mshade.enderframe.world.ChunkBuffer;
import eu.mshade.mwork.ParameterContainer;

public class ChunkUnloadEvent implements EnderchestEvent {

    private final ChunkBuffer chunkBuffer;
    private final ParameterContainer parameterContainer;

    public ChunkUnloadEvent(ChunkBuffer chunkBuffer, ParameterContainer parameterContainer) {
        this.chunkBuffer = chunkBuffer;
        this.parameterContainer = parameterContainer;
    }
}
