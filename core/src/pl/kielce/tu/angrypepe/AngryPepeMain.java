package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import java.util.ArrayList;

public class AngryPepeMain extends ApplicationAdapter {

	private CameraManager cam;
	public Environment environment;
	private Vector3 position = new Vector3();
	private boolean isPulling = false;

	public ModelBatch modelBatch;
	private ModelInstance skyInstance;

	private GameObject playerGameObject;
	private GameObject groundGameObject;
	private GameObject sphereGameObject;
	private GameObject boxGameObject;
	private GameObject skylandGameObject;
	private GameObject rectangleGameObject;
	private GameObject cylinderGameObject;

	private GameObject hintGameObject;

	boolean isGameView = false;

	private InputMultiplexer inputMultiplexer;
	private MyInputProcessor inputProcessor;
	private MyGestureHandler gestureHandler;

	MyContactListener contactListener;

	public float w, h;
	private ArrayList<ModelInstance> objectInstances;
	private ArrayList<GameObject> gameObjectsList = new ArrayList<GameObject>();
	private ArrayList<GameObject> hintsObjectList = new ArrayList<GameObject>();

	private btDefaultCollisionConfiguration collisionConfiguration;
	private btCollisionDispatcher dispatcher;
	private btDbvtBroadphase broadphase;
	private btSequentialImpulseConstraintSolver solver;
	private btDiscreteDynamicsWorld world;

	@Override
	public void create () {

		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		initaliseInputProcessors();
		modelBatch = new ModelBatch();

		cam = new CameraManager();

		objectInstances = new ArrayList<ModelInstance>();

		skyInstance = new ModelInstance(ModelManager.SKYDOME_MODEL);

		objectInstances.add(skyInstance);

		prepareEnviroment();

		Bullet.init();

		setupWorld(new Vector3(0, -9.81f, 0));

		groundGameObject = new GameObject.BodyConstructor(
				ModelManager.GROUND,
				"ground",
				null,
				new Vector3(0f, 0f, 0f),
				0f, 1, false)
				.construct();
		System.out.println(groundGameObject.body.getRestitution());

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
				new Vector3(-2f, 5f, 0f),
				2f, 1f, true)
				.construct();

		playerGameObject = new GameObject.BodyConstructor(
				ModelManager.PEPE_MODEL,
				"pepexD",
				null,
				new Vector3(0f, 6f, 0f),
				2f, 1f, true)
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
		gameObjectsList.add(skylandGameObject);
		gameObjectsList.add(playerGameObject);
		gameObjectsList.add(rectangleGameObject);
		gameObjectsList.add(cylinderGameObject);

		for (GameObject go : gameObjectsList) {
			objectInstances.add(go.getInstance());
			world.addRigidBody(go.getBody());
		}

		createWall();

		contactListener = new MyContactListener();
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		cam.changeFieldOfView();
		cam.update(playerGameObject.instance.transform.getTranslation(new Vector3().Zero));
		cam.followPlayer();

		// 3D
		world.stepSimulation(Gdx.graphics.getDeltaTime(), 5);
		for (GameObject gameObject : gameObjectsList) {
			gameObject.getWorldTransform();
		}

		modelBatch.begin(cam);
		modelBatch.render(objectInstances, environment);
		modelBatch.end();
	}
	
	@Override
	public void dispose () {

		modelBatch.dispose();

		world.dispose();
		collisionConfiguration.dispose();
		dispatcher.dispose();
		broadphase.dispose();
		solver.dispose();

		contactListener.dispose();

		Gdx.app.log(this.getClass().getName(), "Disposed");

	}

	public void createRandomGameObject() {
		GameObject sample = new GameObject.BodyConstructor(
				ModelManager.PEPE_MODEL,
				"PEPE",
				null,
				new Vector3(0f, 15f, 0f),
				2f, 1f, true)
				.construct();
		sample.body.setRestitution(.8f);
		sample.body.setFriction(0.5f);

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
						null,
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

	public void initaliseInputProcessors() {

		inputMultiplexer = new InputMultiplexer();

		Gdx.input.setInputProcessor(inputMultiplexer);

		inputProcessor = new MyInputProcessor();
		gestureHandler = new MyGestureHandler();

		inputMultiplexer.addProcessor(new GestureDetector(gestureHandler));
		inputMultiplexer.addProcessor(inputProcessor);
	}

	public void removeGameObject(GameObject gameObject) {

		// TODO CZASEM WYWALA BLAD
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

	class MyInputProcessor implements InputProcessor {

		private float startX, startY;
		int lastX;
		int lastY;
		@Override
		public boolean scrolled(int amount) {

			cam.changeZoom(amount);

			return true;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {

			return false;
		}

		@Override
		public boolean keyDown(int keycode) {
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			return false;
		}

		@Override
		public boolean keyTyped(char character) {

			final float power = 3;

			if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
				playerGameObject.body.applyCentralImpulse( new Vector3(0, power * 3, 0) );
			}

			if(Gdx.input.isKeyPressed(Input.Keys.A))
				createRandomGameObject();

			if(Gdx.input.isKeyPressed(Input.Keys.P)) {
				cam.setCurrentView(CameraManager.View.SIDE);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.O)) {
				cam.setCurrentView(CameraManager.View.TOP);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.I)) {
				cam.setCurrentView(CameraManager.View.FRONT);
			}

			if(Gdx.input.isKeyPressed(Input.Keys.D)) {
				removeGameObject(boxGameObject);
			}

			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))
				playerGameObject.body.applyCentralImpulse(new Vector3(-power, 0, 0));
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT))
				playerGameObject.body.applyCentralImpulse(new Vector3(power, 0, 0));
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP))
				playerGameObject.body.applyCentralImpulse(new Vector3(0, 0, -power));
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN))
				playerGameObject.body.applyCentralImpulse(new Vector3(0, 0, power));

			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
								 int button) {

			Gdx.app.log("LOGGER", getObject(screenX, screenY) +"");
			Gdx.app.log("TOUCH DOWN: ", "x: " + screenX + " y:" + screenY);

			if (getObject(screenX, screenY).contains(6)) {
				isPulling = true;
				startX = screenX;
				startY = screenY;
			}
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {

			Gdx.app.log("TOUCH UP: ", "x: " + screenX + " y:" + screenY);
			if (isPulling) {
				float maxPower = 30f;
				float powerScale = 0.4f;
				float powerX = (startX - screenX) * powerScale;
				float powerY = (startY - screenY) * powerScale;

				if (Math.abs(powerX) > maxPower) {
					powerX = maxPower;
				}
				if (Math.abs(powerY) > maxPower) {
					powerY = -maxPower;
				}

				Gdx.app.log("POWER: ", "x: " + powerX + " y:" + powerY);
				playerGameObject.body.applyCentralImpulse(new Vector3(powerX, -powerY, 0));
				isPulling = false;
			}
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

	}

	class MyGestureHandler implements GestureDetector.GestureListener {

		public float initialScale = 1.0f;

		private float startX, startY;

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			Gdx.app.log("TOUCH DOWN: ", "x: " + x + " y:" + y);
			cam.updateInitialScale();
			/*
			getObject((int) x, (int) y);
			startX = x;
			startY = y;*/

			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {

			cam.calculateZoom(initialDistance, distance);
			return true;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {

			if (!isPulling) {
				cam.panCamera(deltaX, deltaY);
			}

			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
							 Vector2 pointer1, Vector2 pointer2) {

			System.out.println(initialPointer1.toString() + " : " + initialPointer2.toString() +
				" : " + pointer1.toString() + " : " + pointer2.toString());

			return false;
		}

		@Override
		public void pinchStop() {

		}

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
				removeGameObject(go1);

			}
			if ( go2.getUsetData().getHp() < 0f && go2.getUsetData().isDestructible()) {
				go1.body.setLinearVelocity(go1.body.getLinearVelocity().scl(go2.body.getInvMass()));
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
			for (GameObject gameObject : gameObjectsList) {
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

	@Override
	public void resize(int width, int height) {
		w = width;
		h = height;
		cam.updateViewport(width, height);
	}

	@Override
	public void pause() {


	}

	@Override
	public void resume() {


	}

}
