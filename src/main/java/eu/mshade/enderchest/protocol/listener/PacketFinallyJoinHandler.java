package eu.mshade.enderchest.protocol.listener;

import com.google.inject.Inject;
import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderchest.entity.DefaultPlayer;
import eu.mshade.enderframe.GameMode;
import eu.mshade.enderframe.PlayerInfoBuilder;
import eu.mshade.enderframe.PlayerInfoType;
import eu.mshade.enderframe.entity.metadata.SkinPartEntityMetadata;
import eu.mshade.enderframe.metadata.entity.EntityMetadataKey;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.SkinPart;
import eu.mshade.enderframe.packetevent.PacketFinallyJoinEvent;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.enderframe.world.Location;
import eu.mshade.enderframe.world.World;
import eu.mshade.enderman.packet.play.PacketOutChangeGameState;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketFinallyJoinHandler implements EventListener<PacketFinallyJoinEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PacketFinallyJoinHandler.class);
    @Inject
    private EnderChest enderChest;

    @Override
    public void onEvent(PacketFinallyJoinEvent event, ParameterContainer parameterContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = parameterContainer.getContainer(Channel.class);
        SessionWrapper sessionWrapper = protocolPipeline.getSessionWrapper(channel);
        GameProfile gameProfile = sessionWrapper.getGameProfile();

        sessionWrapper.sendCompression(256);
        sessionWrapper.sendLoginSuccess();


        World world = enderChest.getWorldManager().getWorld("world");
        Location location = new Location(world, 7, 0, 7);
        int highest = location.getChunk().getHighest(location.getBlockX(), location.getBlockZ());
        location.setY(highest+1);

        DefaultPlayer player = new DefaultPlayer(location, gameProfile.getId().hashCode(), sessionWrapper);
        player.setGameMode(GameMode.ADVENTURE);
        protocolPipeline.setPlayer(channel, player);



        sessionWrapper.sendJoinGame(world, false);
        sessionWrapper.sendPluginMessage("MC|Brand", protocolBuffer -> protocolBuffer.writeString("Enderchest"));
        //default value of flying speed as 0.05
        sessionWrapper.sendAbilities(false, false, true, false, 0.05F, 0.1F);

        enderChest.addPlayer(player);

        player.getMetadataKeyValueBucket().setMetadataKeyValue(new SkinPartEntityMetadata(new SkinPart(true, true, true, true, true, true, true)));
        sessionWrapper.sendMetadata(player, EntityMetadataKey.SKIN_PART);


        PlayerInfoBuilder playerInfoBuilder = PlayerInfoBuilder.of(PlayerInfoType.ADD_PLAYER);
        enderChest.getPlayers().forEach(playerInfoBuilder::withPlayer);
        enderChest.getPlayers().forEach(target -> target.getSessionWrapper().sendPlayerInfo(playerInfoBuilder));

        sessionWrapper.sendPacket(new PacketOutChangeGameState());


        player.joinTickBus(enderChest.getTickBus());
        /*
        sessionWrapper.sendPacket(new PacketOutSetSlot(36, new ItemStack(Material.LEATHER_HELMET, 1, 0)));
        sessionWrapper.sendPacket(new PacketOutSetSlot(37, new ItemStack(Material.LEATHER_CHESTPLATE, 1, 0)));
        sessionWrapper.sendPacket(new PacketOutSetSlot(38, new ItemStack(Material.LEATHER_LEGGINGS, 1, 0)));
        sessionWrapper.sendPacket(new PacketOutSetSlot(39, new ItemStack(Material.LEATHER_BOOTS, 1, 0)));
        sessionWrapper.sendPacket(new PacketOutSetSlot(40, new ItemStack(Material.MAGENTA_BANNER, 1, 0)));

         */


        /*
        dedicatedEnderChest.getPlayers().forEach(target ->{
            target.sendMessage(String.format("%s join server", enderFrameSession.getPlayer().getGameProfile().getName()));
        });

         */

        //enderFrameSession.getPlayer().getEnderFrameSessionHandler().sendPacket(new PacketOutSpawnPosition(new BlockPosition(location.getBlockX(),location.getBlockY(),location.getBlockZ())));

        //enderFrameSession.sendPosition(location);
        //sessionWrapper.sendSquareChunk(10, location.getChunkX(), location.getChunkZ(), world);
        sessionWrapper.teleport(location);


        logger.info(String.format("%s join server", player.getGameProfile().getName()));

        sessionWrapper.sendMessage("Welcome to project MShade");
        sessionWrapper.sendHeadAndFooter("Hey this is test", "and this is test");



    }


}
