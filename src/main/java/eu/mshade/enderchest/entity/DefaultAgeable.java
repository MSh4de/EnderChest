package eu.mshade.enderchest.entity;

public class DefaultAgeable {

    private int age;
    private boolean ageLocked;

    public DefaultAgeable(int age, boolean ageLocked) {
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

}
