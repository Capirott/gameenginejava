package engineTester;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		ModelData data4 = OBJFileLoader.loadOBJ("dragon");
		ModelData data5 = OBJFileLoader.loadOBJ("box");
		
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		RawModel fern = loader.loadToVAO(data2.getVertices(), data2.getTextureCoords(), data2.getNormals(), data2.getIndices());
		RawModel grass = loader.loadToVAO(data3.getVertices(), data3.getTextureCoords(), data3.getNormals(), data3.getIndices());
		RawModel bunny = loader.loadToVAO(data4.getVertices(), data4.getTextureCoords(), data4.getNormals(), data4.getIndices()); 
		RawModel box = loader.loadToVAO(data5.getVertices(), data5.getTextureCoords(), data5.getNormals(), data5.getIndices());
		
		TexturedModel playerModel = new TexturedModel(bunny, new ModelTexture(loader.loadTexture("white")));
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel fernModel = new TexturedModel(fern, new ModelTexture(loader.loadTexture("fern")));
		TexturedModel grassModel = new TexturedModel(grass, new ModelTexture(loader.loadTexture("grass2")));
		TexturedModel boxModel = new TexturedModel(box, new ModelTexture(loader.loadTexture("box")));

		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);
		grassModel.getTexture().setHasTransparency(true);
		grassModel.getTexture().setUseFakeLighting(true);
		
		List<Entity> entities = new ArrayList<Entity>();


		Player player = new Player(playerModel,	new Vector3f(500, 0, 470), 0, 0, 0, 1);
		
		Random random = new Random();
		for (int i = 0; i < 5000; i++) {
			entities.add(new Entity(staticModel,
					new Vector3f(random.nextFloat() * 1600, 0, random.nextFloat() * 1600), 0, 0, 0, 5 + random.nextFloat() * 15));
			entities.add(new Entity(fernModel,
					new Vector3f(random.nextFloat() * 1600, 0, random.nextFloat() * 1600), 0, 0, 0, 1));
			entities.add(new Entity(grassModel,
					new Vector3f(random.nextFloat() * 1400 + 100, 0, random.nextFloat() * 1400 + 100), 0, 0, 0, 3));
			entities.add(new Entity(boxModel,
					new Vector3f(random.nextFloat() * 3600 + 100, 3, random.nextFloat() * 3600 + 100), 0, 0, 0, 3));
		}

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

		for (int i = 0; i < 10; ++i) {
			for (int j = 0; j < 10; ++j) {
				Terrain terrain = new Terrain(i, j, loader, texturePack, blendMap, "heightMap");
				terrains.add(terrain);
			}
		}
		
		
		
		while (!Display.isCloseRequested()) {
			camera.move();

			for (Terrain terrain : terrains) {
				renderer.processTerrain(terrain);
			}
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}
			player.move();
			renderer.processEntity(player);
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}