package pl.kielce.tu.angrypepe;

public class CustomObjectData {

    private String name;
    private Integer id;
    private Float hp = 100f;
    private boolean isDestructible = true;
    private float durability = 1f;

    public CustomObjectData(Integer id) {
        this.id = id;
    }

    public CustomObjectData(String name, Float hp, float durability) {
        this.name = name;
        // 0 hp - niezniszczalny
        if (hp == 0) {
            this.isDestructible = false;
            this.durability = 1f;
            this.hp = 100000f;
            return;
        }
        this.hp = hp;
        this.durability = durability;
    }

    @Override
    public String toString() {
        return "CustomObjectData {" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", hp=" + hp +
                ", isDestructible=" + isDestructible +
                '}';
    }

    public String getName() {
        return name;
    }

    public CustomObjectData setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public CustomObjectData setId(Integer id) {
        this.id = id;
        return this;
    }

    public Float getHp() {
        return hp;
    }

    public CustomObjectData updateHp(float change) {
        if (this.isDestructible())
            this.hp -= change * this.durability * 5;
        return this;
    }

    public CustomObjectData setHp(Float hp) {
        this.hp = hp;
        return this;
    }

    public boolean isDestructible() {
        return isDestructible;
    }

    public CustomObjectData setDestructible(boolean destructible) {
        this.isDestructible = destructible;
        return this;
    }

    public float getDurability() {
        return durability;
    }

    public CustomObjectData setDurability(float durability) {
        this.durability = durability;
        return this;
    }

}
