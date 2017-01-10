package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.Gdx;
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
 * Created by patryk on 06.11.2016.
 */
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
    private GameObject skylandGameObject;
    private GameObject rectangleGameObject;
    private GameObject cylinderGameObject;
    private GameObject pepe1GameObject;
    private GameObject pepe2GameObject;
    private GameObject owlMGameObject;
    private GameObject owlSGameObject;
    private GameObject enviromentGameObject;
    private GameObject streeLampGameObject;
    private GameObject woodGameObject;

    private GameObject hintGameObject;

    private MyContactListener contactListener;

    private float power;

    private ArrayList<ModelInstance> objectInstances;
    private ArrayList<GameObject> gameObjectsList = new ArrayList<GameObject>();
    private ArrayList<GameObject> hintsObjectList = new ArrayList<GameObject>();

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

        groundGameObject = new GameObject.BodyConstructor(
                ModelManager.GROUND,
                "ground",
                null,
                new Vector3(0f, 0f, 0f),
                0f, 1, false)
                .construct();

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

        skylandGameObject = new GameObject.BodyConstructor(
                ModelManager.SKYLAND1_MODEL,
                "skyland",
                null,
                new Vector3(-4f, 7f, 0f),
                0.f, 1f, true)
                .construct();
        //jeśli obiekt ma mase inna niż 0 skaluje  model

        pepe1GameObject = new GameObject.BodyConstructor(
                ModelManager.POLY_PEPE_1_MODEL,
                "skyland",
                null,
                new Vector3(-2f, 5f, 0f),
                2f, 1f, true)
                .construct();

        pepe2GameObject = new GameObject.BodyConstructor(
                ModelManager.POLY_PEPE_2_MODEL,
                "skyland",
                null,
                new Vector3(-2f, 5f, 0f),
                2f, 1f, true)
                .construct();

        owlMGameObject = new GameObject.BodyConstructor(
                ModelManager.SOWA_M_MODEL,
                "skyland",
                null,
                new Vector3(-2f, 5f, 0f),
                2f, 1f, true)
                .construct();

        owlSGameObject = new GameObject.BodyConstructor(
                ModelManager.SOWA_S_MODEL,
                "owl S",
                null,
                new Vector3(-2f, 5f, 0f),
                4f, 1.35f, true)
                .construct();

        enviromentGameObject = new GameObject.BodyConstructor(
                ModelManager.SRODOWISKO_MODEL,
                "skyland",
                null,
                new Vector3(-2f, -5f, 0f),
                0f, 1f, false)
                .construct();

        streeLampGameObject = new GameObject.BodyConstructor(
                ModelManager.STREET_LAMP_MODEL,
                "skyland",
                null,
                new Vector3(-2f, 5f, 0f),
                2f, 1f, true)
                .construct();

        woodGameObject = new GameObject.BodyConstructor(
                ModelManager.DREWNO_MODEL,
                "wood",
                null,
                new Vector3(-2f, 10f, 0f),
                2f, 1f, true)
                .construct();

        playerGameObject = new GameObject.BodyConstructor(
                ModelManager.PEPE_MODEL,
                "pepexD",
                null,
                new Vector3(0f, 6f, 0f),
                3f, 1f, true)
                .construct();
        playerGameObject.setUserData(playerGameObject.getUsetData().setDestructible(false).setName("PEPE"));


        hintGameObject = new GameObject.BodyConstructor(
                ModelManager.createSphere(1),
                "hint",
                new btSphereShape(0.5f),
                new Vector3(0f, 0f, 0f),
                1f, 1, true)
                .construct();
        hintGameObject.body.setCollisionFlags( playerGameObject.body.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

        for (int i = 0; i < 10; i++)
            hintsObjectList.add(hintGameObject);

        gameObjectsList.add(groundGameObject);
        gameObjectsList.add(sphereGameObject);
        gameObjectsList.add(boxGameObject);
        //gameObjectsList.add(skylandGameObject);
        gameObjectsList.add(playerGameObject);
        gameObjectsList.add(rectangleGameObject);
        gameObjectsList.add(cylinderGameObject);
        //gameObjectsList.add(pepe1GameObject);
        //gameObjectsList.add(pepe2GameObject); //TODO ten obiekt zwraca lampe
        //gameObjectsList.add(owlMGameObject);
        gameObjectsList.add(owlSGameObject);
        //gameObjectsList.add(enviromentGameObject);
        gameObjectsList.add(streeLampGameObject);
        gameObjectsList.add(woodGameObject);

        for (GameObject go : gameObjectsList) {
            objectInstances.add(go.getInstance());
            world.addRigidBody(go.getBody());
        }

        createWall();

    }

    public void renderWorld() {

        cam.changeFieldOfView();
        cam.update(playerGameObject.instance.transform.getTranslation(new Vector3().Zero));
        cam.followPlayer();

        // 3D
        try {
            // TODO jesli nie dziala znimic parametry
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

    public void createRandomGameObject() {
        GameObject sample = new GameObject.BodyConstructor(
                ModelManager.createRectangle(2f, 1f, 3f),
                "PEPE",
                null,
                new Vector3(0f, 15f, 0f),
                2f, 1f, true)
                .construct();
        sample.body.setRestitution(.8f);
        sample.body.setFriction(0.0f);

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

    public void removeGameObject(GameObject gameObject) {

        // TODO ZAWSZE WYWALA BLAD
        if (gameObject != null) {
            try {
                world.removeRigidBody(gameObject.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TAK DZIALA LEPIEJ
            //gameObject.body.dispose();
            //gameObject.model.dispose();
            //gameObject.destroy();
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

            //System.out.println(getMaxVelecity(go1.body.getLinearVelocity()) + " : " + getMaxVelecity(go2.body.getLinearVelocity()));
            //System.out.println( customObjectData1.toString() + " : " + customObjectData2.toString() );

            if ( go1.getUsetData().getHp() < 0f && go1.getUsetData().isDestructible()) {
                go2.body.setLinearVelocity(go2.body.getLinearVelocity().scl(go1.body.getInvMass()));
                particleUtils.boomEffect(go1.body.getWorldTransform().getTranslation(new Vector3()));
                removeGameObject(go1);
            }
            if ( go2.getUsetData().getHp() < 0f && go2.getUsetData().isDestructible()) {
                go1.body.setLinearVelocity(go1.body.getLinearVelocity().scl(go2.body.getInvMass()));
                particleUtils.boomEffect(go2.body.getWorldTransform().getTranslation(new Vector3()));
                removeGameObject(go2);
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

    public ArrayList<GameObject> getHintsObjectList() {
        return hintsObjectList;
    }

    public void setHintsObjectList(ArrayList<GameObject> hintsObjectList) {
        this.hintsObjectList = hintsObjectList;
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

    public GameObject getSkylandGameObject() {
        return skylandGameObject;
    }

    public void setSkylandGameObject(GameObject skylandGameObject) {
        this.skylandGameObject = skylandGameObject;
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

    public GameObject getHintGameObject() {
        return hintGameObject;
    }

    public void setHintGameObject(GameObject hintGameObject) {
        this.hintGameObject = hintGameObject;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}
