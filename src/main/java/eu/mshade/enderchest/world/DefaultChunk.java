package eu.mshade.enderchest.world;

import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.EntityIdManager;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.item.MaterialKey;
import eu.mshade.enderframe.world.Chunk;
import eu.mshade.enderframe.world.ChunkStatus;
import eu.mshade.enderframe.world.Section;
import eu.mshade.enderframe.world.World;
import eu.mshade.mwork.mediator.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultChunk implements Chunk {

    private static final Logger logger = LoggerFactory.getLogger(DefaultChunk.class);

    public static final int WIDTH = 16, HEIGHT = 16, DEPTH = 256, SEC_DEPTH = 16;

    private final int x;
    private final int z;
    private final UUID id;
    private final AtomicBoolean hasChange;
    private final AtomicLong lastInteract = new AtomicLong(System.currentTimeMillis());
    private final Queue<Player> players = new ConcurrentLinkedQueue<>();
    private final World world;
    private final Section[] sections = new Section[16];
    private final Queue<Entity> entities = new ConcurrentLinkedQueue<>();
    private Mediator<Chunk> chunkMediator = ChunkMediator.CHUNK_MEDIATOR;
    private ChunkStatus chunkStatus = ChunkStatus.PREPARE_TO_LOAD;

    private int age;
    //private final AtomicReference<ChunkBufferStatus> chunkBufferStatus
    private byte[] biomes;

    public DefaultChunk(int x, int z, boolean hasChange, World world, byte[] biomes) {
        this.x = x;
        this.z = z;
        this.hasChange = new AtomicBoolean(hasChange);
        this.world = world;
        this.biomes = biomes;
        this.id = Chunk.ofId(x, z);
    }

    public DefaultChunk(int x, int z, boolean hasChange, World world) {
        this(x, z, hasChange, world, new byte[256]);
    }

    @Override
    public int getX() {
        return chunkMediator.notify(this, () -> x, "getX", new Object[0]);
        //getHealth().set(System.currentTimeMillis());
    }

    @Override
    public int getZ() {
        return chunkMediator.notify(this, () -> z, "getZ", new Object[0]);
        /*
        getHealth().set(System.currentTimeMillis());
        return z;

         */
    }

    @Override
    public UUID getId() {
        return chunkMediator.notify(this, () -> id, "getId", new Object[0]);
        /*
        getHealth().set(System.currentTimeMillis());
        return id;

         */
    }

    @Override
    public Queue<Player> getViewers() {
        return chunkMediator.notify(this, () -> players, "getViewers", new Object[0]);
    }

    @Override
    public void addViewer(Player player) {
        this.chunkMediator.notify(this, () -> {
            this.players.add(player);
            if(getChunkStatus() == ChunkStatus.PREPARE_TO_UNLOAD) setChunkStatus(ChunkStatus.LOADED);
        }, "addViewer", new Object[]{player});
    }

    @Override
    public void removeViewer(Player player) {
        this.chunkMediator.notify(this, () -> {
            this.players.remove(player);
        }, "removeViewer", new Object[]{player});
    }

    @Override
    public World getWorld() {
        return chunkMediator.notify(this, () -> world, "getWorld", new Object[0]);
        /*
        getHealth().set(System.currentTimeMillis());
        return world;

         */
    }

    @Override
    public Section[] getSections() {
        return chunkMediator.notify(this, () -> sections, "getSections", new Object[0]);
        /*
        getHealth().set(System.currentTimeMillis());
        return sections;

         */
    }

    @Override
    public AtomicLong getLastInteract() {
        return chunkMediator.notify(this, () -> lastInteract, "getLastInteract", new Object[0]);
        /*
        return lastInteract;

         */
    }

    @Override
    public ChunkStatus getChunkStatus() {
        return this.chunkStatus;
    }

    @Override
    public void setChunkStatus(ChunkStatus chunkStatus) {
        this.chunkStatus = chunkStatus;
    }


    @Override
    public int getBitMask() {
        return chunkMediator.notify(this, () -> {
            int sectionBitmask = 0;
            for (int i = 0; i < sections.length; i++) {
                if (sections[i] != null && sections[i].getRealBlock() != 0) {
                    sectionBitmask |= 1 << i;
                }
            }
            return sectionBitmask;
        }, "getBitMask", new Object[0]);
        /*
        getLastInteract().set(System.currentTimeMillis());
        int sectionBitmask = 0;
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] != null && sections[i].getRealBlock() != 0) {
                sectionBitmask |= 1 << i;
            }
        }
        return sectionBitmask;

         */
    }

    @Override
    public int getBlock(int x, int y, int z) {
        return chunkMediator.notify(this, () -> {
            Section section = getSectionBuffer(y);
            return section.getBlocks()[getIndex(x, y, z)];
        }, "getBlock", new Object[]{x, y, z});
        /*
        getLastInteract().set(System.currentTimeMillis());
        Section section = getSectionBuffer(y);
        return section.getBlocks()[getIndex(x, y, z)];

         */
    }

    @Override
    public void setBlock(int x, int y, int z, MaterialKey materialKey) {

         chunkMediator.notify(this, () -> {
            Section section = getSectionBuffer(y);

            if (materialKey.getId() == 0){
                int i = section.getRealBlock() - 1;
                section.setRealBlock(Math.max(i, 0));
            }else section.setRealBlock(section.getRealBlock() + 1);
            hasChange.set(true);
            int index = getIndex(x, y, z);
            section.getBlocks()[index] = materialKey.getId();

         }, "setBlock", new Object[]{x, y, z, materialKey});
        /*
        getLastInteract().set(System.currentTimeMillis());
        Section section = getSectionBuffer(y);
        if (materialKey.getId() == 0){
            int i = section.getRealBlock() - 1;
            section.setRealBlock(Math.max(i, 0));
        }else section.setRealBlock(section.getRealBlock() + 1);
        hasChange.set(true);
        int index = getIndex(x, y, z);
        section.getBlocks()[index] = materialKey.getId();

         */
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        return chunkMediator.notify(this, () -> {
            Section section = getSectionBuffer(y);
            return section.getBlockLight().get(getIndex(x, y, z));
        }, "getBlockLight", new Object[]{x, y, z});
        /*
        getLastInteract().set(System.currentTimeMillis());
        Section section = getSectionBuffer(y);
        return section.getBlockLight().get(getIndex(x, y, z));

         */
    }

    @Override
    public void setBlockLight(int x, int y, int z, byte light) {
        chunkMediator.notify(this, () -> {
            Section section = getSectionBuffer(y);
            section.getBlockLight().set(getIndex(x, y, z), light);
        }, "setBlockLight", new Object[]{x, y, z, light});
        /*
        getLastInteract().set(System.currentTimeMillis());
        Section section = getSectionBuffer(y);
        section.getBlockLight().set(getIndex(x, y, z), light);
        hasChange.set(true);

         */
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        return chunkMediator.notify(this, () -> {
            Section section = getSectionBuffer(y);
            return section.getSkyLight().get(getIndex(x, y, z));
        }, "getSkyLight", new Object[]{x, y, z});
        /*
        getLastInteract().set(System.currentTimeMillis());
        Section section = getSectionBuffer(y);
        return section.getSkyLight().get(getIndex(x, y, z));

         */
    }

    @Override
    public void setSkyLight(int x, int y, int z, byte light) {
        chunkMediator.notify(this, () -> {
            Section section = getSectionBuffer(y);
            section.getSkyLight().set(getIndex(x, y, z), light);
        }, "setSkyLight", new Object[]{x, y, z, light});
        /*
        getLastInteract().set(System.currentTimeMillis());
        Section section = getSectionBuffer(y);
        section.getSkyLight().set(getIndex(x, y, z), light);
        hasChange.set(true);

         */
    }

    @Override
    public void setBiome(int x, int z, int biome) {
        chunkMediator.notify(this, () -> {
            hasChange.set(true);
            biomes[(z & 0xF) * WIDTH + (x & 0xF)] = (byte) biome;
        }, "setBiome", new Object[]{x, z, biome});
        /*
        getLastInteract().set(System.currentTimeMillis());
        hasChange.set(true);
        biomes[(z & 0xF) * WIDTH + (x & 0xF)] = (byte) biome;

         */
    }

    @Override
    public int getBiome(int x, int z) {
        return chunkMediator.notify(this, () -> biomes[(z & 0xF) * WIDTH + (x & 0xF)] & 0xFF, "getBiome", new Object[]{x, z});
        /*
        getLastInteract().set(System.currentTimeMillis());
        return biomes[(z & 0xF) * WIDTH + (x & 0xF)] & 0xFF;

         */
    }

    @Override
    public byte[] getBiomes() {
        return chunkMediator.notify(this, () -> biomes, "getBiomes", new Object[0]);
        /*
        getLastInteract().set(System.currentTimeMillis());
        return biomes;

         */
    }

    @Override
    public int getHighest(int x, int z) {
        for (int i = 255; i > 0; i--) {
            if (sections[i >> 4] != null && getBlock(x, i, z) != 0) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean hasChange() {
        return hasChange.get();
    }


    @Override
    public Queue<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public void addEntity(Entity entity) {
        System.out.println("REGISTER "+entity);
        getEntities().add(entity);
        getWorld().addEntity(entity);

        hasChange.set(true);
    }

    @Override
    public void removeEntity(Entity entity) {
        getWorld().removeEntity(entity);
        getEntities().remove(entity);
        EntityIdManager.get().flushId(entity.getEntityId());

        hasChange.set(true);
    }

    @Override
    public void clearEntities() {
        this.entities.forEach(this::removeEntity);
    }



    public void setBiomes(byte[] biomes) {
        this.biomes = biomes;
    }


    public Section getSectionBuffer(int y){
        int realY = y >> 4;
        if (sections[realY] == null){
            sections[realY] = new DefaultSection(this, realY, 0);
        }
        return sections[realY];
    }

    public int getIndex(int x, int y, int z){
        return ((y & 0xf) << 8) | ((z & 0x0f) << 4) | (x & 0x0f);
    }

    @Override
    public String toString() {
        return "DefaultChunk{" +
                "x=" + x +
                ", z=" + z +
                ", players=" + players +
                ", world=" + world +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultChunk that = (DefaultChunk) o;
        return x == that.x && z == that.z && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, world);
    }
}
