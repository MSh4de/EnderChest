package eu.mshade.enderchest.protocol.listener;

import com.google.inject.Inject;
import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.packetevent.PacketEncryptionEvent;
import eu.mshade.enderframe.packetevent.PacketFinallyJoinEvent;
import eu.mshade.enderframe.protocol.MinecraftEncryption;
import eu.mshade.enderframe.protocol.ProtocolPipeline;
import eu.mshade.enderframe.protocol.SessionWrapper;
import eu.mshade.mwork.MOptional;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import io.netty.channel.Channel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketEncryptionHandler implements EventListener<PacketEncryptionEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PacketEncryptionHandler.class);
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Inject
    private EnderChest enderChest;



    @Override
    public void onEvent(PacketEncryptionEvent event, ParameterContainer parameterContainer) {
        ProtocolPipeline protocolPipeline = ProtocolPipeline.get();
        Channel channel = parameterContainer.getContainer(Channel.class);
        SessionWrapper sessionWrapper = protocolPipeline.getSessionWrapper(channel);
        MinecraftEncryption minecraftEncryption = enderChest.getMinecraftEncryption();

        try {
            SecretKey secretKey = minecraftEncryption.getSecretKey(event.getSharedSecret());
            sessionWrapper.enableEncryption(secretKey);
            String hashServerId = minecraftEncryption.getHashServerId(sessionWrapper.getSessionId(), minecraftEncryption.getKeyPair().getPublic(), secretKey);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + sessionWrapper.getGameProfile().getName() + "&serverId=" + hashServerId))
                    .build();
            String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            if (body.isEmpty() || body.isBlank()) {
                sessionWrapper.sendDisconnect("You are not logged into Minecraft account. If you logged into you Minecraft account, try restarting you Minecraft client.");
                return;
            }
            JSONObject jsonObject = new JSONObject(body);
            String id = jsonObject.getString("id");
            UUID uuid = UUID.fromString(id.substring(0, 8)
                    + "-" + id.substring(8, 12)
                    + "-" + id.substring(12, 16)
                    + "-" + id.substring(16, 20)
                    + "-" + id.substring(20, 32));
            List<Property> properties = new ArrayList<>();
            for (Object obj : jsonObject.getJSONArray("properties")) {
                JSONObject propJson = (JSONObject) obj;
                String name = propJson.getString("name");
                String value = propJson.getString("value");
                MOptional<String> signature = (propJson.has("signature") ? MOptional.of(propJson.getString("signature")) : MOptional.empty());
                properties.add(new Property(name, value, signature));
            }

            GameProfile gameProfile = new GameProfile(uuid, jsonObject.getString("name"), properties);
            sessionWrapper.setGameProfile(gameProfile);

            EnderFrame.get().getPacketEventBus().publish(new PacketFinallyJoinEvent(), parameterContainer);
        }catch (Exception e){
            LOGGER.error("", e);
        }

    }
}
