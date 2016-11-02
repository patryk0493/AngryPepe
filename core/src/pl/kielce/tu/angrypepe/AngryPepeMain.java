package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class AngryPepeMain extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	private PerspectiveCamera perspectiveCamera;
	public Environment environment;
	private Vector3 position = new Vector3();
	private boolean isPulling = false;

	private Texture texture;

	public ModelBatch modelBatch;
	private ModelInstance skyInstance;

	private GameObject playerGameObject;
	private GameObject groundGameObject;
	private GameObject sphereGameObject;
	private GameObject boxGameObject;
	private GameObject skylandGameObject;
	private GameObject rectangleGameObject;
	private GameObject cylinderGameObject;

	boolean isGameView = false;

	private InputMultiplexer inputMultiplexer;
	private MyInputProcessor inputProcessor;
	private MyGestureHandler gestureHandler;

	public float w, h;
	public float zoom = 1.0f;
	private ArrayList<ModelInstance> objectInstances;
	private ArrayList<GameObject> gameObjectsList = new ArrayList<GameObject>();

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
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();

		perspectiveCamera = new PerspectiveCamera(100, w, h);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 500f;
		setSideView();

		texture = new Texture(Gdx.files.internal("sky.jpg"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		objectInstances = new ArrayList<ModelInstance>();

		skyInstance = new ModelInstance(ModelManager.SKYDOME_MODEL);
		skyInstance.transform.scale(20, 20f, 20f);

		objectInstances.add(skyInstance);

		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("badlogic.jpg"));

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
		groundGameObject.body.setRestitution(.5f);

		sphereGameObject = new GameObject.BodyConstructor(
				ModelManager.createSphere(2),
				"sphere",
				new btSphereShape(1),
				new Vector3(0f, 5f, 0f),
				1f, 1, true)
				.construct();
		sphereGameObject.body.setRestitution(.5f);

		boxGameObject = new GameObject.BodyConstructor(
				ModelManager.createBox(4, 4, 4),
				"box",
				new btBoxShape(new Vector3(2, 2, 2)),
				new Vector3(3f, 1f, 0f),
				1f, 1f, true)
				.construct();
		boxGameObject.body.setRestitution(.5f);

		rectangleGameObject = new GameObject.BodyConstructor(
				ModelManager.createRectangle(2f, 6f, 2f),
				"rectangle",
				null,
				new Vector3(5f, 1f, 0f),
				3f, 1f, true)
				.construct();
		rectangleGameObject.body.setRestitution(.5f);

		cylinderGameObject = new GameObject.BodyConstructor(
				ModelManager.createCylinder(2f, 6f, 2f),
				"cylinder",
				new btCylinderShape(new Vector3(1f, 3, 1f)),
				new Vector3(3f, 1f, 7f),
				3f, 1f, true)
				.construct();
		cylinderGameObject.body.setRestitution(.5f);
		//cylinderGameObject.body.translate(new Vector3(0f, 0f, 5f));

		skylandGameObject = new GameObject.BodyConstructor(
				ModelManager.SKYLAND1_MODEL,
				"skyland",
				null,
				new Vector3(-2f, 5f, 0f),
				2f, 1f, true)
				.construct();
		skylandGameObject.body.setRestitution(.2f);

		playerGameObject = new GameObject.BodyConstructor(
				ModelManager.PEPE_MODEL,
				"pepexD",
				null,
				new Vector3(0f, 6f, 0f),
				2f, 1f, true)
				.construct();
		playerGameObject.body.setRestitution(.5f);

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
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		perspectiveCamera.fieldOfView = 40 + (int)(zoom*50);
		perspectiveCamera.update();
		batch.setProjectionMatrix(perspectiveCamera.combined);

		// 2D
		batch.begin();
		batch.draw(texture, -texture.getWidth()/2, -texture.getHeight()/2);
		batch.draw(img, 0, 0);
		batch.end();

		// 3D
		world.stepSimulation(Gdx.graphics.getDeltaTime(), 5);
		for (GameObject gameObject : gameObjectsList) {
			gameObject.getWorldTransform();
		}

		modelBatch.begin(perspectiveCamera);
		modelBatch.render(objectInstances, environment);
		modelBatch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();

		modelBatch.dispose();

		world.dispose();
		collisionConfiguration.dispose();
		dispatcher.dispose();
		broadphase.dispose();
		solver.dispose();
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
		sample.body.setRestitution(.1f);

		gameObjectsList.add(sample);
		objectInstances.add(sample.getInstance());
		world.addRigidBody(sample.getBody());

	}

	public ArrayList<Integer> getObject (int screenX, int screenY) {
		Ray ray = perspectiveCamera.getPickRay(screenX, screenY);
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

	public void setTopCameraView() {
		perspectiveCamera.position.set(0, 20, 0);
		perspectiveCamera.lookAt(new Vector3().Zero);
		perspectiveCamera.update();
	}

	public void setSideView() {
		perspectiveCamera.position.set(0, 10, 25);
		perspectiveCamera.lookAt(new Vector3().Zero);
		perspectiveCamera.update();
	}

	public void removeGameObject(GameObject gameObject) {
		if (gameObject != null) {
			world.removeRigidBody(gameObject.getBody());
			gameObject.destroy();
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

			//Zoom out
			if (amount > 0 && zoom < 1) {
				zoom += 0.02f;
			}

			//Zoom in
			if (amount < 0 && zoom > 0.1) {
				zoom -= 0.02f;
			}

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
				playerGameObject.body.applyImpulse(new Vector3(0, power * 3, 0), new Vector3().Zero);
			}

			if(Gdx.input.isKeyPressed(Input.Keys.A))
				createRandomGameObject();

			if(Gdx.input.isKeyPressed(Input.Keys.P)) {
				setTopCameraView();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.O)) {
				setSideView();
			}

			if(Gdx.input.isKeyPressed(Input.Keys.D)) {
				removeGameObject(boxGameObject);
			}

			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))
				playerGameObject.body.applyImpulse(new Vector3(-power, 0, 0), new Vector3().Zero);
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT))
				playerGameObject.body.applyImpulse(new Vector3(power, 0, 0), new Vector3().Zero);
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP))
				playerGameObject.body.applyImpulse(new Vector3(0, 0, -power), new Vector3().Zero);
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN))
				playerGameObject.body.applyImpulse(new Vector3(0, 0, power), new Vector3().Zero);

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
				float powerX = (startX - screenX) * 0.5f;
				float powerY = (startY - screenY) * 0.5f;
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
			/*Gdx.app.log("TOUCH DOWN: ", "x: " + x + " y:" + y);
			initialScale = zoom;

			getObject((int) x, (int) y);
			startX = x;
			startY = y;*/

			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {

			float ratio = initialDistance / distance;
			zoom = MathUtils.clamp(initialScale * ratio, 0.1f, 1f);
			//System.out.println("Zoom: " + zoom);
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
				final float scalar = 0.2f;
				Vector3 transVec = new Vector3(-deltaX * zoom * scalar, deltaY * zoom * scalar, 0);
				perspectiveCamera.translate(transVec);
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
			return false;
		}

		@Override
		public void pinchStop() {

		}

	}

	@Override
	public void resize(int width, int height) {
		w = width;
		h = height;
		perspectiveCamera.viewportWidth = width;
		perspectiveCamera.viewportHeight = height;
		perspectiveCamera.update(true);
	}

	@Override
	public void pause() {


	}

	@Override
	public void resume() {


	}

}
