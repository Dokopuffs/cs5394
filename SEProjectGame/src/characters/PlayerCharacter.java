package characters;
import com.badlogic.gdx.math.Vector2;

public abstract class PlayerCharacter extends Entity {


	PlayerCharacter(Vector2 pos, int health) {
		super(pos, health);
		// TODO Auto-generated constructor stub
	}

	/** The player character, has state and state time, */
	public static class Player {
		static float WIDTH;
		static float HEIGHT;
		static float MAX_VELOCITY = 10f;
		static float JUMP_VELOCITY = 40f;
		static float DAMPING = 0.87f;

		public enum State {
			Standing, Walking, Jumping
		}

		final Vector2 position = new Vector2();
		final Vector2 velocity = new Vector2();
		State state = State.Walking;
		float stateTime = 0;
		boolean facesRight = true;
		boolean grounded = false;
	}
}