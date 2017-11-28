package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

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

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		ModelData data = OBJFileLoader.loadOBJ("tree");
		ModelData data2 = OBJFileLoader.loadOBJ("fern");
		ModelData data3 = OBJFileLoader.loadOBJ("grass");
		ModelData data4 = OBJFileLoader.loadOBJ("joint_i");
		ModelData data5 = OBJFileLoader.loadOBJ("joint");
		
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		RawModel fern = loader.loadToVAO(data2.getVertices(), data2.getTextureCoords(), data2.getNormals(), data2.getIndices());
		RawModel grass = loader.loadToVAO(data3.getVertices(), data3.getTextureCoords(), data3.getNormals(), data3.getIndices());
		RawModel joint_i = loader.loadToVAO(data4.getVertices(), data4.getTextureCoords(), data4.getNormals(), data4.getIndices()); 
		RawModel joint = loader.loadToVAO(data5.getVertices(), data5.getTextureCoords(), data5.getNormals(), data5.getIndices());
		
		TexturedModel joint_iModel = new TexturedModel(joint_i, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel jointModel = new TexturedModel(joint, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel fernModel = new TexturedModel(fern, new ModelTexture(loader.loadTexture("fern")));
		TexturedModel grassModel = new TexturedModel(grass, new ModelTexture(loader.loadTexture("grass2")));

		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);
		grassModel.getTexture().setHasTransparency(true);
		grassModel.getTexture().setUseFakeLighting(true);
		
		List<Entity> entities = new ArrayList<Entity>();


		Player player = new Player(jointModel, joint_iModel, new Vector3f(500, 0, 470), 0, 0, 0, new Vector3f(1.0f, 1.0f, 1.0f));


		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(0.99f, 0.83f, 0.25f));

		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		Camera camera = new Camera(player);
		camera.setPosition(new Vector3f(500, 30, 600));
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