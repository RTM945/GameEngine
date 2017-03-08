package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.RawModel;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDispaly();
		Loader loader = new Loader();
		
		//********************TERRAIN TEXTURE STUFF******************
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		//***********************************************************
		
		RawModel treeModel = loader.loadToVAO(OBJFileLoader.loadOBJ("tree"));
		RawModel grassModel = loader.loadToVAO(OBJFileLoader.loadOBJ("grassModel"));
		RawModel fernModel = loader.loadToVAO(OBJFileLoader.loadOBJ("fern"));
		RawModel lowPolyTreeModel = loader.loadToVAO(OBJFileLoader.loadOBJ("lowPolyTree"));
		
		TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel lowPolyTree = new TexturedModel(lowPolyTreeModel, new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel flower = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("flower")));
		
		ModelTexture frenTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		frenTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(fernModel, frenTextureAtlas);
		
		
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);

		Light light = new Light(new Vector3f(0, 20000, 20000), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");
		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random(676452);
		for (int i = 0; i < 400; i++) {
			if(i % 5 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
			}
//			if(i % 3 == 0) {
//				float x = random.nextFloat() * 800;
//				float z = random.nextFloat() * -800;
//				float y = terrain.getHeightOfTerrain(x, z);
//				entities.add(new Entity(flower, new Vector3f(x, y, z), 0, 0, 0, 2f));
//			}
			if(i % 7 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(tree, new Vector3f(x, y, z), 0, 0, 0, 6f));
			}
			if(i % 11 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * - 800;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(lowPolyTree, new Vector3f(x, y ,z), 0, random.nextFloat() * 360, 0, 1f));
			}
		}

		MasterRenderer renderer = new MasterRenderer();

		RawModel personModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel person = new TexturedModel(personModel, new ModelTexture(loader.loadTexture("playerTexture")));
		person.getTexture().setUseFakeLighting(true);
		Player player = new Player(person, new Vector3f(400, 0, -400), 0, 0, 0, 0.6f);
		
		Camera camera = new Camera(player);
		while (!Display.isCloseRequested()) {
			camera.move();
			player.move(terrain);
			renderer.processEntity(player);
			
			renderer.processTerrain(terrain);
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
