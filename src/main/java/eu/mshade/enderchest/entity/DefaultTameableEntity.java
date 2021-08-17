package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Tameable;

public class DefaultTameableEntity implements Tameable {

    private boolean isSitting;
    private boolean isTamed;
    private final String owner;

    public DefaultTameableEntity(boolean isSitting, boolean isTamed, String owner) {
        this.isSitting = isSitting;
        this.isTamed = isTamed;
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
        return this.isTamed;
    }

    @Override
    public void setTamed(boolean isTamed) {
        this.isTamed = isTamed;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }
}
