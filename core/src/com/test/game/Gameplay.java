package com.test.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;
import com.test.game.collision.GameObjectContactListener;
import com.test.game.objects.Block;
import com.test.game.objects.Player;

public class Gameplay {
	private Map map;
	private ModelBuilder builder;
	private Array<ModelInstance> instances;
	private ModelBatch batch;
	private PerspectiveCamera cam;
	private CameraInputController camController;
	private Environment environment;
	
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private btBroadphaseInterface broadphase;
	private btConstraintSolver constraintSolver;
	
	private GameObjectContactListener contactListener;
	
	private float lerpSpeed;
	
	public static Player player;
	public static btDynamicsWorld dynamicsWorld;
	
	private final static float CAMERA_DISTANCE = 10f;
	private final static short PLAYER_FLAG = 1<<8;
	private final static short BLOCK_FLAG = 1<<9;
	private final static short ALL_FLAG = -1;
	
	public Gameplay(Map map) {
		Bullet.init();
		this.map = map;
		initRenderingHelpers();
		initCollisionHelpers();
		initGameObjects();
        initEnvironment();
        initCamera();
        initMemberVariables();
//        initDebuggingTools();
	}
	
	public void initRenderingHelpers(){
		builder = new ModelBuilder();
		instances = new Array<ModelInstance>();
		batch = new ModelBatch();
	}
	
	public void initCollisionHelpers(){
		collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0f, 0f, 0f));
        contactListener = new GameObjectContactListener();
	}
	
	public void initGameObjects() {
		for(int y = 0; y < map.getBlocks()[0].length; y++) {
			for(int x = 0; x < map.getBlocks().length; x++) {
				float posX = x * Block.SIDE_LENGTH;
				float posY = y * Block.SIDE_LENGTH;
				if(map.getBlocks()[x][y] == Map.BLOCK) {
					initBlockAndAddToCache(new Vector3(posX, -posY, 0f), 1f);
				} else if(map.getBlocks()[x][y] == Map.SPAWN) {
					initPlayer(new Vector3(posX, -posY, 0f), 1f);
				}
			}
		}
		
		// Background grid for depth ?
		Material mat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        Model grid = builder.createLineGrid(250, 250, 1, 1, mat, Usage.Position | Usage.Normal);
        ModelInstance gridInstance = new ModelInstance(grid);
        gridInstance.transform.rotate(Vector3.X, 90);
        gridInstance.transform.translate(0, -3, 0);
        instances.add(gridInstance);
	}
	
	public void initBlockAndAddToCache(Vector3 pos, float mass){
		Material mat = new Material(ColorAttribute.createDiffuse(Color.RED));
		Model model = builder.createBox(Block.SIDE_LENGTH, Block.SIDE_LENGTH, Block.SIDE_LENGTH, mat, Usage.Position | Usage.Normal);
		btCollisionShape collisionShape = new btBoxShape(new Vector3(Block.SIDE_LENGTH / 2f, Block.SIDE_LENGTH / 2f, Block.SIDE_LENGTH / 2f));
		Block block = new Block(model, collisionShape, pos, mass);
		block.getRigidBody().setCollisionFlags(block.getRigidBody().getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		dynamicsWorld.addRigidBody(block.getRigidBody(), BLOCK_FLAG, ALL_FLAG);
		block.getRigidBody().setContactCallbackFlag(BLOCK_FLAG);
		block.getRigidBody().setContactCallbackFilter(0);
		instances.add(block);
	}
	
	public void initPlayer(Vector3 pos, float mass){
		Material mat = new Material(ColorAttribute.createDiffuse(Color.SKY));
		Model model = builder.createCone(Player.getWidth(), Player.getHeight(), Player.getDepth(), 100, mat, Usage.Position | Usage.Normal);
		btCollisionShape collisionShape = new btConeShape(Player.getWidth() / 2f, Player.getHeight());
		player = new Player(model, collisionShape, pos, mass);
		player.getRigidBody().proceedToTransform(player.transform);
		player.getRigidBody().setUserValue(0);
		player.getRigidBody().setCollisionFlags(player.getRigidBody().getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
		dynamicsWorld.addRigidBody(player.getRigidBody(), PLAYER_FLAG, BLOCK_FLAG);
		player.getRigidBody().setContactCallbackFlag(PLAYER_FLAG);
		player.getRigidBody().setContactCallbackFilter(BLOCK_FLAG);
		instances.add(player);
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
		cam.near = .1f;
        cam.far = 1500f;
        cam.update();
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
			player.rotate(delta, Player.getROTATE_CLOCKWISE());
		} else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			player.rotate(delta, Player.getROTATE_COUNTER_CLOCKWISE());
		}
		
		//MOVEMENT
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			player.accelerate(delta);
		} else {
			player.decelerate(delta);
		}
		player.move(delta);
		
		dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
	}

	/**
	 * Renders all of the model instances that are cached in the model batch
	 */
	public void renderGameObjects() {
		batch.begin(cam);
		batch.render(instances, environment);
		batch.end();
	}
	
	public void dispose() {
		batch.dispose();
		
		player.dispose();
		
		dispatcher.dispose();
        collisionConfig.dispose();
        broadphase.dispose();
        constraintSolver.dispose();
        dynamicsWorld.dispose();
        
        contactListener.dispose();
	}
}
