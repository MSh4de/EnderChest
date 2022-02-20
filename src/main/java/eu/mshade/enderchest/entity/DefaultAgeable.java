package eu.mshade.enderchest.entity;

import eu.mshade.enderframe.entity.Ageable;

public class DefaultAgeableEntity implements Ageable {

    private int age;
    private boolean ageLocked;

    public DefaultAgeableEntity(int age, boolean ageLocked) {
        this.age = age;
        this.ageLocked = ageLocked;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public void setAgeLock(boolean ageLock) {
        this.ageLocked = ageLock;
    }

    @Override
    public boolean getAgeLock() {
        return this.ageLocked;
    }

    @Override
    public void setBaby() {
        if (this.isAdult()) {
            this.setAge(-24000);
        }
    }

    @Override
    public void setAdult() {
        if (!this.isAdult()) {
            this.setAge(0);
        }
    }

    @Override
    public boolean isAdult() {
        return this.getAge() >= 0;
    }

    @Override
    public boolean canBreed() {
        return this.getAge() == 0;
    }

    @Override
    public void setBreed(boolean isBreedable) {
        if (isBreedable) this.setAge(0);
        else this.setAge(6000);
    }
}
