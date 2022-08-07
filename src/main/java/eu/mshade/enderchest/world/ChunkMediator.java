package eu.mshade.enderchest.world;

import eu.mshade.enderframe.world.Chunk;
import eu.mshade.mwork.mediator.Mediator;

import java.util.function.Supplier;

public class ChunkMediator implements Mediator<Chunk> {


    public static ChunkMediator CHUNK_MEDIATOR = new ChunkMediator();

    @Override
    public <T> T notify(Chunk source, Supplier<T> callback, String key, Object[] args) {
        return callback.get();
    }

}
