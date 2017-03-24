package normalMappingObjConverter;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class VertexNM {
	private static final int NO_INDEX = -1;

	private Vector3f position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private VertexNM duplicateVertex = null;
	private int index;
	private float length;
	private List<Vector3f> tangents = new ArrayList<Vector3f>();
	private Vector3f averagedTangent = new Vector3f(0, 0, 0);

	public VertexNM(int index, Vector3f position) {
		this.index = index;
		this.position = position;
		this.length = position.length();
	}

	public void addTangent(Vector3f tangent) {
		tangents.add(tangent);
	}

	public void averageTangents() {
		if (tangents.isEmpty()) {
			return;
		}
		for (Vector3f tangent : tangents) {
			Vector3f.add(averagedTangent, tangent, averagedTangent);
		}
		averagedTangent.normalise();
	}

	public Vector3f getAverageTangent() {
		return averagedTangent;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return length;
	}

	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public VertexNM getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(VertexNM duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
