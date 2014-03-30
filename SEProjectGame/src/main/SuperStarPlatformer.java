package main;

import screen.GameScreen;
import screen.PauseScreen;
import screen.TitleScreen;

import com.badlogic.gdx.Game;

public class SuperStarPlatformer extends Game {
	
	private TitleScreen title;
	public TitleScreen getTitle() {
		return title;
	}

	public PauseScreen getPause() {
		return pause;
	}

	public GameScreen getGame() {
		return game;
	}

	private PauseScreen pause;
	private GameScreen game;
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		title = new TitleScreen(this);
		pause = new PauseScreen(this);
		game = new GameScreen(this);
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
