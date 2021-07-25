package eu.mshade.enderchest.redstone;

import eu.mshade.enderchest.redstone.protocol.RedstonePacketIn;
import eu.mshade.enderchest.redstone.protocol.RedstonePacketOut;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Redstone {

    private Map<Integer, CompletableFuture<RedstonePacketIn>> integerCompletableFuture = new ConcurrentHashMap<>();
    private Queue<RedstoneSession> redstoneSessions = new ConcurrentLinkedQueue<>();

    public <T extends RedstonePacketIn> CompletableFuture<T> sendPacket(RedstoneSession redstoneSession, RedstonePacketOut redstonePacketOut, Class<T> redstonePacketIn){
        CompletableFuture<RedstonePacketIn> completableFuture = new CompletableFuture<>();
        integerCompletableFuture.put(redstonePacketOut.getId(), completableFuture);
        redstoneSession.sendPacket(redstonePacketOut);
        return (CompletableFuture<T>) completableFuture;
    }

    public CompletableFuture<RedstonePacketIn> getAwaitRedstonePacketIn(int id){
        return integerCompletableFuture.get(id);
    }

    public void addRedstoneSession(RedstoneSession redstoneSession){
        redstoneSessions.add(redstoneSession);
    }

    public void removeRedstoneSession(RedstoneSession redstoneSession){
        redstoneSessions.remove(redstoneSession);
    }



}
