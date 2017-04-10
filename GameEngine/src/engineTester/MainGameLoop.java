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
        MasterRenderer renderer = new MasterRenderer(loader);
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

		// *****************************************

		TexturedModel rocks = new TexturedModel(OBJFileLoader.loadOBJ("rocks", loader), new ModelTexture(loader.loadTexture("rocks")));
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        
        TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader), fernTextureAtlas);
        fern.getTexture().setHasTransparency(true);
        
        TexturedModel bobble = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader), new ModelTexture(loader.loadTexture("pine")));
        bobble.getTexture().setHasTransparency(true);
        
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
        List<Terrain> terrains = new ArrayList<Terrain>();
        terrains.add(terrain);
 
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
		for (int i = 0; i < 60; i++) {
            if (i % 3 == 0) {
                float x = random.nextFloat() * 150;
                float z = random.nextFloat() * -150;
                if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
                } else {
                    float y = terrain.getHeightOfTerrain(x, z);
                    entities.add(new Entity(fern, 3, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
                }
            }
            if (i % 2 == 0) {
                float x = random.nextFloat() * 150;
                float z = random.nextFloat() * -150;
                if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
                } else {
                    float y = terrain.getHeightOfTerrain(x, z);
                    entities.add(new Entity(bobble, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.6f + 0.8f));
                }
            }
        }
        entities.add(new Entity(rocks, new Vector3f(75, 4.6f, -75), 0, 0, 0, 75));
        
        //*******************OTHER SETUP***************
        
        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);

        RawModel bunnyModel = OBJFileLoader.loadOBJ("nao", loader);
        TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("naoTexture")));
 
        Player player = new Player(stanfordBunny, new Vector3f(20, 5, -20), 0, 100, 0, 0.03f);
        entities.add(player);
        Camera camera = new Camera(player, terrain);
        List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        //**********Water Renderer Set-up************************
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<WaterTile>();
        WaterTile water = new WaterTile(75, -75, 0);
        waters.add(water);
        
        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
        
        ParticleSystem particleSystem = new ParticleSystem(particleTexture, 40, 10, 0.1f, 1, 1.6f);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.25f);
        particleSystem.setScaleError(0.5f);
        
        
        //****************Game Loop Below*********************
        
        while (!Display.isCloseRequested()) {
            player.move(terrain);
            camera.move();
            picker.update();
            ParticleMaster.update(camera);
            
            particleSystem.generateParticles(player.getPosition());
            
            entity.increaseRotation(0, 1, 0);
            entity2.increaseRotation(0, 1, 0);
            entity3.increaseRotation(0, 1, 0);
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
             
            //render reflection teture
            buffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()+1));
            camera.getPosition().y += distance;
            camera.invertPitch();
             
            //render refraction texture
            buffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
             
            //render to screen
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer(); 
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));    
            waterRenderer.render(waters, camera, sun);
            
            ParticleMaster.renderParticles(camera);
            
            guiRenderer.render(guiTextures);
            TextMaster.render();
             
            DisplayManager.updateDisplay();
        }

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
