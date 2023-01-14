package eu.mshade.enderchest.listener.packet;

import com.fasterxml.jackson.databind.JsonNode;
import eu.mshade.enderchest.EnderChest;
import eu.mshade.enderframe.EnderFrame;
import eu.mshade.enderframe.event.PrePlayerJoinEvent;
import eu.mshade.enderframe.mojang.GameProfile;
import eu.mshade.enderframe.mojang.Property;
import eu.mshade.enderframe.packetevent.MinecraftPacketEncryptionEvent;
import eu.mshade.enderframe.protocol.MinecraftEncryption;
import eu.mshade.enderframe.protocol.MinecraftSession;
import eu.mshade.mwork.MWork;
import eu.mshade.mwork.event.EventListener;
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

public class MinecraftPacketEncryptionListener implements EventListener<MinecraftPacketEncryptionEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(MinecraftPacketEncryptionListener.class);
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private EnderChest enderChest;

    public MinecraftPacketEncryptionListener(EnderChest enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onEvent(MinecraftPacketEncryptionEvent event) {
        MinecraftSession minecraftSession = event.getMinecraftSession();
        MinecraftEncryption minecraftEncryption = enderChest.getMinecraftEncryption();

        try {
            SecretKey secretKey = minecraftEncryption.getSecretKey(event.getSharedSecret());
            minecraftSession.enableEncryption(secretKey);
            String hashServerId = minecraftEncryption.getHashServerId(minecraftSession.getSessionId(), minecraftEncryption.getKeyPair().getPublic(), secretKey);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + minecraftSession.getGameProfile().getName() + "&serverId=" + hashServerId))
                    .build();
            String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            if (body.isEmpty() || body.isBlank()) {
                minecraftSession.sendDisconnect("You are not logged into Minecraft account. If you logged into you Minecraft account, try restarting you Minecraft client.");
                return;
            }

            JsonNode jsonNode = MWork.get().getObjectMapper().readTree(body);
            String id = jsonNode.get("id").asText();
            UUID uuid = UUID.fromString(id.substring(0, 8)
                    + "-" + id.substring(8, 12)
                    + "-" + id.substring(12, 16)
                    + "-" + id.substring(16, 20)
                    + "-" + id.substring(20, 32));
            List<Property> properties = new ArrayList<>();
            jsonNode.get("properties").elements().forEachRemaining(property -> {
                String name = property.get("name").asText();
                String value = property.get("value").asText();
                String signature = property.get("signature").asText();
                properties.add(new Property(name, value, signature));
            });

            GameProfile gameProfile = new GameProfile(uuid, jsonNode.get("name").asText(), properties);
            minecraftSession.setGameProfile(gameProfile);

            EnderFrame.get().getEnderFrameEventBus().publish(new PrePlayerJoinEvent(minecraftSession));
        }catch (Exception e){
            LOGGER.error("", e);
        }

    }
}
