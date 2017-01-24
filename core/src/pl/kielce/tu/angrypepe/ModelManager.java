package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;
import java.lang.String;
import java.util.ArrayList;

/**
 * Klasa zarządzająca modelami.
 * @author Patryk Eliasz, Karol Rębiś */
public final class ModelManager implements Disposable{

    /**
     * Statyczny model gry - SPHERE.
     */
    public static Model SPHERE;
    /**
     * TStatyczny model gry - BOX.
     */
    public static Model BOX;
    /**
     * Statyczny model gry - RECTANGLE.
     */
    public static Model RECTANGLE;
    /**
     * Statyczny model gry - GROUND.
     */
    public static Model GROUND;
    /**
     * Statyczny model gry - CYLINDER.
     */
    public static Model CYLINDER;

    /**
     * Nazwa modelu do załadowania - PEPE.
     */
    protected static String PEPE = "pepe_box.g3dj";
    /**
     * Nazwa modelu do załadowania -  SKYDOME.
     */
    protected static String SKYDOME = "skydome.g3dj";
    /**
     * Nazwa modelu do załadowania -  TEST.
     */
    protected static String TEST = "123.g3dj";
    /**
     * Nazwa modelu do załadowania -  POLY_PEPE_1.
     */
    protected static String POLY_PEPE_1 = "poly_pepe1.g3dj";
    /**
     * Nazwa modelu do załadowania -  POLY_PEPE_2.
     */
    protected static String POLY_PEPE_2 = "poly_pepe2.g3dj";
    /**
     * Nazwa modelu do załadowania - SOWA_S.
     */
    protected static String SOWA_S = "sowa_s.g3dj";
    /**
     * Nazwa modelu do załadowania - STREET_LAMP.
     */
    protected static String STREET_LAMP = "Streetlamp.g3dj";
    /**
     * Nazwa modelu do załadowania - DREWNO.
     */
    protected static String DREWNO = "drewno.g3dj";
    /**
     * Nazwa modelu do załadowania - OIL_BARREL.
     */
    protected static String OIL_BARREL = "OilBarrel.g3dj";
    /**
     * Nazwa modelu do załadowania - WOODEN_BOX.
     */
    protected static String WOODEN_BOX = "Wooden_box.g3dj";
    /**
     * Nazwa modelu do załadowania - PLASZCZYZNA.
     */
    protected static String PLASZCZYZNA = "plaszczyzna.g3dj";
    /**
     * Nazwa modelu do załadowania - TREE.
     */
    protected static String TREE = "toon-pine-tree.g3dj";

    /**
     * Nazwa cząsteczki do załadowania -  PARTICLE.
     */
    public static String PARTICLE = "cloudpuff.pfx";

    /**
     * Statyczny model PEPE_MODEL.
     */
    public static Model PEPE_MODEL;
    /**
     * Statyczny model SKYDOME_MODEL.
     */
    public static Model SKYDOME_MODEL;
    /**
     * Statyczny model TEST_MODEL.
     */
    public static Model TEST_MODEL;
    /**
     * Statyczny model POLY_PEPE_1_MODEL.
     */
    public static Model POLY_PEPE_1_MODEL;
    /**
     * Statyczny model POLY_PEPE_2_MODEL.
     */
    public static Model POLY_PEPE_2_MODEL;
    /**
     * Statyczny model SOWA_S_MODEL.
     */
    public static Model SOWA_S_MODEL;
    /**
     * Statyczny model STREET_LAMP_MODEL.
     */
    public static Model STREET_LAMP_MODEL;
    /**
     * Statyczny model DREWNO_MODEL.
     */
    public static Model DREWNO_MODEL;
    /**
     * Statyczny model OIL_BARREL_MODEL.
     */
    public static Model OIL_BARREL_MODEL;
    /**
     * Statyczny model WOODEN_BOX_MODEL.
     */
    public static Model WOODEN_BOX_MODEL;
    /**
     * Statyczny model PLASZCZYZNA_MODEL.
     */
    public static Model PLASZCZYZNA_MODEL;
    /**
     * Statyczny model TREE_MODEL.
     */
    public static Model TREE_MODEL;

    private static AssetManager assets;
    private static ModelBuilder modelBuilder = new ModelBuilder();
    private static ArrayList<Model> modelArrayList = new ArrayList<Model>();

    private ModelManager() { }

    /**
     * Tworzenie obiektów gry przy pierwszy użyciu
     */
    static {
        // CREATE SIMPLE MODELS
        modelArrayList.add(createBox(1f, 1f, 1f));
        modelArrayList.add(createGround(38.8f, 5f, 23.8f));
        modelArrayList.add(createSphere(1f));
        modelArrayList.add(createRectangle(1f, 1f, 3f));
        modelArrayList.add(createCylinder(1f, 3f, 1f));

        loadCustomModels();
    }

    /**
     * Załadowanie modeli z plików, dodanie ich do list.
     */
    public static void loadCustomModels () {

        assets = new AssetManager();
        assets.load(PEPE, Model.class);
        assets.load(SKYDOME, Model.class);
        assets.load(TEST, Model.class);
        assets.load(POLY_PEPE_1, Model.class);
        assets.load(POLY_PEPE_2, Model.class);
        assets.load(SOWA_S, Model.class);
        assets.load(STREET_LAMP, Model.class);
        assets.load(DREWNO, Model.class);
        assets.load(OIL_BARREL, Model.class);
        assets.load(WOODEN_BOX, Model.class);
        assets.load(PLASZCZYZNA, Model.class);
        assets.load(TREE, Model.class);
        assets.finishLoading();
        assets.update();

        PEPE_MODEL = assets.get(PEPE, Model.class);
        SKYDOME_MODEL = assets.get(SKYDOME, Model.class);
        TEST_MODEL = assets.get(TEST, Model.class);
        POLY_PEPE_1_MODEL = assets.get(POLY_PEPE_1, Model.class);
        POLY_PEPE_2_MODEL = assets.get(POLY_PEPE_2, Model.class);
        SOWA_S_MODEL = assets.get(SOWA_S, Model.class);
        STREET_LAMP_MODEL = assets.get(STREET_LAMP, Model.class);
        DREWNO_MODEL = assets.get(DREWNO, Model.class);
        OIL_BARREL_MODEL  = assets.get(OIL_BARREL, Model.class);
        WOODEN_BOX_MODEL = assets.get(WOODEN_BOX, Model.class);
        PLASZCZYZNA_MODEL = assets.get(PLASZCZYZNA, Model.class);
        TREE_MODEL = assets.get(TREE, Model.class);

        modelArrayList.add(PEPE_MODEL);
        modelArrayList.add(SKYDOME_MODEL);
        modelArrayList.add(TEST_MODEL);
        modelArrayList.add(POLY_PEPE_1_MODEL);
        modelArrayList.add(POLY_PEPE_2_MODEL);
        modelArrayList.add(SOWA_S_MODEL);
        modelArrayList.add(STREET_LAMP_MODEL);
        modelArrayList.add(DREWNO_MODEL);
        modelArrayList.add(OIL_BARREL_MODEL);
        modelArrayList.add(WOODEN_BOX_MODEL);
        modelArrayList.add(PLASZCZYZNA_MODEL);
        modelArrayList.add(TREE_MODEL);
    }

    /**
     * Tworzenie płaszczyzny gry.
     *
     * @param x długość
     * @param y wysokość
     * @param z głebokość
     * @return model obiektu
     */
    public static Model createGround(float x, float y, float z) {
        GROUND = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.OLIVE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return GROUND;
    }

    /**
     * Tworzenie sześcianiu.
     *
     * @param x długość
     * @param y wysokość
     * @param z głebokość
     * @return model obiektu
     */
    public static Model createBox(float x, float y, float z) {
        BOX = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.FOREST)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return BOX;
    }

    /**
     * Tworzy cylinder
     *
     * @param width długość
     * @param height wysokość
     * @param lenght głebokość
     * @return model obiektu
     */
    public static Model createCylinder(float width, float height, float lenght) {
        CYLINDER = modelBuilder.createCylinder(width, height, lenght, 20,
                new Material(ColorAttribute.createDiffuse(Color.SKY), ColorAttribute.createSpecular(Color.GRAY),
                        FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return CYLINDER;
    }

    /**
     * Tworzenie kuli
     *
     * @param fi średnica
     * @return model obiektu
     */
    public static Model createSphere(float fi) {
        SPHERE = modelBuilder.createSphere(fi, fi, fi, 20, 20,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.GRAY),
                        FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return SPHERE;
    }

    /**
     * Tworzy prostopadłościan
     *
     * @param x długość
     * @param y wysokość
     * @param z głebokość
     * @return model obiektu
     */
    public static Model createRectangle(float x, float y, float z) {
        RECTANGLE = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.PURPLE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return RECTANGLE;
    }

    @Override
    public void dispose() {
        for (Model model :
                modelArrayList) {
            model.dispose();
        }
        assets.dispose();
    }
}
