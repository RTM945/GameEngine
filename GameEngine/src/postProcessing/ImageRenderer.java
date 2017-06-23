package postProcessing;

import org.lwjgl.opengl.GL11;

public class ImageRenderer {

	private Fbo fbo;

	protected ImageRenderer(int width, int height) {
		this.fbo = new Fbo(width, height, Fbo.NONE);
	}

	protected ImageRenderer() {}

	protected void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	protected int getOutputTexture() {
		return fbo.getColourTexture();
	}

	protected void cleanUp() {
		if (fbo != null) {
			fbo.cleanUp();
		}
	}

}
