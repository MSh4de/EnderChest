package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Damageable;

public class DefaultDamageableEntity implements Damageable {

    private float damageTaken;

    public DefaultDamageableEntity(float damageTaken) {
        this.damageTaken = damageTaken;
    }

    @Override
    public float getDamageTaken() {
        return this.damageTaken;
    }

    @Override
    public void setDamageTaken(float damageTaken) {
        this.damageTaken = damageTaken;
    }
}
