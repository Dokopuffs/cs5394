package characters;

import items.Weapon;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	Vector2 position;
	int health;
	Vector2 looking;
	Weapon weapon;
	
	abstract void New();
	
	abstract void Update();
}
