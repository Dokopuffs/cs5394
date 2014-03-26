package characters;

public abstract class Enemy extends Entity {

	State state;
	
	@Override
	abstract void New();

	@Override
	abstract void Update();

}
