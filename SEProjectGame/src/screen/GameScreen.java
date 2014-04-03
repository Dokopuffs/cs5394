package screen;

import java.util.ArrayList;
import java.util.List;

import koalio.SuperKoalio.Koala;
import koalio.SuperKoalio.Koala.State;
import main.SuperStarPlatformer;
import characters.Bullet;
import characters.Entity;

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

	private Array<Rectangle> tiles = new Array<Rectangle>();
	private static final float GRAVITY = -2.5f;
	private Texture playerTexture;
	private Texture bulletTexture;
	private SuperStarPlatformer ssp;
	private TiledMap level;
	private OrthographicCamera camera;
	private Animation walk;
	private Animation jump;
	private Animation stand;
	private List<Entity> entityList;
	private OrthogonalTiledMapRenderer renderer;
	private TextureRegion bulletReg;
	private Koala koala;
	private float totalTime = 0.0f;
	private float lastBulletFired = 0.0f;
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};

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
		for(Entity e : entityList){
			e.Update(delta);
			boolean isInCamera = camera.frustum.pointInFrustum(e.position.x, e.position.y, 0f);
			if (e instanceof Bullet && !isInCamera){
				entitiesToRemove.add(e);
			}
		}
		entityList.remove(entitiesToRemove);
		// let the camera follow the koala, x-axis only
		camera.position.x = koala.position.x;
		camera.update();

		// set the tile map renderer view based on what the
		// camera sees and render the map
		renderer.setView(camera);
		renderer.render();

		// render the koala
		renderKoala(delta);
		for(Entity e: entityList){
			e.Render(delta);
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
		// instantiate everything here
		int maxWidth = 24, maxHeight = 30;
		playerTexture = new Texture("content/Megaman.png");
		bulletTexture = new Texture("content/bullet.png");
		bulletReg = new TextureRegion(bulletTexture, 2, 4, 12, 8);
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
		Koala.WIDTH = 1 / 16f * standReg.getRegionWidth();
		Koala.HEIGHT = 1 / 16f * standReg.getRegionHeight();

		// load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
		level = new TmxMapLoader().load("reference/koalio_data/level2.tmx");
		renderer = new OrthogonalTiledMapRenderer(level, 1 / 17f);

		// create an orthographic camera, shows us 30x20 units of the world
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 20);
		camera.update();

		// create the Koala we want to move around the world
		koala = new Koala();
		koala.position.set(20, 20);
	}

	static class Koala {
		static float WIDTH;
		static float HEIGHT;
		static float MAX_VELOCITY = 10f;
		static float JUMP_VELOCITY = 40f;
		static float DAMPING = 0.87f;

		enum State {
			Standing, Walking, Jumping
		}

		final Vector2 position = new Vector2();
		final Vector2 velocity = new Vector2();
		State state = State.Walking;
		float stateTime = 0;
		boolean facesRight = true;
		boolean grounded = false;
	}
	
	private void updateKoala(float deltaTime) {
		if (deltaTime == 0)
			return;
		koala.stateTime += deltaTime;
		totalTime += deltaTime;

		// check input and apply to velocity & state
		if ((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.75f, 1))
				&& koala.grounded) {
			koala.velocity.y += Koala.JUMP_VELOCITY;
			koala.state = Koala.State.Jumping;
			koala.grounded = false;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)
				|| isTouched(0, 0.25f)) {
			koala.velocity.x = -Koala.MAX_VELOCITY;
			if (koala.grounded)
				koala.state = Koala.State.Walking;
			koala.facesRight = false;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) {
			koala.velocity.x = Koala.MAX_VELOCITY;
			if (koala.grounded)
				koala.state = Koala.State.Walking;
			koala.facesRight = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.F)){
			//fire off a bullet
			if ((totalTime - lastBulletFired) > 0.08f)
				fireBullet(koala.position, koala.facesRight);
				lastBulletFired = totalTime;
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		// apply gravity if we are falling
		koala.velocity.add(0, GRAVITY);

		// clamp the velocity to the maximum, x-axis only
		if (Math.abs(koala.velocity.x) > Koala.MAX_VELOCITY) {
			koala.velocity.x = Math.signum(koala.velocity.x)
					* Koala.MAX_VELOCITY;
		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(koala.velocity.x) < 1) {
			koala.velocity.x = 0;
			if (koala.grounded)
				koala.state = Koala.State.Standing;
		}

		// multiply by delta time so we know how far we go
		// in this frame
		koala.velocity.scl(deltaTime);

		// perform collision detection & response, on each axis, separately
		// if the koala is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left
		Rectangle koalaRect = rectPool.obtain();
		koalaRect.set(koala.position.x, koala.position.y, Koala.WIDTH,
				Koala.HEIGHT);
		int startX, startY, endX, endY;
		if (koala.velocity.x > 0) {
			startX = endX = (int) (koala.position.x + Koala.WIDTH + koala.velocity.x);
		} else {
			startX = endX = (int) (koala.position.x + koala.velocity.x);
		}
		startY = (int) (koala.position.y);
		endY = (int) (koala.position.y + Koala.HEIGHT);
		getTiles(startX, startY, endX, endY, tiles);
		koalaRect.x += koala.velocity.x;
		for (Rectangle tile : tiles) {
			if (koalaRect.overlaps(tile)) {
				koala.velocity.x = 0;
				break;
			}
		}
		koalaRect.x = koala.position.x;

		// if the koala is moving upwards, check the tiles to the top of it's
		// top bounding box edge, otherwise check the ones to the bottom
		if (koala.velocity.y > 0) {
			startY = endY = (int) (koala.position.y + Koala.HEIGHT + koala.velocity.y);
		} else {
			startY = endY = (int) (koala.position.y + koala.velocity.y);
		}
		startX = (int) (koala.position.x);
		endX = (int) (koala.position.x + Koala.WIDTH);
		getTiles(startX, startY, endX, endY, tiles);
		koalaRect.y += koala.velocity.y;
		for (Rectangle tile : tiles) {
			if (koalaRect.overlaps(tile)) {
				// we actually reset the koala y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if (koala.velocity.y > 0) {
					koala.position.y = tile.y - Koala.HEIGHT;
					// we hit a block jumping upwards, let's destroy it!
					TiledMapTileLayer layer = (TiledMapTileLayer) level
							.getLayers().get(1);
					layer.setCell((int) tile.x, (int) tile.y, null);
				} else {
					koala.position.y = tile.y + tile.height;
					// if we hit the ground, mark us as grounded so we can jump
					koala.grounded = true;
				}
				koala.velocity.y = 0;
				break;
			}
		}
		rectPool.free(koalaRect);

		// unscale the velocity by the inverse delta time and set
		// the latest position
		koala.position.add(koala.velocity);
		koala.velocity.scl(1 / deltaTime);

		// Apply damping to the velocity on the x-axis so we don't
		// walk infinitely once a key was pressed
		koala.velocity.x *= Koala.DAMPING;

	}
	
	private void fireBullet(Vector2 position, boolean facesRight) {
		Vector2 newPos = new Vector2(position.x, position.y + .5f);
		if(facesRight){
			newPos.x += .5f;
			entityList.add(new Bullet(newPos, 2, facesRight, bulletReg, renderer));
		} else {
			newPos.x -= .5f;
			entityList.add(new Bullet(newPos, 2, facesRight, bulletReg, renderer));
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
		switch (koala.state) {
		case Standing:
			frame = stand.getKeyFrame(koala.stateTime);
			break;
		case Walking:
			frame = walk.getKeyFrame(koala.stateTime);
			break;
		case Jumping:
			frame = jump.getKeyFrame(koala.stateTime);
			break;
		}

		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		if (koala.facesRight) {
			batch.draw(frame, koala.position.x, koala.position.y, Koala.WIDTH,
					Koala.HEIGHT);
		} else {
			batch.draw(frame, koala.position.x + Koala.WIDTH, koala.position.y,
					-Koala.WIDTH, Koala.HEIGHT);
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

}
