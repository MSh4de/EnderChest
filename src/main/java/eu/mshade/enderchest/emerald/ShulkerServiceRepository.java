package eu.mshade.shulker;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ShulkerServiceRepository {

    private Queue<ShulkerService> shulkerServices = new ConcurrentLinkedQueue<>();
    private Map<Long, ShulkerService> shulkerServiceById = new ConcurrentHashMap<>();

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


}
