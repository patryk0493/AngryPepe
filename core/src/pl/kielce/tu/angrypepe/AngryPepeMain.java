package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;

public class AngryPepeMain extends ApplicationAdapter {


	public WorldManager worldManager;

	boolean isGameView = false;

	private InputMultiplexer inputMultiplexer;
	private MyInputProcessor inputProcessor;
	private MyGestureHandler gestureHandler;


	BitmapFont font;
	SpriteBatch batch;

	public float w, h;


	@Override
	public void create () {

		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		initaliseInputProcessors();

		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("myFont.fnt"));

		worldManager = new WorldManager();
		worldManager.initWorld();

	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin();

		font.setColor(Color.BLUE);
		font.draw(batch,"0", Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);

		batch.end();

		worldManager.renderWorld();
	}
	
	@Override
	public void dispose () {

		worldManager.dispose();
		Gdx.app.log(this.getClass().getName(), "Disposed");

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

		private float startX, startY;
		int lastX;
		int lastY;
		@Override
		public boolean scrolled(int amount) {
			worldManager.getCam().changeZoom(amount);

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

			final float power = 6;

			if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
				worldManager.applyCentralImpulseToPlayer(new Vector3(0, power * 2, 0));
			}

			if(Gdx.input.isKeyPressed(Input.Keys.A))
				worldManager.createRandomGameObject();

			if(Gdx.input.isKeyPressed(Input.Keys.P)) {
				worldManager.getCam().setCurrentView(CameraManager.View.SIDE);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.O)) {
				worldManager.getCam().setCurrentView(CameraManager.View.TOP);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.I)) {
				worldManager.getCam().setCurrentView(CameraManager.View.FRONT);
			}

			if(Gdx.input.isKeyPressed(Input.Keys.D)) {
				worldManager.removeGameObject(worldManager.getBoxGameObject());
			}

			if(Gdx.input.isKeyPressed(Input.Keys.R)) {
				worldManager.resetWorld();
			}

			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))
				worldManager.applyCentralImpulseToPlayer(new Vector3(-power, 0, 0));
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT))
				worldManager.applyCentralImpulseToPlayer(new Vector3(power, 0, 0));
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP))
				worldManager.applyCentralImpulseToPlayer(new Vector3(0, 0, -power));
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN))
				worldManager.applyCentralImpulseToPlayer(new Vector3(0, 0, power));

			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
								 int button) {

			Gdx.app.log("LOGGER", worldManager.getObject(screenX, screenY) +"");
			Gdx.app.log("TOUCH DOWN: ", "x: " + screenX + " y:" + screenY);

			if (worldManager.getObject(screenX, screenY).contains(13)) {
				worldManager.setPulling(true);
				startX = screenX;
				startY = screenY;
			}
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {

			Gdx.app.log("TOUCH UP: ", "x: " + screenX + " y:" + screenY);
			if (worldManager.isPulling()) {
				float maxPower = 30f;
				float powerScale = 0.4f;
				float powerX = (startX - screenX) * powerScale;
				float powerY = (startY - screenY) * powerScale;

				if (powerX > maxPower)
					powerX = maxPower;
				if (powerX < -maxPower)
					powerX = -maxPower;
				if (powerY > maxPower)
					powerY = maxPower;
				if (powerY < -maxPower)
					powerY = -maxPower;

				Gdx.app.log("POWER: ", "x: " + powerX + " y:" + powerY);
				worldManager.applyCentralImpulseToPlayer(new Vector3(powerX, -powerY, 0));

				worldManager.setPulling(false);
			}
			worldManager.getCam().setFollow(true);
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
			worldManager.getCam().updateInitialScale();
			/*
			getObject((int) x, (int) y);
			startX = x;
			startY = y;*/

			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {

			worldManager.getCam().calculateZoom(initialDistance, distance);
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

			if (!worldManager.isPulling()) {
				worldManager.getCam().panCamera(deltaX, deltaY);
				worldManager.getCam().setFollow(false);
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


	@Override
	public void resize(int width, int height) {
		w = width;
		h = height;
		worldManager.getCam().updateViewport(width, height);
	}

	@Override
	public void pause() {


	}

	@Override
	public void resume() {


	}

}
