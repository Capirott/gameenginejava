package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entity.Camera;
import entity.Entity;
import entity.Light;
import models.RawModel;
import models.TextureModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader =  new Loader();

		RawModel model = OBJLoader.loadObjModel("dragon", loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("white"));
		TextureModel staticModel = new TextureModel(model, texture);
		texture.setShineDamper(10000);
		texture.setReflectivity(1);
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-25), 0.0f, 0, 0, 1.0f);
		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
		
		Terrain terrain = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("grass")));
		
		Camera camera = new Camera();
		
		MasterRenderer renderer = new MasterRenderer();
		
		
		while (!Display.isCloseRequested()) {
			camera.move();
			entity.increaseRotation(0.0f, 1.0f, 0.0f);
			renderer.processTerrain(terrain);
			renderer.processEntity(entity);
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
}
