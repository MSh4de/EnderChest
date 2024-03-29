package eu.mshade.enderchest.entity;

import eu.mshade.enderchest.axolotl.AxololtConnection;
import eu.mshade.enderchest.world.virtual.VirtualSection;
import eu.mshade.enderchest.world.virtual.VirtualSectionStatus;
import eu.mshade.enderframe.Agent;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.Watchable;
import eu.mshade.enderframe.entity.Entity;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.entity.metadata.FlyingEntityMetadata;
import eu.mshade.enderframe.entity.metadata.SprintingEntityMetadata;
import eu.mshade.enderframe.event.PlayerMoveEvent;
import eu.mshade.enderframe.inventory.PlayerInventory;
import eu.mshade.enderframe.entity.metadata.EntityMetadataKey;
import eu.mshade.enderframe.metadata.MetadataKeyValue;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.enderframe.virtualserver.VirtualWorld;
import eu.mshade.enderframe.world.chunk.Chunk;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.Vector;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderframe.world.chunk.EmptySection;
import eu.mshade.enderframe.world.chunk.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultPlayer extends Player {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultPlayer.class);

    private final MinecraftSession minecraftSession;
    private Location lastServerChunkLocation;
    private final Queue<Watchable> watchables = new ConcurrentLinkedQueue<>();

    private long lastElapsedTick;

    public DefaultPlayer(Location location, Vector velocity, int entityId, UUID uuid, MinecraftSession minecraftSession) {
        super(location, velocity, entityId, uuid);

        /**
         * At future set player inventory from read data
         */
        this.setPlayerInventory(new PlayerInventory(getUniqueId()));
        this.getInventory().addWatcher(this);

        this.setGameProfile(minecraftSession.getGameProfile());
        this.setDisplayName(getGameProfile().getName());
        this.setMinecraftProtocolVersion(minecraftSession.getHandshake().getVersion());
        this.minecraftSession = minecraftSession;
    }

    public DefaultPlayer(Location location, int entityId, MinecraftSession minecraftSession) {
        this(location, new Vector(), entityId, minecraftSession.getGameProfile().getId(), minecraftSession);
    }

    @Override
    public void joinWorld(World world) {
        Map<Chunk, List<Integer>> updateChunks = new HashMap<>();
        try {
            if (world instanceof VirtualWorld) {

                for (Chunk lookAtChunk : new ConcurrentLinkedQueue<>(getLookAtChunks())) {
                    Chunk chunk = world.getChunk(lookAtChunk.getX(), lookAtChunk.getZ()).get();
                    this.getLookAtChunks().add(chunk);
                    lookAtChunk.removeWatcher(this);
                    chunk.addWatcher(this);
                    this.getLookAtChunks().remove(lookAtChunk);
                    updateChunks.put(chunk, (Stream.of(chunk.getSections()).filter(section -> (section instanceof VirtualSection virtualSection) && virtualSection.getVirtualSectionStatus() == VirtualSectionStatus.DIFFERENT).map(Section::getY).toList()));

                }

                sendSections(updateChunks);

                this.setLocation(new Location(world, this.getLocation().getX(), this.getLocation().getY(), this.getLocation().getZ(), this.getLocation().getYaw(), this.getLocation().getPitch()));
            }else {

                for (Chunk lookAtChunk : new ConcurrentLinkedQueue<>(getLookAtChunks())) {
                    Chunk chunk = world.getChunk(lookAtChunk.getX(), lookAtChunk.getZ()).get();
                    this.getLookAtChunks().add(chunk);
                    this.getLookAtChunks().remove(lookAtChunk);
                    lookAtChunk.removeWatcher(this);
                    chunk.addWatcher(this);
                    List<Section> sections = Stream.of(lookAtChunk.getSections()).filter(section -> (section instanceof VirtualSection virtualSection) && virtualSection.getVirtualSectionStatus() == VirtualSectionStatus.DIFFERENT).toList();
                    updateChunks.put(chunk, sections.stream().map(Section::getY).toList());
                }

                sendSections(updateChunks);

                this.setLocation(new Location(world, this.getLocation().getX(), this.getLocation().getY(), this.getLocation().getZ(), this.getLocation().getYaw(), this.getLocation().getPitch()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //filtre updateChunks show only chunk who value is not empty

//        System.out.println("updateSections = " + updateChunks.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).size());
    }

    @Override
    public MinecraftSession getMinecraftSession() {
        return this.minecraftSession;
    }

    /**
     * Fix le calcul de speed en passant par un elapsedTime et non par une valeur fix(0,05)
     */


    @Override
    public void tick() {

        //check if player are moved
        if(!this.getTickBeforeLocation().equals(this.getTickLocation())) {
            EnderFrame.get().getEnderFrameEventBus().publish(new PlayerMoveEvent(this, this.getTickBeforeLocation(), this.getTickBeforeLocation()));

            AxololtConnection.INSTANCE.send(axolotlSession -> axolotlSession.sendEntityLocation(this));
        }

        setTickLocation(getLocation());


        getLookAtEntity().forEach(entity -> {
            getMinecraftSession().sendUpdateLocation(entity, entity.getTickBeforeLocation(), entity.getTickLocation());
        });


        boolean hasChangeChunk = lastServerChunkLocation == null || (this.lastServerChunkLocation.getChunkX() != this.getLocation().getChunkX() || this.lastServerChunkLocation.getChunkZ() != this.getLocation().getChunkZ());

        if (hasChangeChunk) {
            Location location = this.getLocation();
            World world = location.getWorld();

            int radius = 10;


            long start = System.currentTimeMillis();
            Queue<CompletableFuture<Chunk>> askChunks = new ConcurrentLinkedQueue<>();

            int rSquared = radius * radius;
            this.lastServerChunkLocation = this.getLocation().clone();

            int chunkX = location.getChunkX();
            int chunkZ = location.getChunkZ();


            for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                    if ((chunkX - x) * (chunkX - x) + (chunkZ - z) * (chunkZ - z) <= rSquared) {
                        CompletableFuture<Chunk> chunk = world.getChunk(x, z);
                        askChunks.add(chunk);
                    }
                }
            }

            try {
                Void waitingAskChunk = CompletableFuture.allOf(askChunks.toArray(new CompletableFuture[0])).get();

                Queue<Chunk> result = askChunks.stream().map(CompletableFuture::join).distinct().collect(Collectors.toCollection(ConcurrentLinkedQueue::new));

                for (Chunk chunk : this.getLookAtChunks()) {
                    if (!result.contains(chunk)) {
                        this.getLookAtChunks().remove(chunk);
                        this.getMinecraftSession().sendUnloadChunk(chunk);
                        chunk.removeWatcher(this);
                    }
                }
                Queue<Chunk> newChunks = new ConcurrentLinkedQueue<>();
                result.stream().filter(chunk -> !hasLookAtChunk(chunk)).forEach(chunk -> {
                    this.getLookAtChunks().add(chunk);
                    chunk.addWatcher(this);
                    newChunks.add(chunk);
                });

                newChunks.forEach(chunk -> {
                    this.getMinecraftSession().sendChunk(chunk);

                });

//                this.getMinecraftSession().sendMessage(ChatColor.GREEN + "Chunk loaded in " + (System.currentTimeMillis() - start) + "ms, there are " + world.getChunks().size() + " chunks in all, new chunk " + newChunks.size(), TextPosition.HOT_BAR);
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("", e);
            }


                Set<Entity> entities = new HashSet<>();

                for (int x = chunkX - 5; x <= chunkX + 5; x++) {
                    for (int z = chunkZ - 5; z <= chunkZ + 5; z++) {
                        if ((chunkX - x) * (chunkX - x) + (chunkZ - z) * (chunkZ - z) <= 5 * 5) {
                            Chunk chunk = world.getChunk(x, z).join();
                            entities.addAll(chunk.getEntities());
                            chunk.getWatchers().stream().filter(target -> !target.equals(this) && target instanceof Entity).forEach(entity -> entities.add((Entity) entity));
                        }
                    }
                }

                Set<Entity> collect = entities.stream().filter(entity -> entity.getLocation().distanceXZ(this.getLocation()) <= 80).collect(Collectors.toSet());

                for (Entity entity : collect) {
                    if (!containsLookAtEntity(entity)) {
                        getMinecraftSession().sendEntity(entity);
                        this.addLookAtEntity(entity);
                    }
                }

                getLookAtEntity().stream().filter(entity -> !collect.contains(entity)).forEach(entity -> {
                    this.removeLookAtEntity(entity);
                    getMinecraftSession().removeEntity(entity);
                });
            }



        if (isPeriod(20)) {
            this.getMinecraftSession().sendKeepAlive((int) System.currentTimeMillis());
        }
    }





    @Override
    public String toString() {
        return "DefaultPlayer{" +
                "sessionWrapper=" + minecraftSession +
                '}';
    }

    @Override
    public String getAgent() {
        return this.getName();
    }

    @Override
    public void joinWatch(Watchable watchable) {
        watchable.addWatcher(this);
        watchables.add(watchable);
    }

    @Override
    public void leaveWatch(Watchable watchable) {
        watchable.removeWatcher(this);
        watchables.remove(watchable);
    }

    @Override
    public Collection<Watchable> getWatchings() {
        return this.watchables;
    }

    public void setPlayerInventory(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    public void sendSections(Map<Chunk, List<Integer>> chunkListMap) {

        chunkListMap.forEach((chunk, sections1) ->{
            if (sections1.size() >= 3){
                this.getMinecraftSession().sendUnloadChunk(chunk);
                this.getMinecraftSession().sendChunk(chunk);
            }else {
                sections1.forEach(section -> {
                    Section sectionSend = chunk.getSection(section);
                    if (sectionSend == null) {
                        sectionSend = new EmptySection(chunk, section);
                    }
                    this.getMinecraftSession().sendSection(sectionSend);
                });
            }
        });
    }
}
