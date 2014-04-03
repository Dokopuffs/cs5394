package characters;

import com.badlogic.gdx.math.Vector2;

public abstract class Enemy extends Entity {

	Enemy(Vector2 pos, int health) {
		super(pos, health);
		// TODO Auto-generated constructor stub
	}

	EnemyState state;

}
