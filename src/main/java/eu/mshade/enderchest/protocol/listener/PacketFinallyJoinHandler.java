package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.*;
import eu.mshade.enderframe.event.entity.PacketFinallyJoinEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.mwork.event.EventContainer;
import eu.mshade.mwork.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketFinallyJoinHandler implements EventListener<PacketFinallyJoinEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PacketFinallyJoinHandler.class);
    private DedicatedEnderChest dedicatedEnderChest;

    public PacketFinallyJoinHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketFinallyJoinEvent event, EventContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = eventContainer.getContainer(EnderFrameSessionHandler.class);
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();

        enderFrameSession.sendCompression(256);
        enderFrameSession.sendLoginSuccess();

        WorldBuffer world = dedicatedEnderChest.getWorldManager().getWorldBuffer("world");
        Location location = new Location(world, 7.5, 0, 7.5);
        int highest = location.getChunkBuffer().getHighest(location.getBlockX(), location.getBlockZ());
        location.setY(highest+1);
        enderFrameSession.setLocation(location);

        enderFrameSession.sendJoinGame(GameMode.CREATIVE, world.getWorldLevel().getDimension(), world.getWorldLevel().getDifficulty(), 20, world.getWorldLevel().getLevelType(), false);
        enderFrameSession.sendSquareChunk(10, location.getChunkX(), location.getChunkZ(), world);
        enderFrameSession.sendPosition(location);
        enderFrameSession.sendAbilities(false, false, true, false, 0.5F, 0.2F);


        dedicatedEnderChest.addPlayer(enderFrameSession);

        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        dedicatedEnderChest.getEnderFrameSessions().forEach(playerInfoBuilder::withPlayer);
        dedicatedEnderChest.getEnderFrameSessions().forEach(target -> target.sendPlayerInfo(playerInfoBuilder));

        /*
        dedicatedEnderChest.getEnderFrameSessions().forEach(target -> {
            if (target != enderFrameSession)
            target.getEnderFrameSessionHandler().sendPacket(new PacketOutSpawnPlayer(1, enderFrameSession.getGameProfile().getId(), new Position(0, 112, 0, false)));
        });

         */
        logger.info(String.format("%s join server", enderFrameSession.getGameProfile().getName()));

        enderFrameSession.sendMessage("Welcome to project MShade");
        enderFrameSession.sendPlayerList("Hey this is test", "and this is test");

    }
}
