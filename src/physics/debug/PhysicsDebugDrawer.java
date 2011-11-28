package physics.debug;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import util.debug.Debug;

import com.bulletphysics.linearmath.IDebugDraw;

/**
 * Draws debug info for JBullet. See the <a href="http://jbullet.advel.cz/javadoc/com/bulletphysics/linearmath/IDebugDraw.html">JBullet documentation</a> for more info.
 * @author TranquilMarmot
 *
 */
public class PhysicsDebugDrawer extends IDebugDraw {

	public PhysicsDebugDrawer() {
		super();
	}

	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glBegin(GL11.GL_LINE); {
			GL11.glVertex3f(from.x, from.y, from.z);
			GL11.glVertex3f(to.x, to.y, to.z);
		}
		GL11.glEnd();

	}

	@Override
	public void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB,
			float distance, int lifeTime, Vector3f color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportErrorWarning(String warningString) {
		Debug.console.print(warningString);
		System.out.println(warningString);
	}

	@Override
	public void draw3dText(Vector3f location, String textString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDebugMode(int debugMode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDebugMode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
