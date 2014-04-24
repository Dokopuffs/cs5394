package screen;

import main.SuperStarPlatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TitleScreen implements Screen {

	private Texture splashTexture;
	private SuperStarPlatformer ssp;
	private SpriteBatch batch;
	private TextureRegion tr;
	private float time = 0.0f;
	private BitmapFont font;
	
	public TitleScreen(SuperStarPlatformer superStarPlatformer) {
		// TODO Auto-generated constructor stub
		this.ssp = superStarPlatformer;
		font = new BitmapFont();
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		time += delta;
		int width, height;
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		batch.begin();
		 
        // we tell the batch to draw the region starting at (0,0) of the
        // lower-left corner with the size of the screen
        batch.draw( tr , 0, 0, width, height);
        if (time >= 2.0){
        	String msg = "PRESS X TO START";
        	if( (time % 2.0) < 1.0){
        		font.setColor(0, 0, 0, 1);
        		font.draw(batch, msg, (width / 2) - (msg.length() * 4), height / 3);
        	}
        }
 
        // the end method does the drawings
        batch.end();
        
        if (Gdx.input.isKeyPressed(Input.Keys.X) && time >= 2.0){
        	ssp.setScreen(ssp.getGame());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && time >= 2.0){
        	Gdx.app.exit();
        }
        
        
        
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
		splashTexture = new Texture("content/ChronoLogo.png");
		tr = new TextureRegion(splashTexture);		
	}

}
