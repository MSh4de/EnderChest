package eu.mshade.enderchest.emerald;

import eu.mshade.shulker.ShulkerService;
import eu.mshade.shulker.protocol.ShulkerPacketType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ShulkerServiceRepository {

    private Queue<ShulkerService> shulkerServices = new ConcurrentLinkedQueue<>();
    private Map<Long, ShulkerService> shulkerServiceById = new ConcurrentHashMap<>();
    private Map<ShulkerPacketType, List<ShulkerService>> subscriberPacketType = new ConcurrentHashMap<>();

    public void addShulkerService(ShulkerService shulkerService){
        this.shulkerServices.add(shulkerService);
        this.shulkerServiceById.put(shulkerService.getId(), shulkerService);
    }

    public void removeShulkerService(ShulkerService shulkerService){
        removeShulkerService(shulkerService.getId());
    }

    public void removeShulkerService(long id){
        Optional.ofNullable(this.shulkerServiceById.remove(id)).ifPresent(shulkerService -> shulkerServices.remove(shulkerService));
    }

    public ShulkerService getShulkerService(long id){
        return this.shulkerServiceById.get(id);
    }

    public Queue<ShulkerService> getShulkerServices(){
        return new ConcurrentLinkedQueue<>(this.shulkerServices);
    }

    public List<ShulkerService> getSubscriber(ShulkerPacketType shulkerPacketType){
        return this.subscriberPacketType.computeIfAbsent(shulkerPacketType, key -> new ArrayList<>());
    }

    public void addSubscriber(ShulkerService shulkerService, ShulkerPacketType shulkerPacketType){
        this.subscriberPacketType.computeIfAbsent(shulkerPacketType, key -> new ArrayList<>()).add(shulkerService);
    }

    public void removeSubscriber(ShulkerService shulkerService, ShulkerPacketType shulkerPacketType){
        this.subscriberPacketType.computeIfAbsent(shulkerPacketType, key -> new ArrayList<>()).remove(shulkerService);
    }


}
