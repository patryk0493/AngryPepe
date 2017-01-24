package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

import java.util.ArrayList;

/**
 * Klasa reprezentująca świat gry.
 * @author Patryk Eliasz, Karol Rębiś */
public class WorldManager {

    /**
     * Kamera.
     */
    public CameraManager cam;
    /**
     * Środowisko
     */
    public Environment environment;
    /**
     * Pozycja, wektor 0,0,0
     */
    public Vector3 position = new Vector3();
    /**
     * Czy obiekt gracza jest ciągnięty.
     */
    public boolean isPulling = false;

    private ModelInstance skyInstance;
    /**
     * Środowisko modeli.
     */
    public ModelBatch modelBatch;

    /**
     * Obiekt gracza.
     */
    public GameObject playerGameObject;
    private GameObject groundGameObject;
    private GameObject sphereGameObject;
    private GameObject boxGameObject;
    private GameObject rectangleGameObject;
    private GameObject cylinderGameObject;
    private GameObject owlSGameObject;
    private GameObject streetLampGameObject;
    private GameObject woodGameObject;
    private GameObject barrelGameObject;
    private GameObject woodenBoxGameObject;
    private GameObject plaszczyznaGameObject;
    private GameObject treeGameObject;

    /**
     * Obserwartor zderzenia obiektów.
     */
    public MyContactListener contactListener;

    /**
     * Lista instancji obiektów.
     */
    public ArrayList<ModelInstance> objectInstances;
    /**
     * Lista instancji obiektów gry.
     */
    public ArrayList<GameObject> gameObjectsList = new ArrayList<GameObject>();

    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher dispatcher;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver solver;
    private btDiscreteDynamicsWorld world;

    /**
     * Obiekt klasy {@link Particile} obsługujący cząsteczki
     */
    public Particile particleUtils;


    /**
     * Inicjalizacja świata gry.
     */
    public void initWorld() {

        modelBatch = new ModelBatch();
        cam = new CameraManager();
        objectInstances = new ArrayList<ModelInstance>();
        skyInstance = new ModelInstance(ModelManager.SKYDOME_MODEL);
        objectInstances.add(skyInstance);

        prepareEnviroment();
        Bullet.init();
        setupWorld(new Vector3(0, -9.81f, 0));

        createGameObjects();

        particleUtils = new Particile();
        particleUtils.initBillBoardParticles(cam);

        contactListener = new MyContactListener();
    }

    /**
     * Utworzenia obiektów gry, dodanie ich do świata oraz zapisanie ich ich w listach.
     */
    public void createGameObjects() {

        sphereGameObject = new GameObject.BodyConstructor(
                ModelManager.createSphere(2),
                "sphere",
                new btSphereShape(1),
                new Vector3(0f, 5f, 0f),
                1f, 1, true)
                .construct();

        boxGameObject = new GameObject.BodyConstructor(
                ModelManager.createBox(4, 4, 4),
                "box",
                new btBoxShape(new Vector3(2, 2, 2)),
                new Vector3(3f, 1f, 0f),
                1f, 1f, true)
                .construct();

        rectangleGameObject = new GameObject.BodyConstructor(
                ModelManager.createRectangle(2f, 6f, 2f),
                "rectangle",
                null,
                new Vector3(5f, 1f, 0f),
                3f, 1f, true)
                .construct();

        cylinderGameObject = new GameObject.BodyConstructor(
                ModelManager.createCylinder(2f, 6f, 2f),
                "cylinder",
                new btCylinderShape(new Vector3(1f, 3, 1f)),
                new Vector3(3f, 1f, 7f),
                3f, 1f, true)
                .construct();

        //jeśli obiekt ma mase inna niż 0 skaluje  model

        owlSGameObject = new GameObject.BodyConstructor(
                ModelManager.SOWA_S_MODEL,
                "owl S",
                null,
                new Vector3(-2f, 5f, 0f),
                4f, 1.35f, true)
                .construct();

        streetLampGameObject = new GameObject.BodyConstructor(
                ModelManager.STREET_LAMP_MODEL,
                "skyland",
                null,
                new Vector3(-2f, 2f, 0f),
                0f, 1f, false)
                .construct();

        woodGameObject = new GameObject.BodyConstructor(
                ModelManager.DREWNO_MODEL,
                "wood",
                null,
                new Vector3(-2f, 10f, 0f),
                2f, 1f, true)
                .construct();

        barrelGameObject = new GameObject.BodyConstructor(
                ModelManager.OIL_BARREL_MODEL,
                "barrel",
                new btCylinderShape(new Vector3(1f, 1f, 3f)),
                new Vector3(-4f, 10f, 0f),
                2f, 1f, true)
                .construct();

        woodenBoxGameObject = new GameObject.BodyConstructor(
                ModelManager.WOODEN_BOX_MODEL,
                "woodBox",
                null,
                new Vector3(-2f, 5f, 0f),
                2f, 1.45f, true)
                .construct();

        plaszczyznaGameObject = new GameObject.BodyConstructor(
                ModelManager.PLASZCZYZNA_MODEL,
                "plaszczyzna",
                null,
                new Vector3(0f, -0.5f, 0f),
                0f, 1.5f, true)
                .construct();

        groundGameObject = new GameObject.BodyConstructor(
                ModelManager.GROUND,
                "ground",
                null,
                new Vector3(0f, 0f, 0f),
                0f, 1, false)
                .construct();

        treeGameObject = new GameObject.BodyConstructor(
                ModelManager.TREE_MODEL,
                "TREE",
                null,
                new Vector3(-9f, 3.5f, -2f),
                0f, 1f, false)
                .construct();

        playerGameObject = new GameObject.BodyConstructor(
                ModelManager.PEPE_MODEL,
                "pepexD",
                null,
                new Vector3(0f, 6f, 0f),
                3f, 1f, true)
                .construct();
        playerGameObject.setUserData(playerGameObject.getUsetData().setDestructible(false).setName("PEPE"));

        gameObjectsList.add(groundGameObject);
        gameObjectsList.add(sphereGameObject);
        gameObjectsList.add(boxGameObject);
        gameObjectsList.add(playerGameObject);
        gameObjectsList.add(rectangleGameObject);
        gameObjectsList.add(cylinderGameObject);
        gameObjectsList.add(owlSGameObject);
        gameObjectsList.add(streetLampGameObject);
        gameObjectsList.add(woodGameObject);

        gameObjectsList.add(woodenBoxGameObject);
        gameObjectsList.add(plaszczyznaGameObject);
        gameObjectsList.add(barrelGameObject);
        gameObjectsList.add(treeGameObject);

        for (GameObject go : gameObjectsList) {
            objectInstances.add(go.getInstance());
            world.addRigidBody(go.getBody());
        }

        createWall();

    }

    /**
     * Render świta gry.
     */
    public void renderWorld() {

        cam.changeFieldOfView();
        cam.update(playerGameObject.instance.transform.getTranslation(new Vector3().Zero));

        try {
            world.stepSimulation(Gdx.graphics.getDeltaTime(), 10);
            for (GameObject gameObject : gameObjectsList) {
                gameObject.getWorldTransform();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        modelBatch.begin(cam);
        modelBatch.render(objectInstances, environment);
        modelBatch.render(particleUtils.updateParticile(), environment);
        modelBatch.end();
    }

    /**
     * Utworzenie obiektu gry - .
     */
    public void createGameObject() {
        GameObject sample = new GameObject.BodyConstructor(
                ModelManager.createRectangle(2f, 1f, 3f),
                "PEPE",
                null,
                new Vector3(0f, 15f, 0f),
                2f, 1f, true)
                .construct();
        sample.body.setRestitution(.8f);
        sample.body.setFriction(0.2f);

        gameObjectsList.add(sample);
        objectInstances.add(sample.getInstance());
        world.addRigidBody(sample.getBody());

    }

    /**
     * Stworzenie ściany z sześcianów.
     */
    public void createWall() {
        for (int i = 0; i< 5; i++) {
            for (int j = 0; j < 7; j++) {
                GameObject sample = new GameObject.BodyConstructor(
                        ModelManager.createBox(2, 2, 2),
                        "PEPE",
                        new btBoxShape(new Vector3(1, 1, 1)),
                        new Vector3(10f, i*2, j*2),
                        2f, 1f, true)
                        .construct();

                gameObjectsList.add(sample);
                objectInstances.add(sample.getInstance());
                world.addRigidBody(sample.getBody());
            }
        }
    }

    /**
     * Pobieranie listy obiektów znajdujących się wzdłóż promienia.
     *
     * @param screenX współrzędna X
     * @param screenY współrzędna Y
     * @return lista obiektów które "przeszył" promień
     */
    public ArrayList<Integer> getObject (int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);
        ArrayList<Integer> objectIdList = new ArrayList<Integer>();
        for (int i = 0; i < gameObjectsList.size(); ++i) {
            final GameObject gameObject = gameObjectsList.get(i);
            gameObject.getInstance().transform.getTranslation(position);
            if (Intersector.intersectRaySphere(ray, position, gameObject.radius, null)) {
                objectIdList.add(gameObject.getObjectId());
            }
        }
        return objectIdList;
    }

    /**
     * Przygotowanie środowiska, dodanie świateł.
     */
    public void prepareEnviroment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(-0.5f, -1f , -0.5f)));
        environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(0.5f, -1f , -0.5f)));
    }

    /**
     * Przygotowanie fizyki świata gry.
     *
     * @param gravityVector Wektor grawitacji.
     */
    public void setupWorld(Vector3 gravityVector) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        world.setGravity(gravityVector);
    }

    /**
     * Usunięcie wszystkich obiektów ze świata gry.
     */
    public void removeAllGameObjects() {
        for (GameObject ob: (ArrayList<GameObject>) gameObjectsList.clone()) {
            gameObjectsList.remove(ob);
            removeGameObject(ob);
        }
    }

    /**
     * Zresetowanie świata gry.
     */
    public void resetWorld() {
        removeAllGameObjects();
        world.dispose();
        setupWorld(new Vector3(0, -9.81f, 0));
        createGameObjects();
        Gdx.app.log("RESET", "WORLD");
    }

    /**
     * Usunięcie pojedyńczego obiektu ze świata gry
     *
     * @param gameObject obiekt do usunięcia
     * @return czy udało się usunąć
     */
    public boolean removeGameObject(GameObject gameObject) {
        if (gameObject != null) {
            try {
                world.removeRigidBody(gameObject.getBody());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            gameObjectsList.remove(gameObject);
            objectInstances.remove(gameObject.instance);
        }
        return true;
    }

    /**
     * "Uderzenie" obiektu gracza.
     *
     * @param vector3 wektor
     */
    public void applyCentralImpulseToPlayer(Vector3 vector3) {
        playerGameObject.body.applyCentralImpulse( vector3 );
    }

    /**
     * Czyszczenie pamięci.
     */
    public void dispose() {

        world.dispose();
        collisionConfiguration.dispose();
        dispatcher.dispose();
        broadphase.dispose();
        solver.dispose();

        particleUtils.dispose();
        modelBatch.dispose();

        contactListener.dispose();
    }

    /**
     * Pobranie obiektu kamery.
     *
     * @return kamera
     */
    public CameraManager getCam() {
        return cam;
    }

    /**
     * Pobranie instancji środowiska gry.
     *
     * @return środowisko gry
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Ustawienie środowiska gry
     *
     * @param environment środowisko gry
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Pobranie wektora
     *
     * @return wektor
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Ustawienie wektora
     *
     * @param position wektor
     */
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    /**
     * Czy obiekty jest "ciągnięty"?
     *
     * @return wartosc logiczna
     */
    public boolean isPulling() {
        return isPulling;
    }

    /**
     * Ustawienie czy jest "ciągnięty"
     *
     * @param pulling wartość logiczna
     */
    public void setPulling(boolean pulling) {
        isPulling = pulling;
    }

    /**
     * Klasa implementująca zdarzenie zderzenia obiektów gry
     */
    class MyContactListener extends ContactListener {
        @Override
        public void onContactStarted (btCollisionObject colObj0, btCollisionObject colObj1) {

            CustomObjectData customObjectData1 = (CustomObjectData) colObj0.userData;
            CustomObjectData customObjectData2  = (CustomObjectData) colObj1.userData;

            GameObject go1 = getCollistionObject(customObjectData1.getId());
            GameObject go2 = getCollistionObject(customObjectData2.getId());

            if(!go1.body.isStaticObject())
                go1.getUsetData().updateHp( calculateMomentum(go1, go2) );
            if(!go2.body.isStaticObject())
                go2.getUsetData().updateHp( calculateMomentum(go1, go2) );

            System.out.println(getMaxVelecity(go1.body.getLinearVelocity()) + " : " + getMaxVelecity(go2.body.getLinearVelocity()));
            System.out.println( customObjectData1.toString() + " : " + customObjectData2.toString() );

            if ( go1.getUsetData().getHp() < 0f && go1.getUsetData().isDestructible()) {
                go2.body.setLinearVelocity(go2.body.getLinearVelocity().scl(go1.body.getInvMass()));
                particleUtils.boomEffect(go1.body.getWorldTransform().getTranslation(new Vector3()),
                        calculateMomentum(go2, go1)/4);
                removeGameObject(go1);

                Music click = Gdx.audio.newMusic(Gdx.files.internal("Tiny Button Push-SoundBible.com-513260752.mp3"));
                click.play();

            }
            if ( go2.getUsetData().getHp() < 0f && go2.getUsetData().isDestructible()) {
                go1.body.setLinearVelocity(go1.body.getLinearVelocity().scl(go2.body.getInvMass()));
                particleUtils.boomEffect(go2.body.getWorldTransform().getTranslation(new Vector3()),
                        calculateMomentum(go2, go1)/4);
                removeGameObject(go2);

                Music click = Gdx.audio.newMusic(Gdx.files.internal("Tiny Button Push-SoundBible.com-513260752.mp3"));
                click.play();
            }

        }

        /**
         * Obiczenie pędu między obiektami
         *
         * @param o1 obiekt 1
         * @param o2 obiekt 2
         * @return pęd
         */
        public float calculateMomentum(GameObject o1, GameObject o2) {
            float momentum;
            momentum = getMaxVelecity(o1.body.getLinearVelocity()) * o1.getBody().getInvMass() +
                    getMaxVelecity(o2.body.getLinearVelocity()) * o2.getBody().getInvMass();
            return momentum;
        }

        /**
         * Pobranie obiektu gry na podstawie jego ID
         *
         * @param id identyfikator obiektu
         * @return obiekt gry
         */
        public GameObject getCollistionObject ( int id ) {
            for ( GameObject gameObject : gameObjectsList ) {
                if (id == gameObject.getObjectId())
                    return gameObject;
            }
            return null;
        }

        /**
         * Pobranie maksymalnej wartości prędkości z wektora przemieszczenia
         *
         * @param vector wektor
         * @return maksymalna prędkość
         */
        public float getMaxVelecity(Vector3 vector) {
            float maxVelocity = 0;
            if (  Math.abs(vector.x) > maxVelocity )
                maxVelocity = Math.abs(vector.x);
            if (  Math.abs(vector.y) > maxVelocity )
                maxVelocity = Math.abs(vector.y);
            if (  Math.abs(vector.z) > maxVelocity )
                maxVelocity = Math.abs(vector.z);
            return maxVelocity;
        }

        @Override
        public void onContactProcessed (int userValue0, int userValue1) {
            // implementation
        }
    }

    /**
     * Pobiera dyspozytora kolizji.
     *
     * @return dyspozytor
     */
    public btCollisionDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * Ustawia dyspozytora kolizji.
     *
     * @param dispatcher dyspozytor
     */
    public void setDispatcher(btCollisionDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Pobiera obiekt gry - pudełko.
     *
     * @return obiekt gry - pudełko
     */
    public GameObject getBoxGameObject() {
        return boxGameObject;
    }


}
