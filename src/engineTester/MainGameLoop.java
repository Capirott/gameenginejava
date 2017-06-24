package engineTester;

import org.lwjgl.opengl.Display;

import models.RawModel;
import models.TextureModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader =  new Loader();
		Renderer renderer = new Renderer();
		StaticShader shader = new StaticShader();
		
		float[] vertices = {
				-0.5f, 0.5f, 0,
				-0.5f, -0.5f, 0,
				 0.5f, -0.5f, 0,
				 0.5f, 0.5f, 0
		};
		int[] indices = {
			0, 1, 3,
			3, 1, 2
		};
		
		float[] textureCoors = {
			0, 0,
			0, 1,
			1, 1,
			1, 0
		};
		
		RawModel model = loader.loadToVAO(vertices, textureCoors, indices);
		ModelTexture texture = new ModelTexture(loader.loadTexture("image"));
		TextureModel textureModel = new TextureModel(model, texture);

		
		while (!Display.isCloseRequested()) {
			shader.start();
			renderer.prepare();
			renderer.render(textureModel);
			shader.start();
			DisplayManager.updateDisplay();
		}
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
}
