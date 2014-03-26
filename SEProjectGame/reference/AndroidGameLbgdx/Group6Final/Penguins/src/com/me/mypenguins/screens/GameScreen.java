package com.me.mypenguins.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.me.mypenguins.model.GameWorld;
import com.me.mypenguins.view.WorldRenderer;
import com.me.mypenguins.screens.TiledMapHelper;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;


import java.lang.Math;
import java.text.DecimalFormat;



public class GameScreen implements Screen, InputProcessor{
	
	public static final float PIXELS_PER_METER = 60.0f;
	private final float WATER_LEVEL = 1.4f,
			BUOYANCY = 50,
			WATER_DRAG = 1;

	private float waterCurrent = 0,
			fishSpeed = 10.0f;

	private GameWorld game_world;
	private WorldRenderer renderer;
	private long lastRender;
    private int fishCount = 6;
	public Sprite rampSprite,
	pSprite,
	fSprite;

	private SpriteBatch batch = new SpriteBatch();
	private SpriteBatch fontSpriteBatch;
	private BitmapFont font;
	private TiledMapHelper tiledMapHelper;
	private ShapeRenderer shapeRenderer = new ShapeRenderer();

	boolean firstTouch = true,
			firstRamp = true,
			fishTurn = false,
			rampDragged = true,
			paused = false;

	private int touchUp = 0,
			width,
			height;

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 * 
	 * This is the game loop.
	 * The cameras are updated, the sprites are drawn and the world is updated.
	 */
	
	public void render(float delta) {
		
		long now = System.nanoTime();
		Vector2 slopevect[] = game_world.getSlopeVects();

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0.5f, 0.9f, 0);
		
		/* These if statements are to keep the camera in sync with the penguin unless the camera must go outside of 
		 * the level's bounds.
		 */
		
		tiledMapHelper.getCamera().position.x = PIXELS_PER_METER * game_world.getPenguin().getPosition().x;

		if (tiledMapHelper.getCamera().position.x < Gdx.graphics.getWidth() / 2) {
			tiledMapHelper.getCamera().position.x = Gdx.graphics.getWidth() / 2;
		}
		if (tiledMapHelper.getCamera().position.x >= tiledMapHelper.getWidth()
				- Gdx.graphics.getWidth() / 2) {
			tiledMapHelper.getCamera().position.x = tiledMapHelper.getWidth()
					- Gdx.graphics.getWidth() / 2;
		}

		if (tiledMapHelper.getCamera().position.y < Gdx.graphics.getHeight() / 2) {
			tiledMapHelper.getCamera().position.y = Gdx.graphics.getHeight() / 2;
		}
		if (tiledMapHelper.getCamera().position.y >= tiledMapHelper.getHeight()
				- Gdx.graphics.getHeight() / 2) {
			tiledMapHelper.getCamera().position.y = tiledMapHelper.getHeight()
					- Gdx.graphics.getHeight() / 2;
		}

		tiledMapHelper.getCamera().update();
		tiledMapHelper.render();

		tiledMapHelper.getCamera().position.x = PIXELS_PER_METER * game_world.getPenguin().getPosition().x;

		shapeRenderer.setProjectionMatrix(renderer.getCam().combined);
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.begin(ShapeType.FilledTriangle);
		shapeRenderer.filledTriangle(slopevect[0].x + game_world.getSlopeBodyPos().x, slopevect[0].y + game_world.getSlopeBodyPos().y, slopevect[1].x + game_world.getSlopeBodyPos().x, slopevect[1].y + game_world.getSlopeBodyPos().y, slopevect[2].x + game_world.getSlopeBodyPos().x, slopevect[2].y + game_world.getSlopeBodyPos().y);
		shapeRenderer.end();

		if(game_world.getPenguin().getPosition().y < WATER_LEVEL) {// check if penguin is below water line
			activateWater();
		}

		placeFishInWater();
		drawAllSprites();
        displayText();
        
		/**
		 * Draw this last, so we can see the collision boundaries on top of the
		 * sprites and map.
		 */

		now = System.nanoTime();
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}

		lastRender = now;
		renderer.render(tiledMapHelper.getWidth(), tiledMapHelper.getHeight());
		game_world.update();
		if( game_world.getPenguin().getLinearVelocity().x < 0.002f && game_world.getPenguin().getPosition().y < WATER_LEVEL){
			paused = true;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			game_world.writeFile();
			if( game_world.round <= 3)
				show();
			else {
				game_world.reset();
				game_world.writeFile();
				System.exit(0);
			}
		}
	}// end render()
	
	/*
	 *    initializeFont()
	 *  
	 *    Reads the font information from files.
	 */
	
    public void initializeFont(){
    	fontSpriteBatch = new SpriteBatch();
    	font = new BitmapFont(Gdx.files.internal("data/latin.fnt"), 
    			Gdx.files.internal("data/latin_0.png"), false);
    }
    
    /*
     *    displayText()
     */
    
    public void displayText(){
    	outputFishHit(0, height * 29 / 30);
    	outputSlopeAngle(width * 12 / 30, height * 29 / 30);
    }
    
    /*
     *    outputSlopeAngle()
     *    
     *    Calculates the angle of the slope and displays it at the given pixel coordinates.
     */
    
    public void outputSlopeAngle(int xpixel, int ypixel){
    	Vector2 slopedim = game_world.getSlopeBodyDim();
    	double angle = Math.toDegrees(Math.atan(slopedim.y / slopedim.x));
    	DecimalFormat df = new DecimalFormat("##.##");
    	CharSequence a = "Slope angle = " + df.format(angle);
    	fontSpriteBatch.begin();
    	font.setColor(0f, 0f, 100f, 1f);
    	font.draw(fontSpriteBatch, a, xpixel, ypixel);
    	fontSpriteBatch.end();
    }
    
    /*
     *    outputFishHit()
     *    
     *    Calculates the number of fish hit and displays the number at the given pixel coordinates.
     */
    
    public void outputFishHit(int xpixel, int ypixel){
    	int fishHit = fishCount - game_world.FISH_COUNT;
    	CharSequence a = "Fish hit = " + fishHit;
    	fontSpriteBatch.begin();
    	font.setColor(0f, 0f, 100f, 1f);
    	font.draw(fontSpriteBatch, a, xpixel, ypixel);
    	fontSpriteBatch.end();
    }
    
    /*
     *    placeFishInWater()
     *    
     */
    
	public void placeFishInWater(){
		/**
		 * Place fish in water and get them to swim end to end forever
		 */
		for(int i = 0; i < game_world.FISH_SCHOOL; i++) {
			if(game_world.getFish(i).getPosition().x >= 30) {
				fishTurn = true;
			}
			
			else if(game_world.getFish(i).getPosition().x <= 10)
				fishTurn = false;
				
			if(!fishTurn) {
				if(game_world.getFish(i).getPosition().y < WATER_LEVEL-.5)		// check if each fish is below water level
					game_world.getFish(i).applyForceToCenter(fishSpeed, BUOYANCY);
				
				game_world.getFish(i).setLinearDamping(WATER_DRAG);
			}
			else if(fishTurn) {
				if(game_world.getFish(i).getPosition().y < WATER_LEVEL-.5)		// check if each fish is below water level
					game_world.getFish(i).applyForceToCenter(-fishSpeed, BUOYANCY);
					
				game_world.getFishSprite().flip(true, false);
				game_world.getFish(i).setLinearDamping(WATER_DRAG);
			}
		}
	}
	
	/*
	 *    drawAllSprites()
	 */
	
	public void drawAllSprites(){

		pSprite = game_world.getPenguinSprite();
		fSprite = game_world.getFishSprite();

		float pen_x, 
		pen_y; 

		float fish_x,
		fish_y;

		batch.setProjectionMatrix(tiledMapHelper.getCamera().combined);
		batch.begin();

		/*********************************************
		 * Draw Penguin Sprite on body as body moves
		 *********************************************/
		pen_x = PIXELS_PER_METER*(game_world.getPenguin().getWorldCenter().x) - pSprite.getWidth()/2;
		pen_y = PIXELS_PER_METER*(game_world.getPenguin().getWorldCenter().y) - pSprite.getHeight()/2;

		pSprite.setPosition(pen_x-5, pen_y+7);

		pSprite.setOrigin(game_world.getPenguin().getPosition().x/2, game_world.getPenguin().getPosition().y/2);
		pSprite.setRotation(MathUtils.radiansToDegrees*game_world.getPenguin().getAngle());
		pSprite.draw(batch);

		/*********************************************
		 * Draw Fish Sprite on bodies as bodies move
		 *********************************************/
		for( int i = 0; i < game_world.FISH_SCHOOL; i++ ){

			Body fish = game_world.getFish(i);

			if( fish.getUserData() == "fish" ){
				fish_x = PIXELS_PER_METER*(game_world.getFish(i).getPosition().x) - fSprite.getWidth()/2;
				fish_y = PIXELS_PER_METER*(game_world.getFish(i).getPosition().y) - fSprite.getHeight()/2;
				fSprite.setPosition(fish_x+55, fish_y+55);

				fSprite.setOrigin(game_world.getFish(i).getPosition().x/2, game_world.getFish(i).getPosition().y/2);
				fSprite.setRotation(MathUtils.radiansToDegrees*game_world.getFish(i).getAngle());
				fSprite.draw(batch);

			}

		}
		batch.end();
	}// end drawAllSprites()

	/*
	 *    activateWater()
	 */
	
	public void activateWater() {
		game_world.getPenguin().applyForceToCenter(waterCurrent, BUOYANCY);
		game_world.getPenguin().setLinearDamping(WATER_DRAG);

	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 * 
	 * Provides the dimensions, in pixels of the screen
	 */

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		tiledMapHelper.prepareCamera(width, height);
		renderer.resizeCam(width, height);
		game_world.BOX_TO_WORLD_WIDTH = width / renderer.getCam().viewportHeight;
		game_world.BOX_TO_WORLD_HEIGHT = height / renderer.getCam().viewportHeight;

	}

	@Override
	/*
	 *    show()
	 *    
	 *    Game is initialized here. 
	 */
	public void show() {

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		Gdx.input.setInputProcessor(this);

		tiledMapHelper = new TiledMapHelper();

		tiledMapHelper.setPackerDirectory("data/packer");

		tiledMapHelper.loadMap("data/world/level1/level.tmx");

		tiledMapHelper.prepareCamera(width, height);
		
		game_world = new GameWorld(new Vector2(0.0f, -10.0f), true);
		
		renderer = new WorldRenderer(game_world, true);
		tiledMapHelper.loadCollisions("data/collisions.txt", game_world,
				PIXELS_PER_METER);

		lastRender = System.nanoTime();
		initializeFont();
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
		//super.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		//w.round++;
		game_world.writeFile();
		if( game_world.round <= 3)
			show();// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {


		Vector3 touchDown = new Vector3( Gdx.input.getX(), Gdx.input.getY(), 0);
		renderer.getCam().unproject(touchDown);

		boolean slopeCheck = false;

		if( !slopeCheck ){
			if( firstTouch && touchUp > 1){
				firstTouch = false;
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {

		touchUp++;

		float xpos = x / renderer.TILE_SCALE_FACTOR;
		float ypos = (height - y) / renderer.TILE_SCALE_FACTOR;

		xpos += (renderer.getCam().position.x - (renderer.getCam().viewportWidth / 2));
		ypos += (renderer.getCam().position.y - (renderer.getCam().viewportHeight / 2));

		game_world.checkPenguinTouch(xpos, ypos);
		if( !game_world.checkSlopeTouch(xpos, ypos) ){

			game_world.applyForceToPenguin(xpos, ypos);
		}
		firstTouch = false;

		return true;
	}

	@Override
	/*
	   touchDragged
	   
	   The first part of this function is a little weird so I think it's 
	   worth explaining.

	   The x and y parameters that are received = the pixel coordinates.
	   To convert to the box2d coordinates(meters), they must be divided by the 
	   number of pixels per meter(which has been set elsewhere).

	   Since the converted coordinates are only relative to the current screen
	   they have to be added to the absolute box2d world coordinates that are 
	   displayed at (0,0) on the screen.   
	 */
	public boolean touchDragged(int x, int y, int pointer) {
		float xpos = x / renderer.TILE_SCALE_FACTOR;
		float ypos = (height - y) /renderer.TILE_SCALE_FACTOR;

		game_world.checkPenguinTouch(xpos, ypos);
		if( rampDragged = game_world.checkSlopeTouch(xpos, ypos) && !game_world.penguinClicked ){	
			game_world.moveSlope(xpos, ypos);
		}

		else if(game_world.penguinClicked){
			xpos += (renderer.getCam().position.x - (renderer.getCam().viewportWidth / 2));
			ypos += (renderer.getCam().position.y - (renderer.getCam().viewportHeight / 2));
			if (tiledMapHelper.getslopeX()-5 > xpos)
				game_world.setPenguinPosition(xpos);

		}
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
