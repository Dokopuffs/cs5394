package characters;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class PlayerCharacter extends Entity {


	public PlayerState state;

	public PlayerCharacter(Vector2 pos, int health, OrthogonalTiledMapRenderer rend, TiledMap level, float width, float height) {
		super(pos, health, rend, level);
		state = PlayerState.Standing;
		this.height = height;
		this.width = width;
	}
	
	public enum State {
		Standing, Walking, Jumping
	}

	/** The player character, has state and state time, */
	public static class Player {
		static float WIDTH;
		static float HEIGHT;
		static float MAX_VELOCITY = 10f;
		static float JUMP_VELOCITY = 40f;
		static float DAMPING = 0.87f;

		final Vector2 position = new Vector2();
		final Vector2 velocity = new Vector2();
		State state = State.Walking;
		float stateTime = 0;
		boolean facesRight = true;
		boolean grounded = false;
	}

	@Override
	public void Update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Render(float delta) {
		// TODO Auto-generated method stub
		
	}
}