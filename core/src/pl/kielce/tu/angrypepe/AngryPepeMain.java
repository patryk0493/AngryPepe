package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class AngryPepeMain extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	private PerspectiveCamera perspectiveCamera;
	public Environment environment;

	private Texture texture;
	private Model pepeModel;

	public ModelBuilder modelBuilder;
	public ModelBatch modelBatch;
	private ModelInstance skyInstance;

	private GameObject playerGameObject;
	private GameObject groundGameObject;
	private GameObject sphereGameObject;
	private GameObject boxGameObject;
	private GameObject skylandGameObject;
	private GameObject skyGameObject;

	Array<Model> models;

	boolean isGameView = false;

	private InputMultiplexer inputMultiplexer;
	private MyInputProcessor inputProcessor;
	private MyGestureHandler gestureHandler;

	public float w, h;
	public float zoom = 1.0f;
	public float objectScale = 2f;
	private AssetManager assets;
	private ArrayList<ModelInstance> objectInstances;
	private ArrayList<GameObject> gameObjectsList = new ArrayList<GameObject>();

	private btDefaultCollisionConfiguration collisionConfiguration;
	private btCollisionDispatcher dispatcher;
	private btDbvtBroadphase broadphase;
	private btSequentialImpulseConstraintSolver solver;
	private btDiscreteDynamicsWorld world;
	private Array<btCollisionShape> shapes = new Array<btCollisionShape>();
	private Array<btRigidBody.btRigidBodyConstructionInfo> bodyInfos = new Array<btRigidBody.btRigidBodyConstructionInfo>();
	private Array<btRigidBody> bodies = new Array<btRigidBody>();


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
		perspectiveCamera.position.set(0, 10, 25);
		perspectiveCamera.update();

		texture = new Texture(Gdx.files.internal("sky.jpg"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);


		// Init
		modelBuilder = new ModelBuilder();
		models = new Array<Model>();
		objectInstances = new ArrayList<ModelInstance>();

		Model skyModel = null;
		// MODELS
		Model groundModel = modelBuilder.createBox(30f, 2.5f, 30f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		Model boxModel = modelBuilder.createBox(2f, 2f, 2f,
				new Material(ColorAttribute.createDiffuse(Color.RED)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		Model sphereModel = modelBuilder.createSphere(2, 2, 2, 20, 20,
				new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.GRAY),
						FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		Model headModel = null;
		try {
			assets = new AssetManager();
			assets.load("skyland.g3dj", Model.class);
			assets.load("pepe_box.g3dj", Model.class);
			assets.load("skydome.g3dj", Model.class);
			assets.finishLoading();
			assets.update();
			headModel = assets.get("skyland.g3dj", Model.class);
			pepeModel = assets.get("pepe_box.g3dj", Model.class);
			skyModel = assets.get("skydome.g3dj", Model.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Dodanie do listy modeli
		models.add(skyModel);
		models.add(groundModel);
		models.add(headModel);
		models.add(boxModel);
		models.add(sphereModel);

		skyInstance = new ModelInstance(skyModel);

		skyInstance.transform.scale(20, 20f, 20f);
		//skyInstance.transform.scl(100f, 100f, 100f);

		objectInstances.add(skyInstance);

		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("badlogic.jpg"));

		prepareEnviroment();

		// Initiating Bullet Physics
		Bullet.init();

		//setting up the world
		setupWorld(new Vector3(0, -9.81f, 0));

		// TODO game objects list

		groundGameObject = new GameObject.BodyConstructor(
				groundModel,
				"ground",
				null,
				new Vector3(0f, 0f, 0f),
				0f, 1, false)
				.construct();
		groundGameObject.body.setRestitution(.5f);
		objectInstances.add(groundGameObject.getInstance());
		world.addRigidBody(groundGameObject.getBody());

		sphereGameObject = new GameObject.BodyConstructor(
				sphereModel,
				"sphere",
				new btSphereShape(2 / objectScale),
				new Vector3(0f, 5f, 0f),
				1f, 1, true)
				.construct();
		sphereGameObject.body.setRestitution(.5f);
		objectInstances.add(sphereGameObject.getInstance());
		world.addRigidBody(sphereGameObject.getBody());

		boxGameObject = new GameObject.BodyConstructor(
				boxModel,
				"box",
				new btBoxShape(new Vector3(1, 1, 1)),
				new Vector3(3f, 1f, 0f),
				1f, 1f, true)
				.construct();
		boxGameObject.body.setRestitution(.5f);
		objectInstances.add(boxGameObject.getInstance());
		world.addRigidBody(boxGameObject.getBody());

		skylandGameObject = new GameObject.BodyConstructor(
				headModel,
				"skyland",
				null,
				new Vector3(-2f, 5f, 0f),
				2f, 1f, true)
				.construct();
		skylandGameObject.body.setRestitution(.2f);
		objectInstances.add(skylandGameObject.getInstance());
		world.addRigidBody(skylandGameObject.getBody());

		playerGameObject = new GameObject.BodyConstructor(
				pepeModel,
				"pepexD",
				null,
				new Vector3(0f, 6f, 0f),
				2f, 1f, true)
				.construct();
		playerGameObject.body.setRestitution(.5f);

		objectInstances.add(playerGameObject.getInstance());
		world.addRigidBody(playerGameObject.getBody());
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		perspectiveCamera.fieldOfView = 40 + (int)(zoom*50);
		//perspectiveCamera.lookAt(playerGameObject.instance.transform.getTranslation(new Vector3().Zero));
		perspectiveCamera.update();
		batch.setProjectionMatrix(perspectiveCamera.combined);

		// 2D
		batch.begin();
		batch.draw(texture, -texture.getWidth()/2, -texture.getHeight()/2, 200f, 200f);
		batch.draw(img, 0, 0);
		batch.end();

		world.stepSimulation(Gdx.graphics.getDeltaTime(), 5);
		boxGameObject.getWorldTransform();
		sphereGameObject.getWorldTransform();
		playerGameObject.getWorldTransform();
		skylandGameObject.getWorldTransform();

		// 3D
		modelBatch.begin(perspectiveCamera);
		modelBatch.render(objectInstances, environment);
		modelBatch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();

		modelBatch.dispose();
		for (Model model : models)
			model.dispose();

		for (btRigidBody body : bodies) {
			body.dispose();
		}

		for (btCollisionShape shape : shapes)
			shape.dispose();
		for (btRigidBody.btRigidBodyConstructionInfo info : bodyInfos)
			info.dispose();
		world.dispose();
		collisionConfiguration.dispose();
		dispatcher.dispose();
		broadphase.dispose();
		solver.dispose();
		Gdx.app.log(this.getClass().getName(), "Disposed");

	}

	public void prepareEnviroment() {

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(0f, -1f , 0)));
		//environment.add(new SpotLight().set(Color.WHITE, new Vector3(0, 30, 0), new Vector3(0, 1, 0), 1, 360, 100));

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

	class MyInputProcessor implements InputProcessor {


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
				//playerGameObject.body.applyForce(new Vector3(0, power * 3, 0), new Vector3().Zero);
				//playerGameObject.body.applyCentralForce(new Vector3(0, power * 3, 0));
				//playerGameObject.body.applyTorque(new Vector3(0, power * 30, 0));
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

			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

	}

	class MyGestureHandler implements GestureDetector.GestureListener {

		public float initialScale = 1.0f;

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {

			initialScale = zoom;
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
			// TODO Auto-generated method stub

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

			final float scalar = 0.2f;
			Vector3 transVec = new Vector3(-deltaX * zoom * scalar, deltaY * zoom * scalar, 0);
			perspectiveCamera.translate(transVec);

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
