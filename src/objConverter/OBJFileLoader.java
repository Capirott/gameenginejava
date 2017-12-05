package objConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class OBJFileLoader {

	private static final String RES_LOC = "res/";

	public static List<ModelData> loadOBJ(String objFileName) {
		FileReader isr = null;
		File objFile = new File(RES_LOC + objFileName + ".obj");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res; don't use any extention");
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<List<Integer>> indicesList = new ArrayList<List<Integer>>();
		String mtlFile = null;
		List<String> mtlNameList = new ArrayList<String>();
		mtlNameList.add(new String());
		indicesList.add(new ArrayList<Integer>());
		int it = 0;
		try {
			while (true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("mtllib ")) {
					mtlFile = currentLine[1];
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null) {
				String[] currentLine = line.split(" ");
				if (line.startsWith("f ")) {
					String[] vertex1 = currentLine[1].split("/");
					String[] vertex2 = currentLine[2].split("/");
					String[] vertex3 = currentLine[3].split("/");
					processVertex(vertex1, vertices, indicesList.get(it));
					processVertex(vertex2, vertices, indicesList.get(it));
					processVertex(vertex3, vertices, indicesList.get(it));
					line = reader.readLine();
				} else if (line.startsWith("usemtl ")) {
					indicesList.add(new ArrayList<Integer>());
					mtlNameList.add(currentLine[1]);
					it++;
					line = reader.readLine();
				} 
				else if (line.startsWith("g ")) {
					line = reader.readLine();
					continue;
				} else {
					break;
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
		List<ModelData> modelDatas = new ArrayList<ModelData>();
		HashMap<String, String> mtlMap = loadMTL(mtlFile);
		for (int i = 0; i < indicesList.size(); ++i) {
			int[] indicesArray = convertIndicesListToArray(indicesList.get(i));
			String mtlName = mtlNameList.get(i);
			String mtlDir = mtlName == null ? null : mtlMap.get(mtlName);
			ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, furthest, mtlDir);
			modelDatas.add(data);
		}
		return modelDatas;
	}

	private static HashMap<String, String> loadMTL(String mtlFile) {
		HashMap<String, String> mtlMap = new HashMap<String, String>();
		if (mtlFile != null) {
			FileReader isr = null;
			File objFile = new File("res/" + mtlFile);
			try {
				isr = new FileReader(objFile);
			} catch (FileNotFoundException e) {
				System.err.println("File not found in res; don't use any extention MTL");
			}
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			String currentMtl = null;
			try {
				line = reader.readLine();
				while (line != null) {
					String[] currentLine = line.split(" ");
					if (line.startsWith("newmtl ")) {
						currentMtl = currentLine[1];
					} else if (line.startsWith("map_Kd ")) {
						String name = currentLine[1];
						mtlMap.put(currentMtl, name.substring(0, name.lastIndexOf(".")));
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				System.err.println("Error reading the file");
			}
		}
		return mtlMap;
	}
	
	
	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
			float[] verticesArray, float[] texturesArray, float[] normalsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex,
			List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}

	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

}