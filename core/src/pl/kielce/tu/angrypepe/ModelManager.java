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

public final class ModelManager implements Disposable{

    public static Model SPHERE;
    public static Model BOX;
    public static Model RECTANGLE;
    public static Model GROUND;
    public static Model CYLINDER;

    protected static String SKYLAND1 = "skyland.g3dj";
    protected static String PEPE = "pepe_box.g3dj";
    protected static String SKYDOME = "skydome.g3dj";
    protected static String TEST = "123.g3dj";
    protected static String POLY_PEPE_1 = "poly_pepe1.g3dj";
    protected static String POLY_PEPE_2 = "poly_pepe2.g3dj";
    protected static String SOWA_S = "sowa_s.g3dj";
    protected static String SOWA_M = "sowa_m.g3dj";
    protected static String SRODOWISKO = "srodowisko.g3dj";
    protected static String STREET_LAMP = "Streetlamp.g3dj";
    protected static String DREWNO = "drewno.g3dj";

    public static String PARTICLE = "cloudpuff.pfx";

    public static Model SKYLAND1_MODEL;
    public static Model PEPE_MODEL;
    public static Model SKYDOME_MODEL;
    public static Model TEST_MODEL;
    public static Model POLY_PEPE_1_MODEL;
    public static Model POLY_PEPE_2_MODEL;
    public static Model SOWA_S_MODEL;
    public static Model SOWA_M_MODEL;
    public static Model SRODOWISKO_MODEL;
    public static Model STREET_LAMP_MODEL;
    public static Model DREWNO_MODEL;

    private static AssetManager assets;
    private static ModelBuilder modelBuilder = new ModelBuilder();
    private static ArrayList<Model> modelArrayList = new ArrayList<Model>();
    private static ArrayList<String> namesArrayList = new ArrayList<String>();

    private ModelManager() { }

    static {
        // CREATE SIMPLE MODELS
        modelArrayList.add(createBox(1f, 1f, 1f));
        modelArrayList.add(createGround(30f, 2.5f, 30f));
        modelArrayList.add(createSphere(1f));
        modelArrayList.add(createRectangle(1f, 1f, 3f));
        modelArrayList.add(createCylinder(1f, 3f, 1f));

        loadCustomModels();
    }

    public static void loadCustomModels () {

        assets = new AssetManager();
        assets.load(SKYLAND1, Model.class);
        assets.load(PEPE, Model.class);
        assets.load(SKYDOME, Model.class);
        assets.load(TEST, Model.class);
        assets.load(POLY_PEPE_1, Model.class);
        assets.load(POLY_PEPE_2, Model.class);
        assets.load(SOWA_S, Model.class);
        assets.load(SOWA_M, Model.class);
        assets.load(SRODOWISKO, Model.class);
        assets.load(STREET_LAMP, Model.class);
        assets.load(DREWNO, Model.class);
        assets.finishLoading();
        assets.update();

        SKYLAND1_MODEL = assets.get(SKYLAND1, Model.class);
        PEPE_MODEL = assets.get(PEPE, Model.class);
        SKYDOME_MODEL = assets.get(SKYDOME, Model.class);
        TEST_MODEL = assets.get(TEST, Model.class);
        POLY_PEPE_1_MODEL = assets.get(POLY_PEPE_1, Model.class);
        POLY_PEPE_2_MODEL = assets.get(POLY_PEPE_2, Model.class);
        SOWA_S_MODEL = assets.get(SOWA_S, Model.class);
        SOWA_M_MODEL = assets.get(SOWA_M, Model.class);
        SRODOWISKO_MODEL = assets.get(SRODOWISKO, Model.class);
        STREET_LAMP_MODEL = assets.get(STREET_LAMP, Model.class);
        DREWNO_MODEL = assets.get(DREWNO, Model.class);

        modelArrayList.add(SKYLAND1_MODEL);
        modelArrayList.add(PEPE_MODEL);
        modelArrayList.add(SKYDOME_MODEL);
        modelArrayList.add(TEST_MODEL);
        modelArrayList.add(POLY_PEPE_1_MODEL);
        modelArrayList.add(POLY_PEPE_2_MODEL);
        modelArrayList.add(SOWA_S_MODEL);
        modelArrayList.add(SOWA_M_MODEL);
        modelArrayList.add(SRODOWISKO_MODEL);
        modelArrayList.add(STREET_LAMP_MODEL);
        modelArrayList.add(DREWNO_MODEL);
    }

    private static Model createGround(float x, float y, float z) {
        GROUND = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.OLIVE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return GROUND;
    }

    public static Model createBox(float x, float y, float z) {
        BOX = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.FOREST)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return BOX;
    }

    public static Model createCylinder(float width, float height, float lenght) {
        CYLINDER = modelBuilder.createCylinder(width, height, lenght, 20,
                new Material(ColorAttribute.createDiffuse(Color.SKY), ColorAttribute.createSpecular(Color.GRAY),
                        FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return CYLINDER;
    }

    public static Model createSphere(float fi) {
        SPHERE = modelBuilder.createSphere(fi, fi, fi, 20, 20,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.GRAY),
                        FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return SPHERE;
    }

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
