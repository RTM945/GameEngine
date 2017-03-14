package renderEngine;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;
import objConverter.ModelData;
import textures.dds.DDSReader;

public class Loader {

	private List<Integer> vaos = new ArrayList<>();
	private List<Integer> vbos = new ArrayList<>();
	private List<Integer> textures = new ArrayList<>();

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		binIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(ModelData modelData) {
		int vaoID = createVAO();
		binIndicesBuffer(modelData.getIndices());
		storeDataInAttributeList(0, 3, modelData.getVertices());
		storeDataInAttributeList(1, 2, modelData.getTextureCoords());
		storeDataInAttributeList(2, 3, modelData.getNormals());
		unbindVAO();
		return new RawModel(vaoID, modelData.getIndices().length);
	}
	
	public RawModel loadToVAO(float[] position) {
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, 2, position);
		unbindVAO();
		return new RawModel(vaoID, position.length / 2);
	}

	public int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	public int loadDDSTexture(String fileName) {
		
		int texture = 0;

        try {
            FileInputStream fis = new FileInputStream("res/" + fileName + ".dds");
            byte [] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            int [] pixels = DDSReader.read(buffer, DDSReader.ABGR, 0);
            int width = DDSReader.getWidth(buffer);
            int height = DDSReader.getHeight(buffer);
            int mipmap = DDSReader.getMipmap(buffer);

            int [] textures = new int[1];
//            gl.glGenTextures(1, textures, 0);
            GL11.glGenTextures();

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[0]);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);

            if(mipmap > 0) {
                // mipmaps
                for(int i=0; (width > 0) || (height > 0); i++) {
                    if(width <= 0) width = 1;
                    if(height <= 0) height = 1;
                    pixels = DDSReader.read(buffer, DDSReader.ABGR, i);

                    IntBuffer texBuffer = IntBuffer.wrap(pixels);
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texBuffer);

                    width /= 2;
                    height /= 2;
                }

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
            }
            else {
                // no mipmaps
                pixels = DDSReader.read(buffer, DDSReader.ABGR, 0);

                IntBuffer texBuffer = IntBuffer.wrap(pixels);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texBuffer);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

            }

            texture = textures[0];
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return texture;
	}

	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	private void binIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		;
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}

		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}

		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}
}
