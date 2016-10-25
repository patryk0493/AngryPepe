package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class AngryPepeMain extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	private PerspectiveCamera perspectiveCamera;
	private Texture texture;

	boolean isGameView = false;

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


		perspectiveCamera = new PerspectiveCamera(90, w, h);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 1000f;
		perspectiveCamera.translate(0, 0, 0);
		perspectiveCamera.position.set(w/2, h/2, 300);

		texture = new Texture(Gdx.files.internal("scene.jpg"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("badlogic.jpg"));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		perspectiveCamera.fieldOfView = 60 + (int)(zoom*50);
		perspectiveCamera.update();

		batch.begin();

		batch.setProjectionMatrix(perspectiveCamera.combined);

		// TODO enviroment
		batch.draw(texture, 0, 0);
		batch.draw(img, parallaxDelta, parallaxDelta);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub

			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
								 int button) {

			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			// TODO Auto-generated method stub
			Vector3 transVec = new Vector3(-deltaX * zoom, deltaY * zoom, 0);
			perspectiveCamera.translate(transVec);

			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
							 Vector2 pointer1, Vector2 pointer2) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void pinchStop() {

		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

}
