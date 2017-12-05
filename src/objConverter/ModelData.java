package objConverter;
 
public class ModelData {
 
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private int[] indices;
    private float furthestPoint;
    String mtlName;
    public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
            float furthestPoint, String mtlName) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
        this.mtlName = mtlName;
    }
 
    public float[] getVertices() {
        return vertices;
    }
 
    public float[] getTextureCoords() {
        return textureCoords;
    }
 
    public float[] getNormals() {
        return normals;
    }
 
    public int[] getIndices() {
        return indices;
    }
 
    public float getFurthestPoint() {
        return furthestPoint;
    }

	public String getMtlName() {
		return mtlName;
	}

	public void setMtlName(String mtlName) {
		this.mtlName = mtlName;
	}
    
}