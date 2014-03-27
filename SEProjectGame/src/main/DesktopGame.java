package main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopGame {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.width = 800;
		cfg.height = 600;
		cfg.useGL20 = false;
		
		new LwjglApplication(new SuperStarPlatformer(), cfg);
	}

}
