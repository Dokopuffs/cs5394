package characters;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public  class Enemy extends Entity {
	
	private int hp;
	private Vector2 playerPosition;
	private final float MAX_VEL = 5f;
	private TextureRegion tex;
	private OrthogonalTiledMapRenderer renderer;
	private float HEIGHT = 8f;
	private float WIDTH = 12f;
	private float leftBorder = 10f;
	private float rightBorder = 40f;
	private float velocity;
	private int enemyType; // 1 = patrol; 2 = charge; 3 = kite; 4 = retreat; 5 = attack;
	private boolean patrolRightBorderReached = false;
	private boolean patrolLeftBorderReached = true;
	private float[] mapEleChange = {18f, 31f, 35f};

	
	public Enemy(Vector2 pos, Vector2 playerPos, int type, int health,TextureRegion enemyReg, OrthogonalTiledMapRenderer renderer) {
		super(pos,  health);
		hp = health;
		tex = enemyReg;
		enemyType = type;
	
	  this.playerPosition = playerPos;
		
		this.position.x = pos.x;
		this.position.y = pos.y;
		this.renderer = renderer;
		velocity =  MAX_VEL;
	}
	
	@Override
	public void Update(float delta) {
		
		
	
		
		//PATROL ENEMY TYPE
	if (enemyType == 1)
	{

		//move right to right patrol border
	
		if (patrolRightBorderReached == false){
				position.x += delta * velocity;
				
				if (position.x >= rightBorder)
				{
					patrolRightBorderReached = true;
					patrolLeftBorderReached = false;
				}	
		}
		
		// move left to left patrol border
		
		if (patrolLeftBorderReached == false)
		{
				position.x -= delta * velocity;
				
				if (position.x <= leftBorder)
				{
					patrolLeftBorderReached = true;
					patrolRightBorderReached = false;
				}
		}
		
		
	 }
	
	//CHARGE ENEMY TYPE
	if (enemyType == 2)
		{
		
			if (playerPosition.x > position.x)
			{
				position.x += delta * velocity;
			}
			else	
				position.x -= delta * velocity;
		
			if (position.x >= mapEleChange[0] ||  position.x >= mapEleChange[1])
			{
				if (position.y <= 3f )
			position.y += delta * velocity * 2;
			}
				if ( position.x < mapEleChange[0])
					if (position.y >= 2f)
						position.y -= delta * velocity * 2;
		
			}
		
		
	
	//KITE ENEMY TYPE
	if (enemyType == 3)
		{
		
		
		}
	//RETREAT ENEMY TYPE
	if (enemyType == 4)
		{
		
		}
	//ATTACK ENEMY TYPE
	if (enemyType == 5)
		{
		
		}
	}
	
	@Override
	public void Render(float delta) {
		// TODO Auto-generated method stub
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		batch.draw(tex, position.x, position.y, WIDTH / 4f, HEIGHT / 4f);
		batch.end();
	}

	EnemyState state;

}
