package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Rideable;

public class DefaultRideable implements Rideable {

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
