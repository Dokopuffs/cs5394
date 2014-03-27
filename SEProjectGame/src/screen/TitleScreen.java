package screen;

import main.SuperStarPlatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TitleScreen implements Screen {

	private Texture splashTexture;
	private SuperStarPlatformer ssp;
	private SpriteBatch batch;
	private TextureRegion tr;
	
	public TitleScreen(SuperStarPlatformer superStarPlatformer) {
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
	public void render(float arg0) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		 
        // we tell the batch to draw the region starting at (0,0) of the
        // lower-left corner with the size of the screen
        batch.draw( tr , 0, 0, 512, 256);
 
        // the end method does the drawings
        batch.end();
        
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
		batch = new SpriteBatch();
		splashTexture = new Texture("content/superstar.png");
		tr = new TextureRegion(splashTexture);		
	}

}
