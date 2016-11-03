package pl.kielce.tu.angrypepe;

/**
 * Created by patryk on 03.11.2016.
 */
public class CustomObjectData {

    private String name;
    private Integer id;
    private Float hp = 100f;
    private boolean indestructible = false;
    private float durability = 1f;

    public CustomObjectData(String name, Integer id, Float hp) {
        this.name = name;
        this.id = id;
        this.hp = hp;
    }

    @Override
    public String toString() {
        return "CustomObjectData {" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", hp=" + hp +
                ", indes=" + indestructible +
                '}';
    }

    public CustomObjectData() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getHp() {
        return hp;
    }

    public void updateHp(float change) {
        this.hp -= change * this.durability;
    }

    public void setHp(Float hp) {
        this.hp = hp;
    }

    public boolean isIndestructible() {
        return indestructible;
    }

    public void setIndestructible(boolean indestructible) {
        this.indestructible = indestructible;
    }

    public float getDurability() {
        return durability;
    }

    public void setDurability(float durability) {
        this.durability = durability;
    }

}
