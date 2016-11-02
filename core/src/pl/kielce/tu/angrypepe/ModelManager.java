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
 * Created by patryk on 02.11.2016.
 */
public final class ModelManager implements Disposable{

    public static Model SPHERE;
    public static Model BOX;
    public static Model RACTANGLE;
    public static Model GROUND;
    public static Model CYLINDER;

    protected static String SKYLAND1 = "skyland.g3dj";
    protected static String PEPE = "pepe_box.g3dj";
    protected static String SKYDOME = "skydome.g3dj";

    public static Model SKYLAND1_MODEL;
    public static Model PEPE_MODEL;
    public static Model SKYDOME_MODEL;

    private static AssetManager assets;
    private static ModelBuilder modelBuilder = new ModelBuilder();
    private static ArrayList<Model> modelArrayList = new ArrayList<Model>();

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
        assets.finishLoading();
        assets.update();

        SKYLAND1_MODEL = assets.get(SKYLAND1, Model.class);
        PEPE_MODEL = assets.get(PEPE, Model.class);
        SKYDOME_MODEL = assets.get(SKYDOME, Model.class);

        modelArrayList.add(SKYLAND1_MODEL);
        modelArrayList.add(PEPE_MODEL);
        modelArrayList.add(SKYDOME_MODEL);

    }

    private static Model createGround(float x, float y, float z) {
        GROUND = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.LIME)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return GROUND;
    }

    public static Model createBox(float x, float y, float z) {
        BOX = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.OLIVE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return BOX;
    }

    public static Model createCylinder(float width, float height, float lenght) {
        //CYLINDER

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
        RACTANGLE = modelBuilder.createBox(x, y, z,
                new Material(ColorAttribute.createDiffuse(Color.PURPLE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return RACTANGLE;
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
