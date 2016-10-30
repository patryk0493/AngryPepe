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

	public ModelBuilder modelBuilder;
	public ModelBatch modelBatch;
	public ModelInstance headInstance;
	private ModelInstance sphereInstance;
	private ModelInstance groundInstance;
	private ModelInstance boxInstance;
	private ModelInstance skyInstance;
	Array<Model> models;

	boolean isGameView = false;

	private InputMultiplexer inputMultiplexer;
	private MyInputProcessor inputProcessor;
	private MyGestureHandler gestureHandler;

	public float w, h;
	public float zoom = 1.0f;
	public float parallaxDelta = 0.5f;
	private AssetManager assets;
	private ArrayList<ModelInstance> objectInstances;

	private btDefaultCollisionConfiguration collisionConfiguration;
	private btCollisionDispatcher dispatcher;
	private btDbvtBroadphase broadphase;
	private btSequentialImpulseConstraintSolver solver;
	private btDiscreteDynamicsWorld world;
	private Array<btCollisionShape> shapes = new Array<btCollisionShape>();
	private Array<btRigidBody.btRigidBodyConstructionInfo> bodyInfos = new Array<btRigidBody.btRigidBodyConstructionInfo>();
	private Array<btRigidBody> bodies = new Array<btRigidBody>();
	private btDefaultMotionState sphereMotionState;


	@Override
	public void create () {

		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		initaliseInputProcessors();
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();

		perspectiveCamera = new PerspectiveCamera(67, w, h);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 500f;
		perspectiveCamera.position.set(0, 0, 150);
		perspectiveCamera.update();

		texture = new Texture(Gdx.files.internal("scene.jpg"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		// Init
		modelBuilder = new ModelBuilder();
		models = new Array<Model>();
		objectInstances = new ArrayList<ModelInstance>();

		Model skyModel = null;
		// MODELS
		Model groundModel = modelBuilder.createBox(50f, 5f, 50f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		Model boxModel = modelBuilder.createBox(20f, 20f, 20f,
				new Material(ColorAttribute.createDiffuse(Color.RED)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		Model sphereModel = modelBuilder.createSphere(20, 20, 20, 20, 20,
				new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.GRAY),
						FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		Model headModel = null;
		try {
			assets = new AssetManager();
			assets.load("test.g3dj", Model.class);
			assets.load("sky.g3dj", Model.class);
			assets.finishLoading();
			assets.update();
			headModel = assets.get("test.g3dj", Model.class);
			skyModel = assets.get("sky.g3dj", Model.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Dodanie do listy modeli
		models.add(skyModel);
		models.add(groundModel);
		models.add(headModel);
		models.add(boxModel);
		models.add(sphereModel);

		//Dodanie do listy instancji
		skyInstance = new ModelInstance(skyModel);
		groundInstance = new ModelInstance(groundModel);
		sphereInstance = new ModelInstance(sphereModel);
		headInstance = new ModelInstance(headModel);
		boxInstance = new ModelInstance(boxModel);

		sphereInstance.transform.trn(new Vector3(0, 20f, 0));
		headInstance.transform.scale(10f, 10f, 10f);
		headInstance.transform.trn(100f, 0f, 0f);
		boxInstance.transform.trn(200f, 50f, 0f);
		skyInstance.transform.scale(5f, 5f, 5f);
		//skyInstance.transform.scl(100f, 100f, 100f);

		objectInstances.add(skyInstance);
		objectInstances.add(groundInstance);
		objectInstances.add(sphereInstance);
		objectInstances.add(headInstance);
		objectInstances.add(boxInstance);

		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("badlogic.jpg"));

		prepareEnviroment();

		// Initiating Bullet Physics
		Bullet.init();

		//setting up the world
		setupWorld(new Vector3(0, -9.81f, 1f));

		// creating ground body
		btCollisionShape groundshape = new btBoxShape(new Vector3(100f, 25f, 100f));
		shapes.add(groundshape);
		btRigidBody.btRigidBodyConstructionInfo bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(
				0, null, groundshape, Vector3.Zero);
		this.bodyInfos.add(bodyInfo);
		btRigidBody body = new btRigidBody(bodyInfo);
		body.setRestitution(1.0f);
		bodies.add(body);
		world.addRigidBody(body);

		// creating sphere body
		sphereMotionState = new btDefaultMotionState(sphereInstance.transform);
		sphereMotionState.setWorldTransform(sphereInstance.transform);

		final btCollisionShape sphereShape = new btSphereShape(1f);
		shapes.add(sphereShape);
		bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(1, sphereMotionState, sphereShape, new Vector3(1, 1, 1));
		this.bodyInfos.add(bodyInfo);
		body = new btRigidBody(bodyInfo);
		body.setRestitution(1.0f);
		bodies.add(body);
		world.addRigidBody(body);

		GameObject sampleGameObject = new GameObject.BodyConstructor(
				modelBuilder.createBox(25f, 25f, 25f,
						new Material(ColorAttribute.createDiffuse(Color.BLUE)),
						VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
				"sample",
				new btBoxShape(new Vector3(25f, 25f, 25f)),
				new Vector3(20f, 40f, 0),
				200f).construct();
		btRigidBody sampleBody = sampleGameObject.getBody();
		objectInstances.add(sampleGameObject.getInstance());

		world.addRigidBody(sampleBody);
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		perspectiveCamera.fieldOfView = 60 + (int)(zoom*50);
		perspectiveCamera.update();
		batch.setProjectionMatrix(perspectiveCamera.combined);

		// 2D
		batch.begin();
		batch.draw(texture, -texture.getWidth()/2, -texture.getHeight()/2);
		batch.draw(img, parallaxDelta, parallaxDelta);
		batch.end();

		world.stepSimulation(Gdx.graphics.getDeltaTime(), 5);
		sphereMotionState.getWorldTransform(sphereInstance.transform);
		// 3D
		modelBatch.begin(perspectiveCamera);
		headInstance.transform.rotate(new Vector3(1f, 1f, 0f), 1f);
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
		sphereMotionState.dispose();
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
				zoom += 0.05f;
			}

			//Zoom in
			if (amount < 0 && zoom > 0.1) {
				zoom -= 0.05f;
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

			if (character == 'q') {

			}
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
			System.out.println("Zoom: " + zoom);

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
			Vector3 transVec = new Vector3(-deltaX * zoom, deltaY * zoom, 0);
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
