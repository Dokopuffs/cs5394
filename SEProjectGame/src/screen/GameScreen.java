package screen;

import main.SuperStarPlatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen implements Screen {

	private SuperStarPlatformer ssp;
	private TiledMap level;
	private OrthographicCamera camera;
	private Animation playerWalk;
	private Animation playerJump;
	private Animation playerStand;
	private OrthogonalTiledMapRenderer renderer;
	
	public GameScreen(SuperStarPlatformer superStarPlatformer) {
		// TODO Auto-generated constructor stub
		this.ssp = superStarPlatformer;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		//handle change in views and what not
		
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// instantiate everything here
		
	}

}
