package pl.kielce.tu.angrypepe.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import pl.kielce.tu.angrypepe.AngryPepeMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Angry Pepe";
		config.width = 840;
		config.height = 480;
		config.resizable=true;
		config.vSyncEnabled = true;
		config.foregroundFPS = 60;
		new LwjglApplication(new AngryPepeMain(), config);
	}
}
