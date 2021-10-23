package eu.mshade.enderchest.protocol.listener;

import eu.mshade.enderchest.DedicatedEnderChest;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.EnderFrameSession;
import eu.mshade.enderframe.EnderFrameSessionHandler;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.packetevent.PacketEncryptionEvent;
import eu.mshade.enderframe.packetevent.PacketFinallyJoinEvent;
import eu.mshade.enderframe.protocol.MinecraftEncryption;
import eu.mshade.mwork.MOptional;
import eu.mshade.mwork.ParameterContainer;
import eu.mshade.mwork.event.EventListener;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketEncryptionHandler implements EventListener<PacketEncryptionEvent> {

    private final DedicatedEnderChest dedicatedEnderChest;
    private final static Logger LOGGER = LoggerFactory.getLogger(PacketEncryptionHandler.class);
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public PacketEncryptionHandler(DedicatedEnderChest dedicatedEnderChest) {
        this.dedicatedEnderChest = dedicatedEnderChest;
    }

    @Override
    public void onEvent(PacketEncryptionEvent event, ParameterContainer eventContainer) {
        EnderFrameSessionHandler enderFrameSessionHandler = event.getEnderFrameSessionHandler();
        EnderFrameSession enderFrameSession = enderFrameSessionHandler.getEnderFrameSession();
        MinecraftEncryption minecraftEncryption = dedicatedEnderChest.getMinecraftEncryption();
        try {
            SecretKey secretKey = minecraftEncryption.getSecretKey(event.getSharedSecret());
            enderFrameSessionHandler.enableEncryption(secretKey);
            String hashServerId = minecraftEncryption.getHashServerId(enderFrameSession.getSessionId(), minecraftEncryption.getKeyPair().getPublic(), secretKey);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + enderFrameSession.getGameProfile().getName() + "&serverId=" + hashServerId))
                    .build();
            JSONObject jsonObject = new JSONObject(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body());
            System.out.println(jsonObject);
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
            enderFrameSession.setGameProfile(gameProfile);

            EnderFrame.get().getPacketEventBus().publish(new PacketFinallyJoinEvent(enderFrameSessionHandler));

        }catch (Exception e){
            LOGGER.error("", e);
        }
    }
}
