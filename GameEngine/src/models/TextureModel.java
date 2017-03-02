package models;

import renderEngine.RawModel;
import textures.ModelTexture;

public class TextureModel {
	
	private RawModel rawModel;
	private ModelTexture texture;
	
	public TextureModel(RawModel rawModel, ModelTexture texture) {
		this.rawModel = rawModel;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public void setRawModel(RawModel rawModel) {
		this.rawModel = rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public void setTexture(ModelTexture texture) {
		this.texture = texture;
	}

}
