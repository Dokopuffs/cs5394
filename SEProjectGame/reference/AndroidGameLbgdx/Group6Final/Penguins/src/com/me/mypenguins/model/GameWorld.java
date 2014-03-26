package com.me.mypenguins.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import aurelienribon.bodyeditor.BodyEditorLoader;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameWorld {
	
	public int FISH_SCHOOL;
	public int FISH_COUNT;
	public int round;
	
	public int [] fishPos;
	
	public float BOX_TO_WORLD_WIDTH;//pixels per meter
	public float BOX_TO_WORLD_HEIGHT;
	public float slopeMuly = 0.62857142f;
	private static float slopYMul = 0.5285714f;
	private static float slopXMul = 0.35f;
	private static float waterYMul = 0.35f;
	
	private float waterLevel;
	
	private World boxWorld;
	private Body slopeBody,
				 pBody;
	
	private Body [] fish;
	
	private Fixture slopeFixture;
					//circleFixture;
	
	private Vector2 slopeBodyPos;
	private Vector2 slopeBodyDim;//width and height of slope
	public Vector2 originalPengPos;
	
	public Sprite rampSprite,
				  pSprite,
				  fSprite,
				  fontSprite;
	
	public boolean penguinClicked = false;
	private Vector2 vertices[];
	private Texture texture;

	List<Body> bodies;//keeps track of the world's bodies

	/*
	 *   GameWorld()
	 *   
	 *   Initializes a box2d world and adds the fish, slope and penguin to it.
	 *   The gravity is set to the values in the argument, v2.
	 *   
	 */
	
	public GameWorld(Vector2 v2, boolean bool){
		boxWorld = new World(v2, bool);
		readFile();
		fish = new Body[FISH_SCHOOL];
		FISH_COUNT = FISH_SCHOOL;	
		initializeTheSlope(4.2f, 6.4f, 3.7f, 3.75f);
		createCollisionListener();	
		pBody = addPenguin();
		originalPengPos = new Vector2(pBody.getPosition().x, pBody.getPosition().y);	
		setSprite();
		for(int i = 0; i < FISH_SCHOOL; i++)
			fish[i] = addFish((10+fishPos[i]), 1.5f, .2f);
	}
	
	/*
	 *    readFile()
	 *    
	 *    Parses a txt file to find out how many fish to place and display in the world.
	 *    This gets called at the beginning of each round.
	 */
	
	public void readFile(){
		
		FileHandle file = Gdx.files.internal("data/gameWorldInfo.txt");
		
		String info = file.readString();
		info = info.replaceAll("\n","");

		round = Character.digit( info.charAt(0), 10);
		FISH_SCHOOL = Character.digit( info.charAt(1), 10);
		fishPos = new int [FISH_SCHOOL];
		for( int i = 2; i < info.length(); i++){
		   fishPos[i-2] = Character.digit( info.charAt(i), 10);
		}
	}
	
	/*
	 * 
	 *    writeFile() 
	 *    
	 *    At the end of a round writes the placement of the remaining fish to a file.
	 *    
	 */
	
	public void writeFile(){

		round++;
		String info = "";
		info = info.concat(Integer.toString(round));
		info = info.concat(Integer.toString(FISH_COUNT));
		
		for( int i = 0; i < FISH_SCHOOL; i++ ){
			if( fishPos[i] != -1 ){
				info = info.concat(Integer.toString(fishPos[i]));
			}
		}
		
		FileHandle file = Gdx.files.local("data/gameWorldInfo.txt");
		file.writeString(info, false);		
	}
	
	public void reset(){
		round = 1;
		FISH_COUNT = 6;
		FISH_SCHOOL= 6;
		fishPos = new int [FISH_COUNT];
		for( int i = 0; i < FISH_COUNT; i++){
			fishPos[i] = i;
		}
	}
	
	private void createCollisionListener() {
        boxWorld.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
            	
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                
                if( fixtureA.getBody().getUserData() == "fish" && fixtureB.getBody().getUserData() == "penguin"){
                	fixtureA.getBody().setUserData("true");
                }
                else if( fixtureB.getBody().getUserData() == "fish" && fixtureA.getBody().getUserData() == "penguin"){
                	fixtureB.getBody().setUserData("true");
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

	/*
	 *    initializeTheSlope()
	 *    
	 *    Creates a slope body at the given coordinates. 
	 *    Only needs to be called once since the slope is changed by applying new fixtures to the same body.
	 */
	
	private void initializeTheSlope(float xpos1, float xpos2, float ypos1, float ypos2) {
		BodyDef bodydef = new BodyDef();
		slopeBodyPos = new Vector2(xpos1, ypos1);
		slopeBodyDim = new Vector2((xpos2 - xpos1), (ypos2 - ypos1));
		bodydef.position.set(slopeBodyPos);
		slopeBody = boxWorld.createBody(bodydef);
		changeTheSlopeTo(slopeBodyDim.x, slopeBodyDim.y);	
	}
	
	public void setSlope(int x, int y) {
		initializeTheSlope(x*slopXMul, x*slopXMul+0.2f, y*slopYMul, y*slopYMul+0.05f);
	}
	
	public void setWater(int y) {
		waterLevel = y * waterYMul;
	}

	public float getWaterLevel(){
		return waterLevel;
	}

	/*
	 *    changeTheSlopeTo()
	 *    
	 *    changes the dimensions of the slope to the given width and height
	 */
	
	public void changeTheSlopeTo(float width, float height) {

		vertices = new Vector2[3];
		vertices[0] = new Vector2(0, 0);
		vertices[1] = new Vector2(width, 0);
		vertices[2] = new Vector2(width, height);

		PolygonShape temp = new PolygonShape();
		temp.set(vertices);
		if(slopeFixture != null)
			slopeBody.destroyFixture(slopeFixture);
		slopeFixture = slopeBody.createFixture(temp, 10);
        slopeBodyDim.x = width;
        slopeBodyDim.y = height;
		temp.dispose();
	}

	/*
	 *    checkSlopeTouch()
	 *    
	 *    Used to see if the user is attempting to drag the slope by
	 *    checking to see if the given point is within an acceptable threshold of the vertical side of the slope.
	 *    The given coordinates are assumed to be box2d coordinates and not pixels.
	 */
	
	public boolean checkSlopeTouch( float xpos, float ypos ){
		float x = slopeBodyPos.x + slopeBodyDim.x;
		float y = slopeBodyPos.y + slopeBodyDim.y;
		if( Math.abs(xpos - x) < 2 && Math.abs(ypos - y ) < 2 && ypos > slopeBodyPos.y){
			return true;
		}
		return false;
	}
	
	/*
	 *   moveSlope
	 */
	
	public boolean moveSlope(float xpos, float ypos){
		if(slopeBody != null){
	        changeTheSlopeTo(slopeBodyDim.x, ypos - slopeBodyPos.y);
			return true;
		}
		return false;
	}

	/*
	 *    checkPenguinTouch()
	 *    
	 *    Checks to see if the given point is a point on the penguin 
	 *    and returns true if it is.
	 *    
	 */
	
	public boolean checkPenguinTouch(float xpos, float ypos) {

		if(pBody != null)
		{
			ArrayList<Fixture> fixtures = pBody.getFixtureList();
			for( Fixture a : fixtures ){
				if( a.testPoint(xpos, ypos) == true){
					penguinClicked = true;
					return true;
				}	
			}
		}
		return false;
	}
	
	/*
	 *    applyForceToPenguin()
	 *    
	 *    Applies a force to the penguin proportional to how far the penguin has been pulled back.
	 */
	
	public void applyForceToPenguin(float xpos, float ypos){
		float x = pBody.getPosition().x;
		
		boolean touchingPeng = checkPenguinTouch(xpos, ypos);
		if( touchingPeng && originalPengPos.x - x == 0){
			pBody.applyForceToCenter(new Vector2( 800f, 0));
		}
		else if( originalPengPos.x -x > 0){
			pBody.applyForceToCenter(new Vector2( 800 + 1000f*(originalPengPos.x - x), 0));
		}
				
		
	}
	
	/*
	 *    addPenguin
	 *    
	 *    Creates the penguin.
	 *    The restitution must be kept at 0 to keep the penguin from bouncing when it hits the slope.
	 */
	
	public Body addPenguin(){

		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("sidePenguin.json"));

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.position.set(1, 3);
		
		FixtureDef fd = new FixtureDef();
		fd.density = 15f;
		fd.friction = 0f;
		fd.restitution = 0f;
		
		Body body = boxWorld.createBody(bd);
		body.setFixedRotation(true);
		body.setUserData("penguin");
		body.setTransform(new Vector2(3, 4), -0.5f);
		
		loader.attachFixture(body, "sidePeng", fd, 2f);
		return body;
	}

    /*
     *    addFish()
     *    
     *    adds the fish to the box2d world
     */
	
	public Body addFish(float posx, float posy, float radius){
		
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("epicFish.json"));

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.position.set(posx, .3f);
		Body body = boxWorld.createBody(bd);
		body.setUserData("fish");
		
		FixtureDef fd = new FixtureDef();
		fd.density = 21.2f;
		fd.friction = 0f;
		fd.restitution = 0f;

		loader.attachFixture(body, "epicFishies", fd, 2f);
		return body;

	}
	
	/*
	 *    setSprite()
	 */
	
	public void setSprite(){
		
		//add texture for penguin sprite
		texture = new Texture(Gdx.files.internal("penguin_side.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		//set penguin sprite
		pSprite = new Sprite(texture);
		pSprite.setSize(35, 35);
		//texture.dispose();
		
		//add texture for fish sprite
		texture = new Texture(Gdx.files.internal("epicfish.png"));
		texture.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		
		//set fish sprite
		fSprite = new Sprite(texture);
		fSprite.setSize(50,50);
		//texture.dispose();
		
	}

	public Sprite getPenguinSprite(){
		return pSprite;
	}
	
	public Sprite getFishSprite(){
		return fSprite;
	}
	public World getWorld(){
		return boxWorld;
	}

	public Body getPenguin(){
		return pBody;
	}
	public Vector2 getSlopeBodyDim(){
		return slopeBodyDim;
	}
	public void setPenguinPosition(float xpos){
		pBody.setTransform(new Vector2( xpos, pBody.getPosition().y), pBody.getAngle());
	}
	public Body getFish( int i){
		return fish[i];
	}

	public Vector2[] getSlopeVects(){
		return vertices ;
	}

	public Vector2 getSlopeBodyPos(){
		return slopeBodyPos;
	}

	/*
	 *   update()
	 *   
	 *   Steps the box2d world.
	 */
	
	public void update()
	{
		if(boxWorld != null)
		{
			boxWorld.step(1/60f, 6, 2);
			for( int i = 0; i < FISH_SCHOOL; ++i ){
				if( fish[i].getUserData() == "true"){
					boxWorld.destroyBody(fish[i]);
					FISH_COUNT--;
					fishPos[i] = -1;
				}
			}		
		}
	}
}
