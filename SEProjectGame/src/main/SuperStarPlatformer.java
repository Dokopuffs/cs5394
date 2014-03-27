package main;

import screen.PauseScreen;
import screen.TitleScreen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class SuperStarPlatformer extends Game {
	
	TitleScreen title;
	PauseScreen pause;
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		title = new TitleScreen(this);
		pause = new PauseScreen(this);
		this.setScreen(title);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		super.render();
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
