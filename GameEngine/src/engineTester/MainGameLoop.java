package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;
import textures.ModelTexture;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDispaly();
		Loader loader = new Loader();
		TextMaster.init(loader);

        RawModel bunnyModel = OBJFileLoader.loadOBJ("person", loader);
        TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));
 
        Player player = new Player(stanfordBunny, new Vector3f(300, 5, -400), 0, 90, 0, 0.6f);
        Camera camera = new Camera(player);
        MasterRenderer renderer = new MasterRenderer(loader, camera);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        
        
		FontType font = new FontType(loader.loadTexture("candara"), new File("res/candara.fnt"));
		GUIText text = new GUIText("Burning Nao!", 3, font, new Vector2f(0, 0.8f), 1f, true);
		text.setColour(1f, 0.0f, 0.0f);
		// *********TERRAIN TEXTURE STUFF**********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		  
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
        List<Terrain> terrains = new ArrayList<Terrain>();
        terrains.add(terrain);

		// *****************************************

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        
        TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader), fernTextureAtlas);
        fern.getTexture().setHasTransparency(true);
        
        TexturedModel pineModel = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader), new ModelTexture(loader.loadTexture("pine")));
        pineModel.getTexture().setHasTransparency(true);
 
        TexturedModel lamp = new TexturedModel(OBJFileLoader.loadOBJ("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUseFakeLighting(true);
 
        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> normalMapEntities = new ArrayList<Entity>();
        
        //******************NORMAL MAP MODELS************************
        
        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader), new ModelTexture(loader.loadTexture("crate")));
        crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), new ModelTexture(loader.loadTexture("boulder")));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.5f);
         
        //************ENTITIES*******************
        
        Entity entity = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
        Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
        Entity entity3 = new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.04f);
        normalMapEntities.add(entity);
        normalMapEntities.add(entity2);
        normalMapEntities.add(entity3);
		
		Random random = new Random(5666778);
		for (int i = 0; i < 320; i++) {
			if (i % 3 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;

				float y = terrain.getHeightOfTerrain(x, z);
				if (y > 0) {
					entities.add(new Entity(fern, 3, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
				}
			}

			if (i % 1 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				if ((x > 50 && x < 100) || (z < -50 && z > -100)) {

				} else {
					float y = terrain.getHeightOfTerrain(x, z);
					if (y > 0) {
						entities.add(new Entity(pineModel, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
								random.nextFloat() * 0.6f + 0.8f));
					}
				}
			}
		}
		for (int i = 0; i < 30; i++) {
			float x = 400 + random.nextFloat() * 200;
			float z = -400 + random.nextFloat() * 200;

			float y = terrain.getHeightOfTerrain(x, z);
			normalMapEntities.add(new Entity(boulderModel, new Vector3f(x, y, z), random.nextFloat() * 360, 0, 0,
					0.5f + random.nextFloat()));
		}
        
        //*******************OTHER SETUP***************
        
        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);

        entities.add(player);
        List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
        
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        //**********Water Renderer Set-up************************
        WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		for (int i = 1; i < 5; i++) {
			for (int j = 1; j < 5; j++) {
				waters.add(new WaterTile(i * 160, -j * 160, -1));
			}
		}
        
        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
        
        ParticleSystem particleSystem = new ParticleSystem(particleTexture, 40, 10, 0.1f, 1, 0.6f);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(0, 1, 0), 0.5f);
        particleSystem.setLifeError(0.2f);
        particleSystem.setSpeedError(0.5f);
        particleSystem.setScaleError(1.5f);
        
        ParticleSystem particleSystem1 = new ParticleSystem(particleTexture, 40, 10, 0.1f, 1, 1.6f);
        particleSystem1.randomizeRotation();
        particleSystem1.setDirection(new Vector3f(0, 1, 0), 0.1f);
        particleSystem1.setLifeError(0.2f);
        particleSystem1.setSpeedError(0.25f);
        particleSystem1.setScaleError(0.5f);
        
        Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
        PostProcessing.init(loader);
        
        //****************Game Loop Below*********************
        player.setCamera(camera);
        while (!Display.isCloseRequested()) {
        	picker.update();
            player.move(terrain);
            camera.move();
//            camera.rotate(-0.1f);
            ParticleMaster.update(camera);
            
            renderer.renderShadowMap(entities, sun);
            Vector3f particlePosition = new Vector3f(player.getPosition());
            Vector3f particlePosition1 = new Vector3f(player.getPosition());
            particlePosition.y += 3f;
            particlePosition.x += 0.6f;
            particlePosition.z += 0.6f;
            
            particlePosition1.y += 3f;
            particlePosition1.x -= 0.6f;
            particlePosition1.z -= 0.6f;
            particleSystem.generateParticles(particlePosition);
            particleSystem.generateParticles(particlePosition1);
            
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
             
            buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera,
					new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();

			// render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera,
					new Vector4f(0, -1, 0, waters.get(0).getHeight() + 0.2f));
             
            //render to screen
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer(); 
            
            fbo.bindFrameBuffer();
            
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));    
            waterRenderer.render(waters, camera, sun);
            ParticleMaster.renderParticles(camera);
            fbo.unbindFrameBuffer();
            PostProcessing.doPostProcessing(fbo.getColourTexture());
            
            guiRenderer.render(guiTextures);
            TextMaster.render();
             
            DisplayManager.updateDisplay();
        }

        PostProcessing.cleanUp();
        fbo.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
