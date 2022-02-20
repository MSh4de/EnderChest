package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Rideable;

public class DefaultRideableEntity implements Rideable {

    private boolean hasSaddle;

    public DefaultRideableEntity(boolean hasSaddle) {
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
