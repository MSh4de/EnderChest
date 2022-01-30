package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.*;
import eu.mshade.enderframe.entity.Player;
import eu.mshade.enderframe.metadata.MetadataMeaning;
import eu.mshade.enderframe.packetevent.PacketFinallyJoinEvent;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.WorldBuffer;
import eu.mshade.enderman.packet.play.PacketOutSetSlot;
import eu.mshade.mwork.ParameterContainer;
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
    public void onEvent(PacketFinallyJoinEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = event.getEnderFrameSessionHandler();
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();

        enderFrameSession.sendCompression(256);
        enderFrameSession.sendLoginSuccess();

        WorldBuffer world = dedicatedEnderChest.getWorldManager().getWorldBuffer("world");
        Location location = new Location(world, 7, 0, 7);
        int highest = location.getChunkBuffer().getHighest(location.getBlockX(), location.getBlockZ());
        location.setY(highest+1);
        Player player = world.spawnPlayer(enderFrameSessionHandler,location);
        enderFrameSession.setPlayer(player);


        enderFrameSession.sendJoinGame(GameMode.CREATIVE, world.getWorldLevel().getDimension(), world.getWorldLevel().getDifficulty(), 20, world.getWorldLevel().getLevelType(), false);
        enderFrameSession.sendAbilities(false, false, true, false, 0.1F, 0.1F);

        dedicatedEnderChest.addPlayer(player);

        enderFrameSession.sendMetadata(player, MetadataMeaning.SKIN_PART);

        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        dedicatedEnderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        dedicatedEnderChest.getPlayers().forEach(target -> target.getEnderFrameSessionHandler().getEnderFrameSession().sendPlayerInfo(playerInfoBuilder));


        //enderFrameSessionHandler.sendPacket(new PacketOutSetSlot());


        dedicatedEnderChest.getPlayers().forEach(target ->{
            target.sendMessage(String.format("%s join server", enderFrameSession.getPlayer().getGameProfile().getName()));
        });

        //enderFrameSession.getPlayer().getEnderFrameSessionHandler().sendPacket(new PacketOutSpawnPosition(new BlockPosition(location.getBlockX(),location.getBlockY(),location.getBlockZ())));

        //enderFrameSession.sendPosition(location);
        enderFrameSession.sendSquareChunk(10, location.getChunkX(), location.getChunkZ(), world);
        enderFrameSession.sendPosition(location);


        logger.info(String.format("%s join server", enderFrameSession.getPlayer().getGameProfile().getName()));

        enderFrameSession.sendMessage("Welcome to project MShade");
        enderFrameSession.sendPlayerList("Hey this is test", "and this is test");



    }


}
