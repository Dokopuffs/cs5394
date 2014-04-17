package characters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends Entity {

	private int damage;
	private final float MAX_VEL = 25f;
	private float velocity;
	private TextureRegion tex;
	private float HEIGHT = 8f;
	private float WIDTH = 12f;
	
	public Bullet(Vector2 pos, int health, boolean facesRight, TextureRegion bulletReg, OrthogonalTiledMapRenderer renderer) {
		super(pos, health, renderer);
		damage = health;
		velocity = facesRight ? MAX_VEL : -MAX_VEL;
		tex = bulletReg;
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
		batch.draw(tex, position.x, position.y, WIDTH / 16f, HEIGHT / 16f);
		batch.end();
	}

}
