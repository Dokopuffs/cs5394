package com.me.mypenguins.screens;

//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
//import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;


public class MenuScreen implements Screen{

	//private SpriteBatch batch;
	//private Texture menu;
	//private Game myGame;
	private Skin skin;
	Stage ui;
	
	private Window window;
	
	public MenuScreen(){
		//myGame = game;
		//batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("MenuScreen.json"), Gdx.files.internal("MenuScreen.png"));
		window = new Window("Dialog", skin);
		
		ui.addActor(window);
		render( 3f);  
	}
	
	
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		ui.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		ui.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
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
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	
}
