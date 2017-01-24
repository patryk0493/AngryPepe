package pl.kielce.tu.angrypepe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import static pl.kielce.tu.angrypepe.CameraManager.View.SIDE;

/**
 * Obiekt kamery perspektywicznej.
 * @author Patryk Eliasz, Karol Rębiś */
public class CameraManager extends PerspectiveCamera{

    /**
     * Kąt widzenia.
     */
    public float viewAngle = 100;
    /**
     * Czy kamera podąża za obiektem gry.
     */
    public boolean isFollow = true;
    /**
     * Zoom.
     */
    public float zoom = 0.2f;
    /**
     * Skala.
     */
    public float initialScale;
    /**
     * Obecny widok - domyślnie z boku.
     */
    public View currentView = SIDE;

    /**
     * Typ wyliczeniowy dostępnych widoków.
     */
    public enum View {
        /**
         * Widok z boku.
         */
        SIDE,
        /**
         * Widok z góry.
         */
        TOP,
        /**
         * Widok z góry.
         */
        FRONT
    }

    /**
     * Podstawowy konstruktor klasy CameraManager.
     */
    public CameraManager () {
        super(100, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.near = 0.1f;
        this.far = 200f;
        this.position.set(new Vector3(0f, 0f, 200f));
    }

    /**
     * Zmiana pola widzenia w stopniach.
     */
    public void changeFieldOfView() {
        this.fieldOfView = 40 + (int)(zoom * 30);
        this.update();
    }

    /**
     * Obsługa przemieszczenia kamery.
     *
     * @param x długość wektora w osi X
     * @param y długość wektora w osi Y
     */
    public void panCamera(float x, float y) {
        final float scalar = 0.2f;
        Vector3 transVec = new Vector3(-x * zoom * scalar, y * zoom * scalar, 0);
        this.translate(transVec);
    }

    /**
     * Aktualizowanie widoku.
     *
     * @param playerPos wektor zawierający pozycję obiektu gracza
     */
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

    /**
     * Ograniczenie przemieszczenia kamery w osi Y i X.
     */
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

    /**
     * Ustawienie widoku z boku.
     *
     * @param playerPos wektor zawierający pozycję obiektu gracza
     */
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

    /**
     * Ustawienie widoku z góry.
     *
     * @param playerPos wektor zawierający pozycję obiektu gracza
     */
    public void setTopCameraView(Vector3 playerPos) {
        if(isFollow) {
            this.position.x = this.position.x + (playerPos.x - this.position.x) * 0.05f;
            this.position.y = this.position.y + 1.2f + (playerPos.y - this.position.y) * 0.05f;
            this.position.z = this.position.z + (playerPos.z - this.position.z) * 0.05f;
            this.lookAt(playerPos);
        }

        this.up.set(new Vector3(0, 1, 0));
        this.update();
    }

    /**
     * Ustawienie widoku z przodu,
     *
     * @param playerPos wektor zawierający pozycję obiektu gracza
     */
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


    /**
     * Obliczenie nowej wartości powiększenia
     *
     * @param initialDistance wstępna wartość odległości
     * @param distance        końcowa wartość odległości
     */
    public void calculateZoom(float initialDistance, float distance) {
        float ratio = initialDistance / distance;
        zoom = MathUtils.clamp(initialScale * ratio, 0.1f, 1f);
    }

    /**
     * Zmiana wartości powiększenia
     *
     * @param amount wartość
     */
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

    /**
     * Aktualizacja powiększenia.
     */
    public void updateInitialScale() {
        initialScale = zoom;
    }

    /**
     * Aktualizacja widoku.
     *
     * @param width  Szerokość
     * @param height Wysokość
     */
    public void updateViewport(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.update(true);
    }

    /**
     * Pobranie kąta widzenia.
     *
     * @return kąt widzenia
     */
    public float getViewAngle() {
        return viewAngle;
    }

    /**
     * Ustawienie kąta widzenia
     *
     * @param viewAngle kąt widzenia
     */
    public void setViewAngle(float viewAngle) {
        this.viewAngle = viewAngle;
    }

    /**
     * Czy podąża za obiektem?
     *
     * @return wartość logiczna
     */
    public boolean isFollow() {
        return isFollow;
    }

    /**
     * Uustawienie podążania za obiektem.
     *
     * @param follow czy podąża
     */
    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    /**
     * Ustawienie widoku
     *
     * @param currentView wartość typu View
     */
    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }

}
