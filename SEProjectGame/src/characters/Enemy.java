package characters;

public abstract class Enemy extends Entity {

	EnemyState state;
	
	@Override
	abstract void New();

	@Override
	abstract void Update();

}
