package characters;

import screen.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Entity {
	
	private Vector2 playerPosition;
	private final float MAX_VEL = 5f;
	private TextureRegion tex;
	private float leftBorder = 10f;
	private float rightBorder = 40f;
	private float hp;
	private float velocity;
	private int enemyType; // 1 = patrol; 2 = charge; 3 = kite; 4 = retreat; 5 =
							// attack;
	
	private boolean kiteRightBorderReached = false;
	private boolean kiteLeftBorderReached =true;
	private float rightBorderKite = 40f;
	private float leftBorderKite = 40f;
	private boolean patrolRightBorderReached = false;
	private boolean patrolLeftBorderReached = true;
	private float[] mapEleChange = { 18f, 31f, 35f };
	private GameScreen bulletListener;

	public Enemy(Vector2 pos, Vector2 playerPos, int type, int health,
			TextureRegion enemyReg, OrthogonalTiledMapRenderer renderer,
			TiledMap level, GameScreen screen) {
		super(pos, health, renderer, level);
		hp = health;
		tex = enemyReg;
		enemyType = type;
		this.bulletListener = screen;

		this.playerPosition = playerPos;

		this.position.x = pos.x;
		this.position.y = pos.y;
		velocity = MAX_VEL;
		this.height = enemyReg.getRegionHeight() / 12f;
		this.width = enemyReg.getRegionHeight() / 12f;
	}

	@Override
	public void Update(float delta) {
		stateTime += delta;
		// PATROL ENEMY TYPE
		if (enemyType == 1) {

			// move right to right patrol border

			if (patrolRightBorderReached == false) {
				position.x += delta * velocity;

				if (position.x >= rightBorder) {
					patrolRightBorderReached = true;
					patrolLeftBorderReached = false;
				}
			}

			// move left to left patrol border

			if (patrolLeftBorderReached == false) {
				position.x -= delta * velocity;

				if (position.x <= leftBorder) {
					patrolLeftBorderReached = true;
					patrolRightBorderReached = false;
				}
			}

		}

		// CHARGE ENEMY TYPE
		if (enemyType == 2) {

			
			
			//Chase player, if hp is low retreat
			if (playerPosition.x > position.x && health > 2) {
				position.x += delta * velocity;
			} else
				position.x -= delta * velocity;

			if (position.x >= mapEleChange[0] || position.x >= mapEleChange[1]) {
				if (position.y <= 3f)
					position.y += delta * velocity * 2;
			}
			if (position.x < mapEleChange[0])
				if (position.y >= 2f)
					position.y -= delta * velocity * 2;
			
			
			//If close enough, attack
			if(stateTime - lastBulletFired > 0.75f  && Math.abs(playerPosition.x - position.x ) < 4){
				FireBullet();
				lastBulletFired = stateTime;
			}
			
			
		
	}
	
		

		// KITE ENEMY TYPE
		if (enemyType == 3) {
			if(position.x-playerPosition.x<=4f && position.x-playerPosition.x>=0)
			{
				position.x += delta * velocity*1.97;
				if(position.x-playerPosition.x>=2f)
				{

					if(stateTime - lastBulletFired > 1f)
					{
						FireBullet();
						lastBulletFired = stateTime;
					}
				}

				if (position.x-playerPosition.x >= rightBorderKite) 
				{
					kiteRightBorderReached = true;
					kiteLeftBorderReached = false;
				}
			}
		}
		// RETREAT ENEMY TYPE
		if (enemyType == 4) {
			if(hp<=.25*health)
			{
				if(position.x-playerPosition.x<=4f && position.x-playerPosition.x>=0)
				{
					position.x+=delta*velocity*2.5;
				}
				}
		}
		// ATTACK ENEMY TYPE
		if (enemyType == 5) {
			
			
			
			if(stateTime - lastBulletFired > 0.75f){
				FireBullet();
				lastBulletFired = stateTime;
			}
		}
	}
	


	@Override
	public void Render(float delta) {
		// TODO Auto-generated method stub
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		batch.draw(tex, position.x, position.y, width , height);
		batch.end();
	}



	private void FireBullet() {
		float diff = position.x - playerPosition.x;
		if(diff > 0.0f){
			bulletListener.addBullet(new Vector2(position), false, Enemy.class);
		} else {
			bulletListener.addBullet(new Vector2(position), true, Enemy.class);
		}
	}

}
