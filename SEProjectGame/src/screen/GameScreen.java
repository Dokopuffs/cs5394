package screen;

import items.QueuedBullet;

import java.util.ArrayList;
import java.util.List;

import screen.TitleScreen;
import koalio.SuperKoalio.Koala;
import main.SuperStarPlatformer;
import characters.Bullet;
import characters.Entity;
import characters.Enemy;
import characters.PlayerCharacter;
import characters.PlayerCharacter.Player;
import characters.PlayerState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class GameScreen implements Screen {

	static float WIDTH;
	static float HEIGHT;
	static float MAX_VELOCITY = 10f;
	static float JUMP_VELOCITY = 40f;
	static float DAMPING = 0.87f;
	
	private Array<Rectangle> tiles = new Array<Rectangle>();
	private static final float GRAVITY = -2.5f;
	private Texture playerTexture;
	private Texture bulletTexture;
	private Texture batTexture;
	private Texture spiderTexture;
	private SuperStarPlatformer ssp;
	private TiledMap level;
	private OrthographicCamera camera;
	private Animation walk;
	private Animation jump;
	private Animation stand;
	private List<Entity> entityList;
	private OrthogonalTiledMapRenderer renderer;
	private TextureRegion bulletReg;
	private TextureRegion batRegion;
	private TextureRegion spiderRegion;
	private TextureRegion enemyReg3;
	private PlayerCharacter player;
	private Enemy enemy;
	private float totalTime = 0.0f;
	private float lastBulletFired = 0.0f;
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	private List<QueuedBullet> bulletsToBeFired;

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
		// handle change in views and what not
		// clear the screen
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// get the delta time
		
	        
		// update the koala (process input, collision detection, position
		// update)
		updateKoala(delta);
		List<Entity> entitiesToRemove = new ArrayList<Entity>();
		for(QueuedBullet b : bulletsToBeFired){
			fireBullet(b.position, b.facesRight, b.owner);
		}
		bulletsToBeFired.clear();
		for(Entity e : entityList){
			//need to consider bullet-enemy/player collision and enemy-player collisions
			e.Update(delta);
			if (e instanceof Bullet){
				boolean isInCamera = camera.frustum.pointInFrustum(e.position.x, e.position.y, 0f);
				Bullet b = (Bullet) e;
				if (!isInCamera){
					entitiesToRemove.add(e);
				} else {
					//check for collision detection with player or enemy
					Array<Rectangle> tiles = new Array<Rectangle>();
					int startX, startY, endX, endY;
					startX = (int)e.position.x;
					startY = (int)e.position.y;
					endX = startX + (int)e.width;
					endY = startY + (int)e.height;
					getTiles(startX, startY, endX, endY, tiles);
					for(Entity e2 : entityList){
						if (!b.equals(e2)){
 							if((b.owner == Enemy.class && e2 instanceof PlayerCharacter) || (b.owner == PlayerCharacter.class && e2 instanceof Enemy)){
								//Gdx.app.log("CollDet", "bullet checking with enemy/character ");
								Rectangle entityRect = rectPool.obtain();
								entityRect.set(e2.position.x, e2.position.y, e2.width, e2.height);
								Rectangle bulletRect = rectPool.obtain();
								bulletRect.set(b.position.x, b.position.y, b.width, b.height);
								Gdx.app.log("CollDet", "Bullet " + bulletRect + " checking with " + e2.getClass());
								if(bulletRect.overlaps(entityRect)){
									e2.health -= b.damage;
									entitiesToRemove.add(b);
									if(e2.health <= 0){
										entitiesToRemove.add(e2);
									}
								}
							}							
						}
					}				
				}
			} else if(e instanceof Enemy){
				//if enemy hits player, player takes damage and gets knocked back
			}
		}
		entityList.removeAll(entitiesToRemove);
		entitiesToRemove.clear();
		// let the camera follow the koala, x-axis only
		camera.position.x = player.position.x;
		camera.update();

		// set the tile map renderer view based on what the
		// camera sees and render the map
		renderer.setView(camera);
		renderer.render();
		showPlayerHealth(player.health);

		// render the koala
		renderKoala(delta);
		for(Entity e: entityList){
			e.Render(delta);
		}
	}

	private void showPlayerHealth(int health) {
		BitmapFont font = new BitmapFont();
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		font.draw(batch, "Health: " + health, 0.5f, Gdx.graphics.getHeight() - 0.5f);
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
		// instantiate everything here
		int maxWidth = 24, maxHeight = 30;
		playerTexture = new Texture("content/Megaman.png");
		bulletTexture = new Texture("content/bullet.png");
		batTexture = new Texture("content/koalio2.png");
		spiderTexture = new Texture("content/koalio3.png");
		bulletReg = new TextureRegion(bulletTexture, 2, 4, 12, 8);
		batRegion = new TextureRegion( batTexture, 1, 3, 16, 21); //bat
		spiderRegion = new TextureRegion( spiderTexture, 37, 32); //spider
		TextureRegion standReg = new TextureRegion(playerTexture, 94, 4, maxWidth, maxHeight);
		TextureRegion jumpReg = new TextureRegion(playerTexture, 199, 4, maxWidth, maxHeight);
		stand = new Animation(0, standReg);
		jump = new Animation(0, jumpReg);
		TextureRegion[] walkRegs = new TextureRegion[3];
		walkRegs[0] = new TextureRegion(playerTexture, 122, 4, maxWidth, maxHeight);
		walkRegs[1] = new TextureRegion(playerTexture, 147, 4, maxWidth, maxHeight);
		walkRegs[2] = new TextureRegion(playerTexture, 173, 4, maxWidth, maxHeight);
		walk = new Animation(0.15f, walkRegs[0], walkRegs[1], walkRegs[2]);
		walk.setPlayMode(Animation.LOOP_PINGPONG);
		
		entityList = new ArrayList<Entity>();

		// figure out the width and height of the koala for collision
		// detection and rendering by converting a koala frames pixel
		// size into world units (1 unit == 16 pixels)
		WIDTH = 1 / 16f * standReg.getRegionWidth();
		HEIGHT = 1 / 16f * standReg.getRegionHeight();

		// load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
		level = new TmxMapLoader().load("reference/koalio_data/level2.tmx");
		renderer = new OrthogonalTiledMapRenderer(level, 1 / 17f);

		// create an orthographic camera, shows us 30x20 units of the world
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 20);
		camera.update();
		
		bulletsToBeFired = new ArrayList<QueuedBullet>();
		
		
		

		// create the Koala we want to move around the world
		Vector2 charPosition = new Vector2(20,20);
		player = new PlayerCharacter(charPosition, 100, renderer, level, WIDTH, HEIGHT);
		entityList.add(player);
		
		
		//create enemies
		Vector2 enemyPos = new Vector2(20f, 5f);
	
		
		entityList.add(new Enemy(enemyPos,  player.position, 1,  4, batRegion, renderer, level, this));
		
		Vector2 enemyPos2 = new Vector2(5f, 4f);
		entityList.add(new Enemy(enemyPos2, player.position, 2,  8, spiderRegion, renderer, level, this));
		
		
		Vector2 enemyPos3 = new Vector2(30f, 7f);
		entityList.add(new Enemy(enemyPos3, player.position, 1,  2, batRegion, renderer, level, this));
		
		
		Vector2 enemyPos4 = new Vector2(35f, 6f);
		entityList.add(new Enemy(enemyPos4, player.position, 3,  2, batRegion, renderer, level, this));
		
	}
	
	private void updateKoala(float deltaTime) {
		if (deltaTime == 0)
			return;
		player.stateTime += deltaTime;
		totalTime += deltaTime;
		
		 if(player.position.y < .01f || player.health <= 0){
	        	
	        	ssp.setScreen(ssp.getGame());
	        }

		// check input and apply to velocity & state
		if ((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.75f, 1))
				&& player.grounded) {
			player.velocity.y +=  JUMP_VELOCITY;
			player.state = PlayerState.Jumping;
			player.grounded = false;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)
				|| isTouched(0, 0.25f)) {
			player.velocity.x = - MAX_VELOCITY;
			if (player.grounded)
				player.state = PlayerState.Walking;
			player.facesRight = false;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) {
			player.velocity.x =  MAX_VELOCITY;
			if (player.grounded)
				player.state = PlayerState.Walking;
			player.facesRight = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.F)){
			//fire off a bullet
			if ((totalTime - lastBulletFired) > 0.08f)
				fireBullet(player.position, player.facesRight, PlayerCharacter.class);
				lastBulletFired = totalTime;
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		// apply gravity if we are falling
		player.velocity.add(0, GRAVITY);

		// clamp the velocity to the maximum, x-axis only
		if (Math.abs(player.velocity.x) >  MAX_VELOCITY) {
			player.velocity.x = Math.signum(player.velocity.x)
					*  MAX_VELOCITY;
		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(player.velocity.x) < 1) {
			player.velocity.x = 0;
			if (player.grounded)
				player.state =  PlayerState.Standing;
		}

		// multiply by delta time so we know how far we go
		// in this frame
		player.velocity.scl(deltaTime);

		// perform collision detection & response, on each axis, separately
		// if the koala is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left
		Rectangle koalaRect = rectPool.obtain();
		koalaRect.set(player.position.x, player.position.y, WIDTH,
				HEIGHT);
		int startX, startY, endX, endY;
		if (player.velocity.x > 0) {
			startX = endX = (int) (player.position.x + WIDTH + player.velocity.x);
		} else {
			startX = endX = (int) (player.position.x + player.velocity.x);
		}
		startY = (int) (player.position.y);
		endY = (int) (player.position.y + HEIGHT);
		getTiles(startX, startY, endX, endY, tiles);
		koalaRect.x += player.velocity.x;
		for (Rectangle tile : tiles) {
			if (koalaRect.overlaps(tile)) {
				player.velocity.x = 0;
				break;
			}
		}
		koalaRect.x = player.position.x;

		// if the koala is moving upwards, check the tiles to the top of it's
		// top bounding box edge, otherwise check the ones to the bottom
		if (player.velocity.y > 0) {
			startY = endY = (int) (player.position.y + HEIGHT + player.velocity.y);
		} else {
			startY = endY = (int) (player.position.y + player.velocity.y);
		}
		startX = (int) (player.position.x);
		endX = (int) (player.position.x + WIDTH);
		getTiles(startX, startY, endX, endY, tiles);
		koalaRect.y += player.velocity.y;
		
		
		for (Rectangle tile : tiles) {
			if (koalaRect.overlaps(tile)) {
				// we actually reset the koala y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if (player.velocity.y > 0) {
					player.position.y = tile.y - HEIGHT;
					// we hit a block jumping upwards, let's destroy it!
					TiledMapTileLayer layer = (TiledMapTileLayer) level
							.getLayers().get(1);
					layer.setCell((int) tile.x, (int) tile.y, null);
				} else {
					player.position.y = tile.y + tile.height;
					// if we hit the ground, mark us as grounded so we can jump
					player.grounded = true;
				}
				player.velocity.y = 0;
				break;
			}
		}
		rectPool.free(koalaRect);

		// unscale the velocity by the inverse delta time and set
		// the latest position
		player.position.add(player.velocity);
		player.velocity.scl(1 / deltaTime);

		// Apply damping to the velocity on the x-axis so we don't
		// walk infinitely once a key was pressed
		player.velocity.x *= DAMPING;

	}
	
	public void fireBullet(Vector2 position, boolean facesRight, Class<?> owner) {
		Vector2 newPos = new Vector2(position.x, position.y + .5f);
		if(facesRight){
			newPos.x += .5f;
			entityList.add(new Bullet(newPos, 2, facesRight, bulletReg, renderer, level, owner));	
		} else {
			newPos.x -= .5f;
			entityList.add(new Bullet(newPos, 2, facesRight, bulletReg, renderer, level, owner));
		}
	}

	private boolean isTouched(float startX, float endX) {
		// check if any finge is touch the area between startX and endX
		// startX/endX are given between 0 (left edge of the screen) and 1
		// (right edge of the screen)
		for (int i = 0; i < 2; i++) {
			float x = Gdx.input.getX() / (float) Gdx.graphics.getWidth();
			if (Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
				return true;
			}
		}
		return false;
	}
	
	private void renderKoala(float deltaTime) {
		// based on the koala state, get the animation frame
		TextureRegion frame = null;
		switch (player.state) {
		case Standing:
			frame = stand.getKeyFrame(player.stateTime);
			break;
		case Walking:
			frame = walk.getKeyFrame(player.stateTime);
			break;
		case Jumping:
			frame = jump.getKeyFrame(player.stateTime);
			break;
		}

		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		if (player.facesRight) {
			batch.draw(frame, player.position.x, player.position.y,  WIDTH,
					 HEIGHT);
		} else {
			batch.draw(frame, player.position.x +  WIDTH, player.position.y,
					- WIDTH,  HEIGHT);
		}
		batch.end();
	}
	
	private void getTiles(int startX, int startY, int endX, int endY,
			Array<Rectangle> tiles) {
		TiledMapTileLayer layer = (TiledMapTileLayer) level.getLayers().get(1);
		rectPool.freeAll(tiles);
		tiles.clear();
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if (cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
	}

	public void addBullet(Vector2 position, boolean facesRight, Class<?> owner) {
		// TODO Auto-generated method stub
		bulletsToBeFired.add(new QueuedBullet(position, facesRight, owner));
	}

}
