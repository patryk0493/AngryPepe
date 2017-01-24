package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Disposable;

/**
 * Klasa reprezentująca obiekt gry.
 * @author Patryk Eliasz, Karol Rębiś */
public class GameObject implements Disposable{


    /**
     * Ciało obiektu.
     */
    public btRigidBody body;
    /**
     * Model obiektu
     */
    public Model model;
    /**
     * Wezły
     */
    public String node;
    /**
     * Instancja modelu.
     */
    public ModelInstance instance;
    /**
     * Stan ruchu.
     */
    public btDefaultMotionState motionState = null;
    /**
     * Skalowanie obiektu.
     */
    protected float scaleRatio;

    private final static BoundingBox bounds = new BoundingBox();
    /**
     * Wektor (środek obiektu.)
     */
    public final Vector3 center = new Vector3();
    /**
     * Rozmiary obiektu.
     */
    public final Vector3 dimensions = new Vector3();
    /**
     * Średnica
     */
    public float radius = 0;
    /**
     * Identyfikator obiektu.
     */
    public int objectId;
    /**
     * Unikalny identykator obiektu.
     */
    public static int id = 0;

    /**
     * Konstruktor nowej instancji obiektu gry.
     *
     * @param model             model
     * @param instance          instancja
     * @param node              węzły
     * @param constructionInfo  dane i obiekcie
     * @param scaleRatio        skala
     * @param createMotionState stan ruchu
     */
    public GameObject(Model model, ModelInstance instance, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo,
                       float scaleRatio, boolean createMotionState) {
        this.model = model;
        this.node = node;
        this.scaleRatio = scaleRatio;
        this.instance = instance;
        body = new btRigidBody(constructionInfo);
        body.setActivationState(Collision.DISABLE_DEACTIVATION); // poruszanie obiektu,gdy wektor ruchuchu = 0
        body.setRestitution(0.2f);
        if (createMotionState) {
            motionState = new btDefaultMotionState(this.instance.transform);
            motionState.setWorldTransform(this.instance.transform);
            body.setMotionState(motionState);
        } else
            body.setWorldTransform(this.instance.transform);

        instance.calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        this.radius = dimensions.len() / 2f;
        objectId = id;
        this.setUserData(new CustomObjectData(objectId));
        id++;

    }

    /**
     * Ustawia dane o obiekcie.
     *
     * @param customObjectData nowe dane o obiekcie
     */
    public void setUserData(CustomObjectData customObjectData) {
        body.userData = customObjectData;
    }

    /**
     * Pobiera dane o obiekcie
     *
     * @return the uset data
     */
    public CustomObjectData getUsetData() {
        return (CustomObjectData) body.userData;
    }

    /**
     * Pobiera ciało obiektu.
     *
     * @return ciało obiektu
     */
    public btRigidBody getBody() {
        return body;
    }

    /**
     * Pobiera model obiektu.
     *
     * @return model obiektu
     */
    public Model getModel() {
        return model;
    }

    /**
     * Pobiera węzły obiektu.
     *
     * @return wezły
     */
    public String getNode() {
        return node;
    }

    /**
     * Pobiera instancję obiektu.
     *
     * @return instancja
     */
    public ModelInstance getInstance() {
        return instance;
    }

    /**
     * Pobiera identyfikator obiektu.
     *
     * @return identyfikator obiektu
     */
    public int getObjectId() {
        return objectId;
    }

    /**
     * Pobiera transformację świata.
     */
    public void getWorldTransform() {
        if (this.motionState != null)
            this.motionState.getWorldTransform(this.getInstance().transform);
    }

    /**
     * Niszczenie obiektu.
     */
    public void destroy() {
        body.dispose();
        instance.model.dispose();
    }

    @Override
    public void dispose() {
        motionState.dispose();
        body.getMotionState().dispose();
        body.dispose();
        instance.model.dispose();
    }

    /**
     * Konstruktor instacji ciałą obiektu.
     */
    static class BodyConstructor implements Disposable {
        /**
         * Model
         */
        public final Model model;
        /**
         * Węzły
         */
        public final String node;
        private final float scaleRatio;
        /**
         * Kształ kolizji.
         */
        public btCollisionShape shape;
        /**
         * Pozycja - wektor 0,0,0
         */
        public Vector3 position = Vector3.Zero;
        /**
         * Stan ruchu
         */
        public boolean motionStateCreate;
        /**
         * Instancja modelu
         */
        public ModelInstance instance;
        /**
         * Informacje o twardym ciele obiektu.
         */
        public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
        private static Vector3 localInertia = new Vector3();

        /**
         * Konstruktor klasy BodyConstructor
         *
         * @param model             model
         * @param node              węzły
         * @param shape             kształt
         * @param pos               pozycja (przesuniecie)
         * @param mass              masa obiektu
         * @param scaleRatio        skala
         * @param motionStateCreate stan ruchu
         */
        public BodyConstructor(Model model, String node, btCollisionShape shape, Vector3 pos, float mass,
                               float scaleRatio, boolean motionStateCreate) {
            this.model = model;
            this.node = node;
            this.shape = shape;
            this.scaleRatio = scaleRatio;
            this.position = pos;
            this.motionStateCreate = motionStateCreate;

            // INSTANCE
            this.instance = new ModelInstance(model);
            instance.transform.trn(position);
            this.instance.transform.scl(scaleRatio, scaleRatio, scaleRatio);

            // SHAPE
            if (this.shape == null) {
                this.shape = createConvexHullShape(this.model, true);
            }
            this.shape.setMargin(0.00f);
            //this.shape.setLocalScaling(new Vector3(scaleRatio, scaleRatio, scaleRatio));
            //TODO FIX SCALING
            this.shape = new btUniformScalingShape((btConvexShape) this.shape, scaleRatio);

            // INERTIA
            if (mass > 0f)
                this.shape.calculateLocalInertia(mass, localInertia);
            else
                this.shape.calculateLocalInertia(mass, localInertia.set(0, 0, 0));
            this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, this.shape, localInertia);
        }

        /**
         * Tworzenie obiektu na podstawie załadowanego modelu
         *
         * @param model    model
         * @param optimize czy ma optymalizować model
         * @return model
         */
        public static btConvexHullShape createConvexHullShape(final Model model, boolean optimize) {
            final Mesh mesh = model.meshes.get(0);
            final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
            if (!optimize) return shape;
            final btShapeHull hull = new btShapeHull(shape);
            hull.buildHull(shape.getMargin());
            final btConvexHullShape result = new btConvexHullShape(hull);
            shape.dispose();
            hull.dispose();
            return result;
        }


        /**
         * Zwraca nowy obiekt gry.
         *
         * @return obiekt gry
         */
        public GameObject construct () {
            return new GameObject(model, instance, node, constructionInfo, scaleRatio, motionStateCreate);
        }

        @Override
        public void dispose () {
            shape.dispose();
            instance.model.dispose();
            constructionInfo.dispose();
        }

    }
}
