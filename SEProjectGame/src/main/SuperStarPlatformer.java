package main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class SuperStarPlatformer implements ApplicationListener {

	public static void main(String[] args){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.width = 800;
		cfg.height = 600;
		cfg.useGL20 = true;
		
		new LwjglApplication(new SuperStarPlatformer(), cfg);
	}
	
	@Override
	public void create() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

}
