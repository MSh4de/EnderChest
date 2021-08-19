package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.*;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.event.entity.PacketFinallyJoinEvent;
import eu.mshade.enderframe.protocol.packet.PacketOutSpawnPosition;
import eu.mshade.enderframe.world.BlockPosition;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.enderman.packet.play.PacketOutSetSlot;
import eu.mshade.enderman.packet.play.PacketOutSpawnPlayer;
import eu.mshade.mwork.event.EventListener;
import eu.mshade.mwork.event.ParameterContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketFinallyJoinHandler implements EventListener<PacketFinallyJoinEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PacketFinallyJoinHandler.class);
    private DedicatedEnderChest dedicatedEnderChest;

    public PacketFinallyJoinHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketFinallyJoinEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();

        enderFrameSession.sendCompression(256);
        enderFrameSession.sendLoginSuccess();

        WorldBuffer world = dedicatedEnderChest.getWorldManager().getWorldBuffer("world");
        Location location = new Location(world, 7, 0, 7);
        int highest = location.getChunkBuffer().getHighest(location.getBlockX(), location.getBlockZ());
        location.setY(highest+1);
        System.out.println(location);
        enderFrameSession.setLocation(location);

<<<<<<< HEAD
        enderFrameSession.sendJoinGame(GameMode.CREATIVE, world.getWorldLevel().getDimension(), world.getWorldLevel().getDifficulty(), 20, world.getWorldLevel().getLevelType(), false);
        enderFrameSession.sendAbilities(false, false, true, false, 0.5F, 0.2F);
=======
>>>>>>> feature/marshal

<<<<<<< HEAD
        enderFrameSessionHandler.sendPacket(new PacketOutSpawnPosition(new BlockPosition(7, location.getBlockY(), 7)));

=======
>>>>>>> feature/recovery
        enderFrameSession.sendJoinGame(GameMode.CREATIVE, world.getWorldLevel().getDimension(), world.getWorldLevel().getDifficulty(), 20, world.getWorldLevel().getLevelType(), false);
        enderFrameSession.sendAbilities(false, false, true, false, 0.2F, 0.2F);

        dedicatedEnderChest.addPlayer(enderFrameSession);

        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        dedicatedEnderChest.getEnderFrameSessions().forEach(playerInfoBuilder::withPlayer);
<<<<<<< HEAD
        dedicatedEnderChest.getEnderFrameSessions().forEach(target -> target.sendPlayerInfo(playerInfoBuilder));
        enderFrameSession.sendPosition(location);
        //enderFrameSessionHandler.sendPacket(new PacketOutEntityVelocity(0, new Vector(0, 2, 0)));
        enderFrameSession.sendSquareChunk(10, location.getChunkX(), location.getChunkZ(), world);
        enderFrameSession.sendPosition(location);

        enderFrameSessionHandler.sendPacket(new PacketOutSetSlot());
        enderFrameSessionHandler.sendPacket(new PacketOutSpawnPlayer(1, enderFrameSession.getGameProfile().getId(), 7, location.getBlockY(), 7));


        dedicatedEnderChest.getEnderFrameSessions().forEach(target ->{
            target.sendPlayerInfo(playerInfoBuilder);
            target.sendMessage(String.format("%s join server", enderFrameSession.getGameProfile().getName()));
        });

        Player player = world.spawnPlayer(enderFrameSessionHandler,location);
        enderFrameSession.setPlayer(player);
        enderFrameSession.getEnderFrameSessionHandler().sendPacket(new PacketOutSpawnPosition(new BlockPosition(location.getBlockX(),location.getBlockY(),location.getBlockZ())));

        enderFrameSession.sendPosition(location);
        enderFrameSession.sendSquareChunk(10, location.getChunkX(), location.getChunkZ(), world);
        enderFrameSession.sendPosition(location);

        logger.info(String.format("%s join server", enderFrameSession.getGameProfile().getName()));

        enderFrameSession.sendMessage("Welcome to project MShade");
        enderFrameSession.sendPlayerList("Hey this is test", "and this is test");
        //enderFrameSessionHandler.sendPacket(new PacketOutSpawnEntity(2));
        //PacketOutSpawnMob packetOutSpawnMob = new PacketOutSpawnMob(2,54, 7, 52, 7);
        //enderFrameSessionHandler.sendPacket(packetOutSpawnMob);
        /*enderFrameSessionHandler.sendPacket(new PacketOutEntityHeadLook(2,0));
        enderFrameSessionHandler.sendPacket(new PacketOutEntityMetadata(2, EntityType.ZOMBIE, packetOutSpawnMob.get()));
        enderFrameSessionHandler.sendPacket(new PacketOutEntityTeleport(2,7*32,53*32,7*32,(byte) 0,(byte) 0,false));
        enderFrameSessionHandler.sendPacket(new PacketOutEntityProperties(2, attributeProperties));
        enderFrameSessionHandler.sendPacket(new PacketOutSpawnPlayer(3, enderFrameSession.getGameProfile().getId(), new Position(0, highest+1, 0, false)));*/
    }
}
