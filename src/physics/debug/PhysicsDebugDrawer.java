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
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1.0f);
		
		GL11.glColor3f(1.0f, 0.0f, 1.0f);
		GL11.glPushMatrix();{
			//GL11.glTranslatef(from.x, from.y, from.z);
			GL11.glBegin(GL11.GL_LINES); {
				GL11.glVertex3f(0.0f, 0.0f, 0.0f);
				GL11.glVertex3f((to.x - from.x) * 30, (to.y - from.y) * 30, (to.z - from.z) * 30);
			}
			GL11.glEnd();
		}
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_LIGHTING);
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
		System.out.println(textString);
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
