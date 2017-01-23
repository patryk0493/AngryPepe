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

public class WorldManager {

    private CameraManager cam;
    private Environment environment;
    private Vector3 position = new Vector3();
    private boolean isPulling = false;

    private ModelInstance skyInstance;
    public ModelBatch modelBatch;

    private GameObject playerGameObject;
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

    private MyContactListener contactListener;

    private float power;

    private ArrayList<ModelInstance> objectInstances;
    private ArrayList<GameObject> gameObjectsList = new ArrayList<GameObject>();

    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher dispatcher;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver solver;
    private btDiscreteDynamicsWorld world;

    private Particile particleUtils;


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
        modelBatch.render(particleUtils.updateAndDraw(), environment);
        modelBatch.end();
    }

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

    public void prepareEnviroment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(-0.5f, -1f , -0.5f)));
        environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(0.5f, -1f , -0.5f)));
    }

    public void setupWorld(Vector3 gravityVector) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        world.setGravity(gravityVector);
    }

    public void removeAllGameObjects() {
        for (GameObject ob: (ArrayList<GameObject>) gameObjectsList.clone()) {
            gameObjectsList.remove(ob);
            removeGameObject(ob);
        }
    }

    public void resetWorld() {
        removeAllGameObjects();
        world.dispose();
        setupWorld(new Vector3(0, -9.81f, 0));
        createGameObjects();
        Gdx.app.log("RESET", "WORLD");
    }

    public void removeGameObject(GameObject gameObject) {
        if (gameObject != null) {
            try {
                world.removeRigidBody(gameObject.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }

            gameObjectsList.remove(gameObject);
            objectInstances.remove(gameObject.instance);
        }
    }

    public void applyCentralImpulseToPlayer(Vector3 vector3) {

        playerGameObject.body.applyCentralImpulse( vector3 );

    }

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

    public CameraManager getCam() {
        return cam;
    }

    public void setCam(CameraManager cam) {
        this.cam = cam;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public boolean isPulling() {
        return isPulling;
    }

    public void setPulling(boolean pulling) {
        isPulling = pulling;
    }

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

        public float calculateMomentum(GameObject o1, GameObject o2) {
            float momentum;
            momentum = getMaxVelecity(o1.body.getLinearVelocity()) * o1.getBody().getInvMass() +
                    getMaxVelecity(o2.body.getLinearVelocity()) * o2.getBody().getInvMass();
            return momentum;
        }

        public GameObject getCollistionObject ( int id ) {
            for ( GameObject gameObject : gameObjectsList ) {
                if (id == gameObject.getObjectId())
                    return gameObject;
            }
            return null;
        }

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

    public MyContactListener getContactListener() {
        return contactListener;
    }

    public void setContactListener(MyContactListener contactListener) {
        this.contactListener = contactListener;
    }

    public ArrayList<ModelInstance> getObjectInstances() {
        return objectInstances;
    }

    public void setObjectInstances(ArrayList<ModelInstance> objectInstances) {
        this.objectInstances = objectInstances;
    }

    public ArrayList<GameObject> getGameObjectsList() {
        return gameObjectsList;
    }

    public void setGameObjectsList(ArrayList<GameObject> gameObjectsList) {
        this.gameObjectsList = gameObjectsList;
    }

    public btDefaultCollisionConfiguration getCollisionConfiguration() {
        return collisionConfiguration;
    }

    public void setCollisionConfiguration(btDefaultCollisionConfiguration collisionConfiguration) {
        this.collisionConfiguration = collisionConfiguration;
    }

    public btCollisionDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(btCollisionDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public btDbvtBroadphase getBroadphase() {
        return broadphase;
    }

    public void setBroadphase(btDbvtBroadphase broadphase) {
        this.broadphase = broadphase;
    }

    public btSequentialImpulseConstraintSolver getSolver() {
        return solver;
    }

    public void setSolver(btSequentialImpulseConstraintSolver solver) {
        this.solver = solver;
    }

    public btDiscreteDynamicsWorld getWorld() {
        return world;
    }

    public void setWorld(btDiscreteDynamicsWorld world) {
        this.world = world;
    }

    public ModelInstance getSkyInstance() {
        return skyInstance;
    }

    public void setSkyInstance(ModelInstance skyInstance) {
        this.skyInstance = skyInstance;
    }

    public GameObject getPlayerGameObject() {
        return playerGameObject;
    }

    public void setPlayerGameObject(GameObject playerGameObject) {
        this.playerGameObject = playerGameObject;
    }

    public GameObject getGroundGameObject() {
        return groundGameObject;
    }

    public void setGroundGameObject(GameObject groundGameObject) {
        this.groundGameObject = groundGameObject;
    }

    public GameObject getSphereGameObject() {
        return sphereGameObject;
    }

    public void setSphereGameObject(GameObject sphereGameObject) {
        this.sphereGameObject = sphereGameObject;
    }

    public GameObject getBoxGameObject() {
        return boxGameObject;
    }

    public void setBoxGameObject(GameObject boxGameObject) {
        this.boxGameObject = boxGameObject;
    }

    public GameObject getRectangleGameObject() {
        return rectangleGameObject;
    }

    public void setRectangleGameObject(GameObject rectangleGameObject) {
        this.rectangleGameObject = rectangleGameObject;
    }

    public GameObject getCylinderGameObject() {
        return cylinderGameObject;
    }

    public void setCylinderGameObject(GameObject cylinderGameObject) {
        this.cylinderGameObject = cylinderGameObject;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}
