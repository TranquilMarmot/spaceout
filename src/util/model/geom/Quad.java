package util.model.geom;

import org.lwjgl.opengl.GL11;

public class Quad extends Face{
	
	public Quad(){
		vertices = new float[4][3];
		normals = new float[4][3];
	}

	@Override
	public void draw() {
		GL11.glBegin(GL11.GL_QUADS);
		{
			for (int i = 0; i < vertices.length; i++) {
				float[] normal = normals[i];
				float[] vertex = vertices[i];

				GL11.glNormal3f(normal[0], normal[1], normal[2]);
				GL11.glVertex3f(vertex[0], vertex[1], vertex[2]);
			}
		}
		GL11.glEnd();
	}

}
