package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

public class AngryPepeMain extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	private PerspectiveCamera perspectiveCamera;
	public Environment environment;

	private Texture texture;

	public ModelBatch modelBatch;
	public Model model;
	public ModelInstance instance;

	boolean isGameView = false;

	private Box2DDebugRenderer b2dr;
	private World world;
	private Body player;
	public final int PPM = 32;

	private InputMultiplexer inputMultiplexer;
	private MyInputProcessor inputProcessor;
	private MyGestureHandler gestureHandler;

	public float zoom = 1.0f;
	public float parallaxDelta = 0.5f;
	
	@Override
	public void create () {

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		initaliseInputProcessors();
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();


		perspectiveCamera = new PerspectiveCamera(90, w, h);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 500f;
		perspectiveCamera.position.set(w/2, h/2, 420);
		perspectiveCamera.update();

		texture = new Texture(Gdx.files.internal("scene.jpg"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createBox(100f, 100f, 100f,
				new Material(ColorAttribute.createDiffuse(Color.RED)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		instance = new ModelInstance(model);
		instance.transform.translate(600f, 200f, 0);

		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("badlogic.jpg"));

		world = new World(new Vector2(0, -9.8f), false);
		b2dr = new Box2DDebugRenderer();

		player = createBox(-180, 300, 132, 132, false);
		createBox(0, -300, 264, 232, true);

		prepareEnviroment();
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		b2dr.render(world, perspectiveCamera.combined.scl(PPM));

		perspectiveCamera.fieldOfView = 60 + (int)(zoom*50);
		perspectiveCamera.update();
		batch.setProjectionMatrix(perspectiveCamera.combined);

		batch.begin();
		world.step(1 / 60f, 6, 2);

		// TODO enviroment
		batch.draw(texture, 0, 0);
		batch.draw(img, parallaxDelta, parallaxDelta);

		batch.end();
		modelBatch.begin(perspectiveCamera);

		instance.transform.rotate(new Vector3(1f, 1f, 1f), 1f);
		modelBatch.render(instance, environment);

		modelBatch.end();
	}

	public Body createBox(int x, int y, int width, int height, boolean isStatic) {
		Body pBody;
		BodyDef def = new BodyDef();
		FixtureDef fixture;

		if(isStatic)
			def.type = BodyDef.BodyType.StaticBody;
		else
			def.type = BodyDef.BodyType.DynamicBody;


		def.position.set(x / PPM, y / PPM);
		def.fixedRotation = false;
		pBody = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

		fixture = new FixtureDef();
		fixture.friction = 0.1f;
		fixture.density = 1;
		fixture.shape = shape;
		pBody.setTransform(0, 0, 170);

		pBody.createFixture(fixture);

		shape.dispose();
		return pBody;
	}

	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		world.dispose();
		b2dr.dispose();
	}

	public void prepareEnviroment() {

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(1f, 0 , 0)));

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
				zoom += 0.1f;
			}

			//Zoom in
			if (amount < 0 && zoom > 0.1) {
				zoom -= 0.1f;
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
			player.applyForceToCenter(300f, 300.5f, false);
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


	}

	@Override
	public void pause() {


	}

	@Override
	public void resume() {


	}

}
