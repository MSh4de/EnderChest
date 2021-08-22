package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Tameable;

public class DefaultTameableEntity implements Tameable {

    private boolean isSitting;
    private boolean isTame;
    private final String owner;

    public DefaultTameableEntity(boolean isSitting, boolean isTame, String owner) {
        this.isSitting = isSitting;
        this.isTame = isTame;
        this.owner = owner;
    }

    @Override
    public boolean isSitting() {
        return this.isSitting;
    }

    @Override
    public void setSitting(boolean isSitting) {
        this.isSitting = isSitting;
    }

    @Override
    public boolean isTamed() {
        return isTame;
    }

    @Override
    public void setTamed(boolean isTame) {
        this.isTame = isTame;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }
}
