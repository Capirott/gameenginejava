package engineTester;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	private static ArrayList<Terrain> terrains = new ArrayList<Terrain>();

	public static void main(String[] args) throws IOException {

		
		Obj obj =  ObjReader.read(new FileInputStream("res/untitled.obj"));
	        
    obj = ObjUtils.convertToRenderable(obj);
        
        // Obtain the data from the OBJ, as direct buffers:
        int[] indices = ObjData.getFaceVertexIndicesArray(obj, 3);
        float[] vertices = ObjData.getVerticesArray(obj);
        float[] texCoords = ObjData.getTexCoordsArray(obj, 2);
        float[] normals = ObjData.getNormalsArray(obj);


		DisplayManager.createDisplay();
		Loader loader = new Loader();

		List<ModelData> data = OBJFileLoader.loadOBJ("tree");
		ModelData modelData = data.get(0);
		List<ModelData> data2 = OBJFileLoader.loadOBJ("fern");
		ModelData modelData2 = data2.get(0);
		List<ModelData> data3 = OBJFileLoader.loadOBJ("grass");
		ModelData modelData3 = data3.get(0);
		List<ModelData> data4 = OBJFileLoader.loadOBJ("joint_i");
		ModelData modelData4 = data4.get(0);
		List<ModelData> data5 = OBJFileLoader.loadOBJ("joint");		
		ModelData modelData5 = data5.get(0);
		List<ModelData> data6 = OBJFileLoader.loadOBJ("untitled");		
		ModelData modelData6 = data6.get(0);
		
		
		RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getTextureCoords(), modelData.getNormals(), modelData.getIndices());
		RawModel fern = loader.loadToVAO(modelData2.getVertices(), modelData2.getTextureCoords(), modelData2.getNormals(), modelData2.getIndices());
		RawModel grass = loader.loadToVAO(modelData3.getVertices(), modelData3.getTextureCoords(), modelData3.getNormals(), modelData3.getIndices());
		RawModel joint_i = loader.loadToVAO(modelData4.getVertices(), modelData4.getTextureCoords(), modelData4.getNormals(), modelData4.getIndices()); 
		RawModel joint = loader.loadToVAO(modelData5.getVertices(), modelData5.getTextureCoords(), modelData5.getNormals(), modelData5.getIndices());
		
		TexturedModel joint_iModel = new TexturedModel(joint_i, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel jointModel = new TexturedModel(joint, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel staticModel = new TexturedModel(rawModel, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel fernModel = new TexturedModel(fern, new ModelTexture(loader.loadTexture("fern")));
		TexturedModel grassModel = new TexturedModel(grass, new ModelTexture(loader.loadTexture("grass2")));

		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);
		grassModel.getTexture().setHasTransparency(true);
		grassModel.getTexture().setUseFakeLighting(true);
		
		List<Entity> entities = new ArrayList<Entity>();


		Player player = new Player(jointModel, joint_iModel, new Vector3f(500, 0, 500), 0, 0, 0, new Vector3f(1.0f, 1.0f, 1.0f));


		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(0.99f, 0.83f, 0.25f));

		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
	
		for (ModelData model : data6) {
			RawModel house = loader.loadToVAO(vertices, texCoords, normals, indices);
			String mtlName = model.getMtlName();
			if (true) {
				mtlName = "path";
			}
			TexturedModel houseModel = new TexturedModel(house, new ModelTexture(loader.loadTexture(mtlName)));
			houseModel.getTexture().setUseFakeLighting(true);
			entities.add(new Entity(houseModel, new Vector3f(500, 0, 500), 0, 0, 0, new Vector3f(0.3f, 0.3f, 0.3f)));
		}
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		Camera camera = new Camera(player);
		camera.setPosition(new Vector3f(0, 30, 0));
		MasterRenderer renderer = new MasterRenderer();

		for (int i = 0; i < 1; ++i) {
			for (int j = 0; j < 1; ++j) {
				Terrain terrain = new Terrain(i, j, loader, texturePack, blendMap, "heightMap");
				terrains.add(terrain);
			}
		}		
		
		
		for (int i = 0; i < 100; i++) {
			entities.add(generateEntity(staticModel, 5, 25, 0));
			entities.add(generateEntity(fernModel, 0.5f, 1.5f, 0));
		}
		
		
		while (!Display.isCloseRequested()) {
			processInput();
			camera.move();

			for (Terrain terrain : terrains) {
				renderer.processTerrain(terrain);
			}
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}
			player.move(terrains.get(0));
			renderer.processEntity(player);
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}
	
	public static Entity generateEntity(TexturedModel model, float minSize, float maxSize, float heightOffSet) {
		Random random = new Random();
		float x = random.nextFloat() * 800;
		float z = random.nextFloat() * 600;
		float y = terrains.get(0).getHeightOfTerrain(x, z) + heightOffSet;
		float scale = minSize + maxSize * random.nextFloat();
		return new Entity(model, new Vector3f(x, y, z), 0, 0, 0, new Vector3f(scale, scale, scale));
	}
	
	private static void processInput() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			System.exit(0);
		}
	}

}