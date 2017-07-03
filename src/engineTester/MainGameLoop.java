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
import renderEngine.Renderer;
import shaders.StaticShader;
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
		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 0, 1));
		Camera camera = new Camera();
		
		MasterRenderer renderer = new MasterRenderer();
		
		
		while (!Display.isCloseRequested()) {
			camera.move();
			entity.increaseRotation(0.0f, 1.0f, 0.0f);
			renderer.processEntity(entity);
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		renderer.clenUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
}
