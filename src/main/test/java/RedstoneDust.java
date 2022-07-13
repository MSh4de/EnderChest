public class RedstoneDust implements Tickable{


    private boolean active = false;
    private ChunkState chunkState = ChunkState.ACTIVE;
    private long lastActive = 0;
    private int tick = 0;

    @Override
    public void tick() {
        if (chunkState == ChunkState.PASSIVE) return;

        tick++;
        if (tick == 48) {

            tick = 0;
        }

    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setChunkState(ChunkState chunkState) {
        this.chunkState = chunkState;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }
}
