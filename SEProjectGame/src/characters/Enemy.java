package characters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public  class Enemy extends Entity {
	
	private int hp;
	private final float MAX_VEL = 2f;
	private TextureRegion tex;
	private OrthogonalTiledMapRenderer renderer;
	private float HEIGHT = 8f;
	private float WIDTH = 12f;
	private float velocity;

	
	public Enemy(Vector2 pos, int health,TextureRegion enemyReg, OrthogonalTiledMapRenderer renderer) {
		super(pos, health);
		hp = health;
		tex = enemyReg;
		this.position.x = pos.x;
		this.position.y = pos.y;
		this.renderer = renderer;
		velocity =  MAX_VEL;
	}
	
	@Override
	public void Update(float delta) {
		
		position.x += delta * velocity;
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
