package com.test.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.test.game.objects.Block;
import com.test.game.objects.Player;

public class GameRenderer {
	
	class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0, 
										btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
			System.out.println("COLLISION");
			return true;
		}
	}
	
	private Map map;
	private Player player;
	private ModelBuilder builder;
	private ModelCache cache;
	private ModelBatch batch;
	private PerspectiveCamera cam;
	private CameraInputController camController;
	private Environment environment;
	private List<Block> blocks;
	
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private MyContactListener contactListener;
	
	private float lerpSpeed;
	private final float CAMERA_DISTANCE = 10f;
	
	public GameRenderer(Map map) {
		Bullet.init();
		this.map = map;
		initRenderingHelpers();
		initGameObjects();
        initEnvironment();
        initCamera();
        initCollisionHelpers();
        initMemberVariables();
//        initDebuggingTools();
	}
	
	public void initRenderingHelpers(){
		builder = new ModelBuilder();
		cache = new ModelCache();
		batch = new ModelBatch();
	}
	
	public void initGameObjects() {
		blocks = new ArrayList<Block>();
		cache.begin();
		for(int y = 0; y < map.getBlocks()[0].length; y++) {
			for(int x = 0; x < map.getBlocks().length; x++) {
				int posX = x * Block.SIDE_LENGTH;
				int posY = y * Block.SIDE_LENGTH;
				if(map.getBlocks()[x][y] == Map.BLOCK) {
					initBlockAndAddToCache(posX, posY);
				}
				else if(map.getBlocks()[x][y] == Map.SPAWN) {
					initPlayer(posX, posY);
				}
			}
		}
		
		// Background grid for depth ?
		Material mat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        Model grid = builder.createLineGrid(250, 250, 1, 1, mat, Usage.Position | Usage.Normal);
        ModelInstance gridInstance = new ModelInstance(grid);
        gridInstance.transform.rotate(Vector3.X, 90);
        gridInstance.transform.translate(0, -3, 0);
        cache.add(gridInstance);
		cache.end();
	}
	
	public void initBlockAndAddToCache(int posX, int posY){
		Material mat = new Material(ColorAttribute.createDiffuse(Color.RED));
		Model model = builder.createBox(Block.SIDE_LENGTH, Block.SIDE_LENGTH, Block.SIDE_LENGTH, mat, Usage.Position | Usage.Normal);
		btCollisionShape collisionShape = new btBoxShape(new Vector3(Block.SIDE_LENGTH / 2, Block.SIDE_LENGTH / 2, Block.SIDE_LENGTH / 2));
		Vector3 pos = new Vector3(posX, -posY, 0);
		Block block = new Block(model, collisionShape, pos);
		blocks.add(block);
		cache.add(block);
	}
	
	public void initPlayer(int posX, int posY){
		Material mat = new Material(ColorAttribute.createDiffuse(Color.SKY));
		Model model = builder.createCone(Player.DIAMETER, Player.DIAMETER, Player.DIAMETER, 100, mat, Usage.Position | Usage.Normal);
		btCollisionShape collisionShape = new btConeShape(Player.DIAMETER, Player.DIAMETER);
		Vector3 pos = new Vector3(posX, -posY, 0);
		player = new Player(model, collisionShape, pos);
	}
	
	public void initEnvironment(){
		//Puts a light in the game scene
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}
	
	public void initCamera(){
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(player.getPosition().x, player.getPosition().y, player.getPosition().z + CAMERA_DISTANCE);
//		cam.position.set(0, 0, CAMERA_DISTANCE);
		cam.near = .1f;
        cam.far = 1500f;
        cam.update();
	}
	
	public void initCollisionHelpers(){
		collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        contactListener = new MyContactListener();
	}
	
	public void initMemberVariables(){
		lerpSpeed = 3f;
	}
	
	/**
	 * For free movement in the game world (for debugging only)
	 */
	public void initDebuggingTools(){
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
	}
	
	public void render(float delta) {
		updateCamera(delta);
		
		//For free movement in the game world (for debugging only)
//		camController.update();
		
		updatePlayerTransform(delta);
		renderGameObjects();
	}
	
	/**
	 * Focuses the camera on the player, having it look at and move with the player as the player moves
	 * @param delta
	 */
	public void updateCamera(float delta) {
		cam.position.lerp(new Vector3(player.getPosition().x, player.getPosition().y, player.getPosition().z + CAMERA_DISTANCE), delta * lerpSpeed);
		cam.lookAt(player.getPosition());
		cam.up.set(0, 1, 0);
		cam.update();
	}
	
	/**
	 * Processes player input
	 * @param delta
	 */
	public void updatePlayerTransform(float delta) {
		
		//ROTATION
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			player.rotate(delta, Player.ROTATE_CLOCKWISE);
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			player.rotate(delta, Player.ROTATE_COUNTER_CLOCKWISE);
		}
		
		//MOVEMENT
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			player.accelerate(delta);
			player.move(delta);
		}
		else {
			player.decelerate(delta);
			player.move(delta);
		}
	}

	/**
	 * Renders all of the model instances that are cached in the model batch
	 */
	public void renderGameObjects() {
		batch.begin(cam);
		//Render the map
		batch.render(cache, environment);
		batch.render(player);
		batch.end();
	}
	
	public void dispose() {
		batch.dispose();
		
		for(Block block : blocks) {
			block.dispose();
		}
		
		cache.dispose();
		player.dispose();
		dispatcher.dispose();
        collisionConfig.dispose();
        contactListener.dispose();
	}
}
