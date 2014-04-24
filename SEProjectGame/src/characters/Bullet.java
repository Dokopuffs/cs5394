package characters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends Entity {

	public int damage;
	private final float MAX_VEL = 25f;
	private float velocity;
	private TextureRegion tex;
	public Class<?> owner;
	
	public Bullet(Vector2 pos, int health, boolean facesRight, TextureRegion bulletReg, OrthogonalTiledMapRenderer renderer, TiledMap level, Class<?> owner) {
		super(pos, health, renderer, level);
		damage = health;
		velocity = facesRight ? MAX_VEL : -MAX_VEL;
		tex = bulletReg;
		this.owner = owner;
		this.height = 8f / 16f;
		this.width = 12f / 16f;
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
		batch.draw(tex, position.x, position.y, width , height);
		batch.end();
	}

}
