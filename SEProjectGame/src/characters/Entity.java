package characters;

import items.Weapon;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class Entity {
	public Vector2 position;
	public float width, height;
	int health;
	public boolean facesRight;
	public boolean grounded;
	Weapon weapon;
	public Vector2 velocity;
	protected OrthogonalTiledMapRenderer renderer;
	protected TiledMap level;
	public float stateTime;
	public float lastBulletFired;
	
	Entity(Vector2 pos, int health, OrthogonalTiledMapRenderer renderer, TiledMap level){
		this.position = new Vector2(pos.x, pos.y);
		this.health = health;
		velocity = new Vector2(0,0);
		this.renderer = renderer;
		this.level = level;
	}
	
	public abstract void Update(float delta);

	public abstract void Render(float delta);
}
