package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import static pl.kielce.tu.angrypepe.CameraManager.View.SIDE;

public class CameraManager extends PerspectiveCamera{

    private float viewAngle = 100;
    private boolean isFollow = true;
    private float zoom = 0.2f;
    private float initialScale;
    private View currentView = SIDE;

    enum View {
        SIDE,
        TOP,
        FRONT
    }

    public CameraManager () {
        super(100, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.near = 0.1f;
        this.far = 200f;
        this.position.set(new Vector3(0f, 0f, 200f));
    }

    public void changeFieldOfView() {
        this.fieldOfView = 40 + (int)(zoom * 30);
        this.update();
    }

    public void panCamera(float x, float y) {
        final float scalar = 0.2f;
        Vector3 transVec = new Vector3(-x * zoom * scalar, y * zoom * scalar, 0);
        this.translate(transVec);
    }

    public void update(Vector3 playerPos) {
        super.update();

        limitCameraPosition();
        switch (currentView) {
            case SIDE:
                setSideView(playerPos);
                break;
            case TOP:
                setTopCameraView(playerPos);
                break;
            case FRONT:
                setFrontView(playerPos);
                break;
        }
    }

    public void limitCameraPosition() {

        float yMIN = -1, yMAX= 40;
        float xMIN = -40, xMAX= 40;

        if (this.position.y > yMAX)
            this.position.y = yMAX;
        if (this.position.y < yMIN)
            this.position.y = yMIN;
        if (this.position.x > xMAX)
            this.position.x = xMAX;
        if (this.position.x < xMIN)
            this.position.x = xMIN;
    }

    public void setSideView(Vector3 playerPos) {
        if(isFollow) {
            this.position.x = this.position.x + (playerPos.x - this.position.x) * 0.05f;
            this.position.y = this.position.y + 0.2f + (playerPos.y - this.position.y) * 0.05f;
            this.position.z = this.position.z + 1.1f + (playerPos.z - this.position.z) * 0.05f;
        }
        this.lookAt(new Vector3(playerPos.x , playerPos.y , 0));
        this.up.set(new Vector3(0f, 1f, 0f));
        this.update();
    }

    public void setTopCameraView(Vector3 playerPos) {
        if(isFollow) {
            this.position.x = this.position.x + (playerPos.x - this.position.x) * 0.05f;
            this.position.y = this.position.y + 1.2f + (playerPos.y - this.position.y) * 0.05f;
            this.position.z = this.position.z + (playerPos.z - this.position.z) * 0.05f;
        }
        if(isFollow)
            this.lookAt(playerPos);
        this.up.set(new Vector3(0, 1, 0));
        this.update();
    }

    public void setFrontView(Vector3 playerPos) {
        if(isFollow) {
            this.position.x = this.position.x - 1.2f + (playerPos.x - this.position.x) * 0.05f;
            this.position.y = this.position.y + 0.2f + (playerPos.y - this.position.y) * 0.05f;
            this.position.z = this.position.z + (playerPos.z - this.position.z) * 0.05f;
        }
        this.lookAt(playerPos);
        if(isFollow)
            this.up.set(new Vector3(0, 1, 0));
        this.update();
    }


    public void calculateZoom(float initialDistance, float distance) {
        float ratio = initialDistance / distance;
        zoom = MathUtils.clamp(initialScale * ratio, 0.1f, 1f);
    }

    public void changeZoom(int amount) {
        //Zoom out
        if (amount > 0 && zoom < 1) {
            zoom += 0.02f;
        }

        //Zoom in
        if (amount < 0 && zoom > 0.1) {
            zoom -= 0.02f;
        }
    }

    public void updateInitialScale() {
        initialScale = zoom;
    }

    public void updateViewport(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.update(true);
    }

    public float getViewAngle() {
        return viewAngle;
    }

    public void setViewAngle(float viewAngle) {
        this.viewAngle = viewAngle;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }

    public View getCurrentView() {
        return this.currentView;
    }

}
