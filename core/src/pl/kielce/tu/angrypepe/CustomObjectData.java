package pl.kielce.tu.angrypepe;

/**
 * Informacje o obiekcie gry.
 * @author Patryk Eliasz, Karol Rębiś */
public class CustomObjectData {

    /**
     * Nazwa obiektu.
     */
    public String name;
    /**
     * Identyfikator.
     */
    public Integer id;
    /**
     * Zdrowie obiektu.
     */
    public Float hp = 100f;
    /**
     * Czy jest zniszczalny?
     */
    public boolean isDestructible = true;
    /**
     * Wytrzymałość obiektu.
     */
    public float durability = 1f;

    /**
     * Kontruktor nowej instacji informacji o obiekcie.
     *
     * @param id identyfikator
     */
    public CustomObjectData(Integer id) {
        this.id = id;
    }

    /**
     * Kontruktor nowej instacji informacji o obiekcie.
     *
     * @param name       nazwa obiektu
     * @param hp         zdrowie obiektu
     * @param durability wytrzymałość obienktu
     */
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

    /**
     * Pobiera nazwę obiektu.
     *
     * @return zwraca jego nazwę
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę obiektu.
     *
     * @param name nowa nazwa obiektu
     * @return instancja obiektu informacji o obiekcie gry
     */
    public CustomObjectData setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Pobiera identyfikator
     *
     * @return identyfikator obiektu
     */
    public Integer getId() {
        return id;
    }

    /**
     * Ustawia identyfikator obiektu
     *
     * @param id nowy identyfikator obiektu
     * @return instancja obiektu informacji o obiekcie gry
     */
    public CustomObjectData setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Pobiera zdrowie
     *
     * @return zdrowie
     */
    public Float getHp() {
        return hp;
    }

    /**
     * Ustawia nowe zdrowie obiektu na podstawie zmiany.
     *
     * @param change zmiana
     * @return instancja obiektu informacji o obiekcie gry
     */
    public CustomObjectData updateHp(float change) {
        if (this.isDestructible())
            this.hp -= change * this.durability * 5;
        return this;
    }

    /**
     * Ustawia zdrowie
     *
     * @param hp zdrowie
     * @return instancja obiektu informacji o obiekcie gry
     */
    public CustomObjectData setHp(Float hp) {
        this.hp = hp;
        return this;
    }

    /**
     * Czy obiekt jest zniszczalny?
     *
     * @return wartość logiczna
     */
    public boolean isDestructible() {
        return isDestructible;
    }

    /**
     * Ustawia wartość logiczną "zniszczalności"
     *
     * @param destructible nowa wartość logiczna zniszczlności
     * @return instancja obiektu informacji o obiekcie gry
     */
    public CustomObjectData setDestructible(boolean destructible) {
        this.isDestructible = destructible;
        return this;
    }

    /**
     * Pobiera wytrzymałość
     *
     * @return wytrzymałość
     */
    public float getDurability() {
        return durability;
    }

    /**
     * Ustawia wytrzymałość
     *
     * @param durability nowa wytrzymałość
     * @return instancja obiektu informacji o obiekcie gry
     */
    public CustomObjectData setDurability(float durability) {
        this.durability = durability;
        return this;
    }

}
