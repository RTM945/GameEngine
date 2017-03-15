package textures.dds;

import java.io.FileInputStream;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class DDSLoader {

	public int loadDDS(String path) {
		try (FileInputStream fin = new FileInputStream(path)) {
			byte[] buffer = new byte[fin.available()];
			fin.read(buffer);

			int[] pixels = DDSReader.read(buffer, DDSReader.ARGB, 0);
			int width = DDSReader.getWidth(buffer);
			int height = DDSReader.getHeight(buffer);
			int mipmap = DDSReader.getMipmap(buffer);

			IntBuffer glName = BufferUtils.createIntBuffer(1);
			GL11.glGenTextures(glName);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glName.get(0));

			if(mipmap > 0) {
                // mipmaps
                for(int i=0; (width > 0) || (height > 0); i++) {
                    if(width <= 0) width = 1;
                    if(height <= 0) height = 1;
                    pixels = DDSReader.read(buffer, DDSReader.ABGR, i);

                    IntBuffer texBuffer = BufferUtils.createIntBuffer(pixels.length);
                    texBuffer.put(pixels);
                    texBuffer.flip();
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texBuffer);
                    width /= 2;
                    height /= 2;
                }

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
            } else {
                // no mipmaps
                pixels = DDSReader.read(buffer, DDSReader.ABGR, 0);

                IntBuffer texBuffer = BufferUtils.createIntBuffer(pixels.length);
                texBuffer.put(pixels);
                texBuffer.flip();
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texBuffer);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

            }
			return glName.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return 0;
	}

}
