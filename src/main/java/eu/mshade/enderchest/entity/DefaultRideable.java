package eu.mshade.enderchest.entity;

public class DefaultRideable {

    private boolean hasSaddle;

    public DefaultRideable(boolean hasSaddle) {
        this.hasSaddle = hasSaddle;
    }

    @Override
    public boolean hasSaddle() {
        return this.hasSaddle;
    }

    @Override
    public void setSaddle(boolean hasSaddle) {
        this.hasSaddle = hasSaddle;
    }
}
