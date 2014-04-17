package items;

import com.badlogic.gdx.math.Vector2;

public class QueuedBullet {
	public Vector2 position;
	public boolean facesRight;
	
	public QueuedBullet(Vector2 pos, boolean face){
		position = pos;
		facesRight = face;
	}
}
