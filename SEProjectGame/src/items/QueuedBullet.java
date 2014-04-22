package items;

import com.badlogic.gdx.math.Vector2;

public class QueuedBullet {
	public Vector2 position;
	public boolean facesRight;
	public Class<?> owner;
	
	public QueuedBullet(Vector2 pos, boolean face, Class<?> owner){
		position = pos;
		facesRight = face;
		this.owner = owner;
	}
}
