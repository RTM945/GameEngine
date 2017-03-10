package guis;

import org.lwjgl.util.vector.Vector2f;

public class GuiTexture {
	
	private int texture;
	private Vector2f position;
	private Vector2f scala;
	
	public GuiTexture(int texture, Vector2f position, Vector2f scala) {
		this.texture = texture;
		this.position = position;
		this.scala = scala;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScala() {
		return scala;
	}
}
