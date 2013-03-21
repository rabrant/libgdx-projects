package com.rabrant.bashnbump;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import com.rabrant.bashnbump.TiledMapHelper;
import com.rabrant.bashnbump.AnimatedTexture;
import com.rabrant.bashnbump.CrateData;
import com.rabrant.bashnbump.BaddieData;

import java.util.Vector;
import java.util.Iterator;


public class BashNBump implements ApplicationListener, ContactListener {
	// General
	public static final float PIXELS_PER_METER = 80.0f;
	
	// Game State
	public static final int GAMESTATE_STARTSCREEN = 0;
	public static final int GAMESTATE_LEVELSTART = 1;
	public static final int GAMESTATE_PLAY = 2;
	public static final int GAMESTATE_PAUSED = 3;
	public static final int GAMESTATE_GAMEOVER = 4;
	public int gameState;
	
	// Textures and Dead List
	private Texture startScreen;
	private Texture levelStart;
	private Texture pausedScreen;
	private Texture gameOverScreen;
	private Texture smallCrate;
	private Texture playerOne;
	private Texture badKat;
	private Texture scoreboard;
	private Vector<IAmDead> deadList;
	private SimpleTimer splashTimer;
	
	private SpriteBatch batch;
	
	// Level
	private int level;
	private float panToX;
	private float panToY;
	private World world;
	private TiledMapHelper tiledLevel;
	private TiledMapHelper tiledCollisionMap;
	private CrateData[] smallCrateData1;
	private CrateData[] smallCrateData2;
	private CrateData[] smallCrateData3;
	private CrateData[] smallCrateData4;
	private CrateData[] smallCrateData5;
	private CrateData[] smallCrateData6;
	private CrateData[] smallCrateData7;
	private CrateData[] smallCrateData8;

	// Player
	private Body po;
	private boolean poIsOnGround;
	private boolean poHeadIsBumped;
	private boolean poIsDead;
	private int poFrameRate;
	private float poVelocityX;
	private AnimatedTexture poAnimated;
	private int poScore;
	private int poLives;
	private int poKills;
	
	// Baddie
	private int baddieCount;
	private int baddieIndex;
	private SimpleTimer spawnTimer;
	private int spawnDelay;
	
	// Input
	private Controller[] controllers; // PLATFORM_DESKTOP: JInput
	private BitmapFontCache scoreCache;
	private BitmapFontCache levelCache;
	private BitmapFontCache livesCache;
	private BitmapFont font;
	private CharSequence str;
	// Debug
	//private Box2DDebugRenderer debugRenderer;
	
	
	// Constructor to define which Platform for Input Handling
	public static final int PLATFORM_DESKTOP = 0;
	public static final int PLATFORM_ANDROID = 1;
	public static final int PLATFORM_OUYA = 2;
	private int myPlatform;
	public BashNBump(int platform) {
		myPlatform = platform;
	}
	
	// LibGDX Framework Provided Methods
	@Override
	public void create() {
		//Gdx.input.setCursorCatched(true); // Hide the mouse cursor for full-screen.
		font = new BitmapFont(Gdx.files.internal("bashnbump/arial.fnt"),
		         Gdx.files.internal("bashnbump/arial.png"), false);
		font.setScale(0.5f);
		scoreCache = new BitmapFontCache(font, true);
		scoreCache.setColor(0.184f, 0.663f, 0.965f, 1.0f);
		levelCache = new BitmapFontCache(font, true);
		levelCache.setColor(0.184f, 0.663f, 0.965f, 1.0f);
		livesCache = new BitmapFontCache(font, true);
		livesCache.setColor(0.184f, 0.663f, 0.965f, 1.0f);
		
		levelStart = new Texture(Gdx.files.internal("bashnbump/Level_Start.png"));
		smallCrate = new Texture(Gdx.files.internal("bashnbump/bnbSmallCrate.png"));
		badKat = new Texture(Gdx.files.internal("bashnbump/PeachKat_Mod.png"));
		playerOne = new Texture(Gdx.files.internal("bashnbump/bnbPlayer1_Mod.png"));
		pausedScreen = new Texture(Gdx.files.internal("bashnbump/Paused_Screen.png"));
		gameOverScreen = new Texture(Gdx.files.internal("bashnbump/Game_Over_Screen.png"));
		loadInput(myPlatform);
		loadLevel(1);
		//loadDebug();
		batch = new SpriteBatch();
		gameState = GAMESTATE_STARTSCREEN;
		startScreen = new Texture(Gdx.files.internal("bashnbump/Start_Screen.png"));
	}

	@Override
	public void dispose() {
		batch.dispose();
		if (startScreen != null)
			startScreen.dispose();
		if (pausedScreen != null)
			pausedScreen.dispose();
		if (gameOverScreen != null)
			gameOverScreen.dispose();
		if (levelStart != null)
			levelStart.dispose();
		if (smallCrate != null)
			smallCrate.dispose();
		if (playerOne != null)
			playerOne.dispose();
		if (badKat != null)
			badKat.dispose();
		if (tiledLevel != null)
			tiledLevel.dispose();
		if (scoreboard != null)
			scoreboard.dispose();
		if (font != null)
			font.dispose();
		if (world != null)
			world.dispose();
	}

	@Override
	public void render() {
		handleInput(myPlatform);

		// Clear the screen
		Gdx.gl.glClearColor(0.8f, 0.7f, 0.6f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(tiledLevel.getCamera().combined);
        
		if (gameState == GAMESTATE_STARTSCREEN) {
			batch.begin();
				// draw start screen overlay:
				batch.draw(startScreen, tiledLevel.getCamera().position.x - 1280 / 2, tiledLevel.getCamera().position.y - 720 / 2);
			batch.end();
		} else if (gameState == GAMESTATE_LEVELSTART) {
			// draw level start overlay for 3 seconds:
			if (splashTimer.stopwatch(3000)) {
				gameState = GAMESTATE_PLAY;
			} else {
				batch.begin();
					batch.draw(levelStart, tiledLevel.getCamera().position.x - 1280 / 2, tiledLevel.getCamera().position.y - 720 / 2);

					// draw scoreboard overlay:
					batch.draw(scoreboard, tiledLevel.getCamera().position.x - 1280 / 2, 280 + tiledLevel.getCamera().position.y);
					// draw information:
					//font.draw(batch, "Score: " + poScore, tiledLevel.getCamera().position.x - (1280 / 2) + 30, 280 + tiledLevel.getCamera().position.y + 48);
					str = "Score: " + poScore;
					scoreCache.setText(str, 0.0f, 0.0f);
					scoreCache.setText(str, tiledLevel.getCamera().position.x - (1280 / 2) + 30, 280 + tiledLevel.getCamera().position.y + 48);
					scoreCache.draw(batch);
					//font.draw(batch, "Level: " + level, tiledLevel.getCamera().position.x - (1280 / 2) + 640, 280 + tiledLevel.getCamera().position.y + 48);
					str = "Level: " + level;
					levelCache.setText(str, 0.0f, 0.0f);
					levelCache.setText(str, tiledLevel.getCamera().position.x - (1280 / 2) + 640 - levelCache.getBounds().width / 2, 280 + tiledLevel.getCamera().position.y + 48);
					levelCache.draw(batch);
					//font.draw(batch, "Lives: " + poLives, tiledLevel.getCamera().position.x - (1280 / 2) + 1200, 280 + tiledLevel.getCamera().position.y + 48);
					str = "Lives: " + poLives;
					livesCache.setText(str, 0.0f, 0.0f);
					livesCache.setText(str, tiledLevel.getCamera().position.x - (1280 / 2) + 1280 - levelCache.getBounds().width - 30, 280 + tiledLevel.getCamera().position.y + 48);
					livesCache.draw(batch);
				batch.end();
			}
		} else if (gameState == GAMESTATE_PAUSED) {
			batch.begin();
				// draw level start overlay for 3 seconds:
				batch.draw(pausedScreen, tiledLevel.getCamera().position.x - 1280 / 2, tiledLevel.getCamera().position.y - 720 / 2);
			batch.end();
		} else if (gameState == GAMESTATE_GAMEOVER) {
			batch.begin();
				// draw level start overlay for 3 seconds:
				batch.draw(gameOverScreen, tiledLevel.getCamera().position.x - 1280 / 2, tiledLevel.getCamera().position.y - 720 / 2);
				
				// draw scoreboard overlay:
				batch.draw(scoreboard, tiledLevel.getCamera().position.x - 1280 / 2, 280 + tiledLevel.getCamera().position.y);
				// draw information:
				//font.draw(batch, "Score: " + poScore, 375, tiledLevel.getCamera().position.y - 140);
				str = "Score: " + poScore;
				scoreCache.setText(str, 0.0f, 0.0f);
				scoreCache.setText(str, (1280 / 3) - levelCache.getBounds().width / 2, 280 + tiledLevel.getCamera().position.y + 48);
				scoreCache.draw(batch);
				//font.draw(batch, "Level: " + level, 855, tiledLevel.getCamera().position.y - 140);
				str = "Level: " + level;
				levelCache.setText(str, 0.0f, 0.0f);
				levelCache.setText(str, (1280 / 3) * 2 - levelCache.getBounds().width / 2, 280 + tiledLevel.getCamera().position.y + 48);
				levelCache.draw(batch);
			batch.end();
		} else if (gameState == GAMESTATE_PLAY) {
			update();
			
			batch.begin();
				// draw small crates:
				drawSmallCrates();
				// draw background
				//batch.draw(background, 40, 40);
				// draw player one:
				float playerOneX = 0.0f;
				float playerOneY = 0.0f;
				if (po != null) {
					playerOneX = PIXELS_PER_METER * po.getPosition().x;
					playerOneY = PIXELS_PER_METER * po.getPosition().y;
					batch.draw(poAnimated.getRegion(), playerOneX, playerOneY);
				} else if(!poIsDead) { 
					loadPlayer();
				}
				// draw bad kat:
				float badKatX = 0.0f;
				float badKatY = 0.0f;
				BaddieData baddieData;
				for (Iterator<Body> iter = world.getBodies(); iter.hasNext();) {
				     Body body = iter.next();
				     if(body!=null) {
				    	 if (body.getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData")) {
					          baddieData = (BaddieData) body.getUserData();
					          if (baddieData.getBody() != null) {
					        	  	BaddieData bkData = (BaddieData) baddieData.getBody().getUserData();
					        	  	if (bkData != null) {
					        	  		badKatX = PIXELS_PER_METER * baddieData.getBody().getPosition().x;
					        	  		badKatY = PIXELS_PER_METER * baddieData.getBody().getPosition().y;
					        	  		batch.draw(baddieData.getAnimatedTexture().getRegion(), badKatX, badKatY);
					        	  		//System.out.println("VelocityX: " + baddieData.getBody().getLinearVelocity().x + " for " + baddieData.getName());
					          		} else {
					        	  		baddieData.setBody(null);
					        	  	}
					          }
				    	 }
				     }
				}
			batch.end();
			
			tiledLevel.render();
			
			batch.begin();
				float deadGuyX = 0.0f;
				float deadGuyY = 0.0f;
				for (int k = 0; k < deadList.size(); k++) {
					IAmDead deadGuy = deadList.get(k);
					deadGuyX = deadGuy.getPosX();
					deadGuyY = deadGuy.getPosY();
					batch.draw(deadGuy.getAnimatedTexture().getRegion(), deadGuyX, deadGuyY);
				}
				// draw scoreboard overlay:
				batch.draw(scoreboard, 0, 280 + tiledLevel.getCamera().position.y);
				// draw debug fps:
				//font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), playerOneX, playerOneY + 100);
				// draw information:
				//font.draw(batch, "Score: " + poScore, 30, 280 + tiledLevel.getCamera().position.y + 48);
				str = "Score: " + poScore;
				scoreCache.setText(str, 0.0f, 0.0f);
				scoreCache.setText(str, 30, 280 + tiledLevel.getCamera().position.y + 48);
				scoreCache.draw(batch);
				//font.draw(batch, "Level: " + level, 640, 280 + tiledLevel.getCamera().position.y + 48);
				str = "Level: " + level;
				levelCache.setText(str, 0.0f, 0.0f);
				levelCache.setText(str, 1280 / 2 - levelCache.getBounds().width / 2, 280 + tiledLevel.getCamera().position.y + 48);
				levelCache.draw(batch);
				//font.draw(batch, "Lives: " + poLives, 1200, 280 + tiledLevel.getCamera().position.y + 48);
				str = "Lives: " + poLives;
				livesCache.setText(str, 0.0f, 0.0f);
				livesCache.setText(str, 1280 - levelCache.getBounds().width - 30, 280 + tiledLevel.getCamera().position.y + 48);
				livesCache.draw(batch);
			batch.end();
			
			// DEBUG:
			//debugRenderer.render(world, tiledLevel.getCamera().combined.scale(PIXELS_PER_METER, PIXELS_PER_METER, PIXELS_PER_METER));
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	// ContactListener Provided Methods
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		BaddieData tmpBaddie;
		BaddieData tmpBaddie2;
		String tmpString = "";
		
		
		//System.out.println("A: " + fixA.getBody().getUserData().getClass() + " B: " + fixB.getBody().getUserData().getClass());
		
		WorldManifold worldManifold;
		worldManifold = contact.getWorldManifold();
		Vector2 worldNormal = worldManifold.getNormal();
		Vector2[] worldPoints = worldManifold.getPoints();
		
		//System.out.println(worldNormal.x + ", " + worldNormal.y);
		
	    int foundCollisions = 0;
	    // 0 - no collision
	    // 1 - collision from top
	    // 2 - collision from bottom
	    // 4 - collision from left
	    // 8 - collision from right
	    /* <BADKAT> */
	    if (fixB.getBody().getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData") && fixA.getBody().getUserData().getClass().toString().matches("class java.lang.String")) {
		    tmpBaddie = (BaddieData) fixB.getBody().getUserData();
		    tmpBaddie2 = new BaddieData();
		    tmpString = (String) fixA.getBody().getUserData();
	    } else if (fixA.getBody().getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData") && fixB.getBody().getUserData().getClass().toString().matches("class java.lang.String")) {
	    	tmpBaddie = (BaddieData) fixA.getBody().getUserData();
	    	tmpBaddie2 = new BaddieData();
		    tmpString = (String) fixB.getBody().getUserData();
		} else if (fixA.getBody().getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData") && fixB.getBody().getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData")) { 
			tmpBaddie = (BaddieData) fixA.getBody().getUserData();
			tmpBaddie2 = (BaddieData) fixB.getBody().getUserData();
		} else {
			tmpBaddie = new BaddieData();
			tmpBaddie2 = new BaddieData();
			tmpString = "";
		}
	    
	    if (tmpBaddie.getType().matches("BadKat") && tmpBaddie2.getType().matches("BadKat")) {
		    
			//if (r1->y > r2->y)
		        // r2 is above r1
	    	if (worldNormal.y < 0) {
	    		foundCollisions = 1;
	    	}
		    //if (r1->y < r2->y)
		        // r2 is below r1
		    if (worldNormal.y > 0) {
	    		foundCollisions = 2;
		    }
		    //if (r1->x > r2->x)
		        // r2 stands left of r1
		    if (worldNormal.x > 0) {
		    	foundCollisions = foundCollisions | 4;
		    	//System.out.println("r2 left of r1");
		    	//tmpBaddie.setVelocityX(tmpBaddie.getSpeed() * -1.0f);
		    	if (tmpBaddie.getBody().getPosition().x > tmpBaddie2.getBody().getPosition().x) {
		    		tmpBaddie.setVelocityX(tmpBaddie.getSpeed());
		    		tmpBaddie2.setVelocityX(tmpBaddie2.getSpeed() * -1.0f);
		    	} else {
		    		tmpBaddie.setVelocityX(tmpBaddie.getSpeed() * -1.0f);
		    		tmpBaddie2.setVelocityX(tmpBaddie2.getSpeed());
		    	}
		    }
		    //if (r1->x < r2->x)
		        // r2 stands right of r1
		    if (worldNormal.x < 0) {
		    	foundCollisions = foundCollisions | 8;
		    	//System.out.println("r2 right of r1");
		    	//tmpBaddie.setVelocityX(tmpBaddie.getSpeed() * -1.0f);
		    	if (tmpBaddie.getBody().getPosition().x > tmpBaddie2.getBody().getPosition().x) {
		    		tmpBaddie.setVelocityX(tmpBaddie.getSpeed());
		    		tmpBaddie2.setVelocityX(tmpBaddie2.getSpeed() * -1.0f);
		    	} else {
		    		tmpBaddie.setVelocityX(tmpBaddie.getSpeed() * -1.0f);
		    		tmpBaddie2.setVelocityX(tmpBaddie2.getSpeed());
		    	}
		    }

		    //System.out.println("fixA: " + collisionSideText(foundCollisions));
	    } else if (tmpBaddie.getType().matches("BadKat") && tmpString.matches("Ground")) {
		    //if (r1->y > r2->y)
		        // r2 is above r1
	    	if (worldNormal.y < 0) {
	    		foundCollisions = 1;
	    	}
		    //if (r1->y < r2->y)
		        // r2 is below r1
		    if (worldNormal.y > 0) {
	    		foundCollisions = 2;
		    }
		    //if (r1->x > r2->x)
		        // r2 stands left of r1
		    if (worldNormal.x > 0) {
		    	foundCollisions = foundCollisions | 4;
		    	tmpBaddie.setVelocityX(tmpBaddie.getSpeed());
		    }
		    //if (r1->x < r2->x)
		        // r2 stands right of r1
		    if (worldNormal.x < 0) {
		    	foundCollisions = foundCollisions | 8;
		    	tmpBaddie.setVelocityX(tmpBaddie.getSpeed() * -1.0f);
		    }
		    //System.out.println("fixA: " + collisionSideText(foundCollisions));
	    } else if (tmpString.matches("PlayerOne") && tmpBaddie.getType().matches("BadKat")) {
		    //if (r1->y > r2->y)
	        // r2 is above r1
	    	if (worldNormal.y < 0) {
	    		foundCollisions = 1;
	    	}
		    //if (r1->y < r2->y)
		        // r2 is below r1
		    if (worldNormal.y > 0) {
	    		foundCollisions = 2;
		    }
		    //if (r1->x > r2->x)
		        // r2 stands left of r1
		    if (worldNormal.x > 0) {
		    	foundCollisions = foundCollisions | 4;
		    	tmpBaddie.setVelocityX(tmpBaddie.getSpeed() * -1.0f);
		    }
		    //if (r1->x < r2->x)
		        // r2 stands right of r1
		    if (worldNormal.x < 0) {
		    	foundCollisions = foundCollisions | 8;
		    	tmpBaddie.setVelocityX(tmpBaddie.getSpeed());
		    }
		    if (tmpBaddie.isHit() && !tmpBaddie.isDead()) {
		    	tmpBaddie.setIsDead(true);
		    	
		    	IAmDead deadBaddie = new IAmDead();
		    	deadBaddie.setName(tmpBaddie.getName());
		    	deadBaddie.setType(tmpBaddie.getType());
		    	deadBaddie.setAnimatedTexture(tmpBaddie.getAnimatedTexture());
		    	deadBaddie.setPosX(tmpBaddie.getBody().getPosition().x * PIXELS_PER_METER);
		    	deadBaddie.setPosY(tmpBaddie.getBody().getPosition().y * PIXELS_PER_METER);
		    	deadBaddie.getAnimatedTexture().setCurrentAnimation("dead");
		    	deadList.add(deadBaddie);
		    	
				//System.out.println("Bad Kat Died!");
		    } else if (!tmpBaddie.isHit() && !poIsDead) {
		    	poIsDead = true;

		    	IAmDead deadPlayer = new IAmDead();
		    	deadPlayer.setName("PlayerOne");
		    	deadPlayer.setType("Player");
		    	deadPlayer.setAnimatedTexture(poAnimated);
		    	deadPlayer.setPosX(po.getPosition().x * PIXELS_PER_METER);
		    	deadPlayer.setPosY(po.getPosition().y * PIXELS_PER_METER);
		    	deadPlayer.getAnimatedTexture().setCurrentAnimation("dead");
		    	deadList.add(deadPlayer);
		    	
		    	//System.out.println("Player One Died!");
		    }
	    }
	    /* </BADKAT> */
    	
	    if (fixB.getBody().getUserData() == "PlayerOne" && fixA.getBody().getUserData() == "Ground") {
		    //if (r1->y > r2->y)
	        // r2 is above r1
	    	if (worldNormal.y > 0) {
	    		foundCollisions = 1;
	    		poHeadIsBumped = false;
	    	}
		    //if (r1->y < r2->y)
		        // r2 is below r1
		    if (worldNormal.y < 0) {
	    		foundCollisions = 2;
	    		poHeadIsBumped = true;
		    }
		    //if (r1->x > r2->x)
		        // r2 stands left of r1
		    if (worldNormal.x < 0) {
		    	foundCollisions = foundCollisions | 4;
		    }
		    //if (r1->x < r2->x)
		        // r2 stands right of r1
		    if (worldNormal.x > 0) {
		    	foundCollisions = foundCollisions | 8;
		    }
		    //System.out.println("fixB: " + collisionSideText(foundCollisions));
		    
		    if ((foundCollisions & 2) > 0) {
		    	int[] hitX = new int[1];
	    		int[] hitY = new int[1];
	    		if (worldPoints.length > 0) {
	    			hitX = new int[worldPoints.length];
	    			hitY = new int[worldPoints.length];
	    		}
		    	for (int i = 0; i < worldPoints.length; i++) {
		    		hitX[i] = Math.round(worldPoints[i].x * PIXELS_PER_METER);
		    		hitY[i] = Math.round(worldPoints[i].y * PIXELS_PER_METER);
		    		
		    		if (hitY[i] >= 160 && hitY[i] < 180) { // Row 1
		    			for (int k = 0; k < smallCrateData1.length; k++) {
		    				int testX = smallCrateData1[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData1[k].isAnimated()) {
		    						smallCrateData1[k].setIsAnimated(true);
		    						smallCrateData1[k].setIsHit(true);
		    						smallCrateData1[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		} else if (hitY[i] >= 320 && hitY[i] < 340) { // Row 2
		    			for (int k = 0; k < smallCrateData2.length; k++) {
		    				int testX = smallCrateData2[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData2[k].isAnimated()) {
		    						smallCrateData2[k].setIsAnimated(true);
		    						smallCrateData2[k].setIsHit(true);
		    						smallCrateData2[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		} else if (hitY[i] >= 480 && hitY[i] < 500) { // Row 3
		    			for (int k = 0; k < smallCrateData3.length; k++) {
		    				int testX = smallCrateData3[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData3[k].isAnimated()) {
		    						smallCrateData3[k].setIsAnimated(true);
		    						smallCrateData3[k].setIsHit(true);
		    						smallCrateData3[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		} else if (hitY[i] >= 520 && hitY[i] < 540) { // Row 4
		    			for (int k = 0; k < smallCrateData4.length; k++) {
		    				int testX = smallCrateData4[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData4[k].isAnimated()) {
		    						smallCrateData4[k].setIsAnimated(true);
		    						smallCrateData4[k].setIsHit(true);
		    						smallCrateData4[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		} else if (hitY[i] >= 680 && hitY[i] < 700) { // Row 5
		    			for (int k = 0; k < smallCrateData5.length; k++) {
		    				int testX = smallCrateData5[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData5[k].isAnimated()) {
		    						smallCrateData5[k].setIsAnimated(true);
		    						smallCrateData5[k].setIsHit(true);
		    						smallCrateData5[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		} else if (hitY[i] >= 720 && hitY[i] < 740) { // Row 6
		    			for (int k = 0; k < smallCrateData6.length; k++) {
		    				int testX = smallCrateData6[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData6[k].isAnimated()) {
		    						smallCrateData6[k].setIsAnimated(true);
		    						smallCrateData6[k].setIsHit(true);
		    						smallCrateData6[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		} else if (hitY[i] >= 880 && hitY[i] < 900) { // Row 7
		    			for (int k = 0; k < smallCrateData7.length; k++) {
		    				int testX = smallCrateData7[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData7[k].isAnimated()) {
		    						smallCrateData7[k].setIsAnimated(true);
		    						smallCrateData7[k].setIsHit(true);
		    						smallCrateData7[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		} else if (hitY[i] >= 1040 && hitY[i] < 1060) { // Row 8
		    			for (int k = 0; k < smallCrateData8.length; k++) {
		    				int testX = smallCrateData8[k].getX();
		    				if (hitX[i] >= testX && hitX[i] < testX + 40) {
		    					if (!smallCrateData8[k].isAnimated()) {
		    						smallCrateData8[k].setIsAnimated(true);
		    						smallCrateData8[k].setIsHit(true);
		    						smallCrateData8[k].setIsMoving(true);
		    					}
		    				}
		    			}
		    		}
		    		//System.out.println("x: " + hitX[i] + " y: " + hitY[i]);
		    	}
		    }
	    }
	}
	public void endContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		WorldManifold worldManifold;
		worldManifold = contact.getWorldManifold();
		Vector2 worldNormal = worldManifold.getNormal();
		
		int foundCollisions = 0;
	    // 0 - no collision
	    // 1 - collision from top
	    // 2 - collision from bottom
	    // 4 - collision from left
	    // 8 - collision from right
		
		if (fixA.getBody().getUserData() == "PlayerOne" && fixB.getBody().getUserData() == "Ground") {
		    //if (r1->y > r2->y)
		        // r2 is above r1
	    	if (worldNormal.y < 0) {
	    		foundCollisions = 1;
	    	}
		    //if (r1->y < r2->y)
		        // r2 is below r1
		    if (worldNormal.y > 0) {
	    		foundCollisions = 2;
		    }
		    //if (r1->x > r2->x)
		        // r2 stands left of r1
		    if (worldNormal.x > 0) {
		    	foundCollisions = foundCollisions | 4;
		    }
		    //if (r1->x < r2->x)
		        // r2 stands right of r1
		    if (worldNormal.x < 0) {
		    	foundCollisions = foundCollisions | 8;
		    }
	
		    //System.out.println("endContact: " + collisionSideText(foundCollisions));
	    } else if (fixB.getBody().getUserData() == "PlayerOne" && fixA.getBody().getUserData() == "Ground") {
		    //if (r1->y > r2->y)
	        	// r2 is above r1
	    	if (worldNormal.y > 0) {
	    		foundCollisions = 1;
	    		poHeadIsBumped = false;
	    	}
		    //if (r1->y < r2->y)
		        // r2 is below r1
		    if (worldNormal.y < 0) {
	    		foundCollisions = 2;
		    }
		    //if (r1->x > r2->x)
		        // r2 stands left of r1
		    if (worldNormal.x < 0) {
		    	foundCollisions = foundCollisions | 4;
		    	poHeadIsBumped = false;
		    }
		    //if (r1->x < r2->x)
		        // r2 stands right of r1
		    if (worldNormal.x > 0) {
		    	foundCollisions = foundCollisions | 8;
		    	poHeadIsBumped = false;
		    }
		    //System.out.println("endContact: " + collisionSideText(foundCollisions));
	    }
	}
	public void postSolve(Contact contact, ContactImpulse contImp) {
		//System.out.println("postSolve");
	}
	public void preSolve(Contact contact, Manifold manifold) {
		//System.out.println("preSolve");
	}
	public String collisionSideText(int collisionsides) {
		String sideText = "";
		switch (collisionsides) {
		case 0: sideText = "None";
			break;
		case 1: sideText = "Top";
			break;
		case 2: sideText = "Bottom";
			break;
		case 3: sideText = "Top and Bottom";
			break;
		case 4: sideText = "Left";
			break;
		case 5: sideText = "Top and Left";
			break;
		case 6: sideText = "Bottom and Left";
			break;
		case 7: sideText = "Top and Bottom and Left";
			break;
		case 8: sideText = "Right";
			break;
		case 9: sideText = "Top and Right";
			break;
		case 10: sideText = "Bottom and Right";
			break;
		case 11: sideText = "Top and Bottom and Right";
			break;
		default:
			break;
		}
		
		return sideText;
	}
	
	// User Defined Methods:
	public void handleInput(int whichPlatform) {
		String compName = "";
		float value = 0.0f;
		
		if (whichPlatform == PLATFORM_DESKTOP) {
			/* Remember to poll each one */
			controllers[controllers.length-1].poll();
			/* Get the controllers event queue */
			EventQueue queue = controllers[controllers.length-1].getEventQueue();
			/* Create an event object for the underlying plugin to populate */
			Event event = new Event();
			/* For each object in the queue */
			while (queue.getNextEvent(event)) {
				// Grab The Component and Value
				Component comp = event.getComponent();
				compName = comp.getName();
				value = event.getValue();
				
				/*
				 * Used for Debugging:
				 * Create a string buffer and put in it, the controller name,
				 * the time stamp of the event, the name of the component
				 * that changed and the new value.
				 * 
				 * Note that the time-stamp is a relative thing, not
				 * absolute, we can tell what order events happened in
				 * across controllers this way. We can not use it to tell
				 * exactly *when* an event happened just the order.
				 */
				StringBuffer buffer = new StringBuffer(
						controllers[controllers.length-1].getName());
				buffer.append(" at ");
				buffer.append(event.getNanos()).append(", ");
				buffer.append(compName).append(" changed to ");
				
				/*
				 * Check the type of the component and display an
				 * appropriate value
				 */
				if (comp.isAnalog()) {
					buffer.append(value);
				} else {
					if (value == 1.0f) {
						buffer.append("On: " + value);
					} else {
						buffer.append("Off: " + value);
					}
				}
				// Controller Debug output:
				//System.out.println(buffer.toString());
				if (compName.matches("x") || compName.matches("X Axis")) {
					compName = "AXIS_LS_X";
				} else if (compName.matches("A") || compName.matches("Button 0")) {
					compName = "BUTTON_O";
				} else if (compName.matches("Y") || compName.matches("Button 3")) {
					compName = "BUTTON_Y";
				}
				
				applyInputLogic(compName, value);
			}
		} else if (whichPlatform == PLATFORM_ANDROID) {
			// STUB
			// applyInputLogic(compName, value);
		} else if (whichPlatform == PLATFORM_OUYA) {
			// STUB
			// applyInputLogic(compName, value);
		}
	}
	
	// Apply Game Logic Based On Input
	public void applyInputLogic(String compName, float value) {
		//System.out.println(compName + " value: " + value);
		// Start Screen
		if (gameState == GAMESTATE_STARTSCREEN) {
			if (compName.matches("BUTTON_Y") && value == 1.0f) {
				gameState = GAMESTATE_LEVELSTART;
				splashTimer = new SimpleTimer();
				deadList = new Vector<IAmDead>();
				spawnTimer = new SimpleTimer();
				poScore = 0;
				poLives = 3;
				level = 1;
				
				loadLevel(level);
				loadPlayer();
				loadScoreboard();
				//loadDebug();
			}
			if (compName.matches("BUTTON_O") && value == 1.0f) {
				Gdx.app.exit();
			}
		} else 
		
		// Game Play
		if (gameState == GAMESTATE_PLAY) {
			if (compName.matches("BUTTON_Y") && value == 1.0f) {
				gameState = GAMESTATE_PAUSED;
			}
			if (po != null) {
				// Moving!
				if (compName.matches("AXIS_LS_X")) {
					poVelocityX = value;
				}
				// Jumping!
				if (compName.matches("BUTTON_O") && value == 1.0f && poIsOnGround) {
					po.applyLinearImpulse(new Vector2(0.0f, 0.8f),
							po.getWorldCenter());
					poIsOnGround = false;
					if (poAnimated.getCurrentAnimation() == "standEast" 
							|| poAnimated.getCurrentAnimation() == "walkEast") {
						poAnimated.animate("jumpEast");
					} else if (poAnimated.getCurrentAnimation() == "standWest" 
							|| poAnimated.getCurrentAnimation() == "walkWest") {
						poAnimated.animate("jumpWest");
					}
				} else if ((compName.matches("BUTTON_O")) && value == 0.0f) {
					po.applyLinearImpulse(new Vector2(0.0f, -0.2f),
							po.getWorldCenter());
				}
			}
		} else 
		
		// Game Paused
		if (gameState == GAMESTATE_PAUSED) {
			if (compName.matches("BUTTON_Y") && value == 1.0f) {
				gameState = GAMESTATE_PLAY;
			} else if (compName.matches("BUTTON_O") && value == 1.0f) {
				gameState = GAMESTATE_STARTSCREEN;
			}
		} else 
		
		if (gameState == GAMESTATE_GAMEOVER) {
			if (compName.matches("BUTTON_Y") && value == 1.0f) {
				gameState = GAMESTATE_STARTSCREEN;
			}
		}
	}
	
	public void update() {
		BaddieData baddieData;
		if (poLives < 0) {
			//System.out.println(poScore);
			gameState = GAMESTATE_GAMEOVER;
		}

		if (poKills >= 15) {
			level = level + 1;
			loadLevel(level);
			loadPlayer();
			gameState = GAMESTATE_LEVELSTART;
			splashTimer.reset();
			deadList.removeAllElements();
		}
		updateTheDead();
		animateSmallCrates();
		calcBaddieFlips();
		
		// Player One Animation Timer
		if (po != null) {
			if (Math.abs(po.getLinearVelocity().x) > 3) {
				poFrameRate = 240;
			} else if (Math.abs(po.getLinearVelocity().x) < 1) {
				poFrameRate = 480;
			} else {
				poFrameRate = 320;
			}
		}
		
		// Ground Logic:
		/* <BADKAT> */
		for (Iterator<Body> iter = world.getBodies(); iter.hasNext();) {
			Body body = iter.next();
			if(body!=null) {
				if (body.getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData")) {
					baddieData = (BaddieData) body.getUserData();
					if (baddieData.getBody() != null) {
						if (Math.abs(baddieData.getBody().getLinearVelocity().y) < 1e-6) {
							baddieData.setIsOnGround(true);
						} else {
							baddieData.setIsOnGround(false);
						}
						
						if (baddieData.isHit()) {
							baddieData.getBody().setLinearVelocity(new Vector2(0.0f, baddieData.getBody().getLinearVelocity().y));
						}
						if (baddieData.getAnimatedTexture().getCurrentAnimation() == "walkEast" && baddieData.isHit()) {
							baddieData.getAnimatedTexture().animate("hitEast");
						} else if (baddieData.getAnimatedTexture().getCurrentAnimation() == "walkWest" && baddieData.isHit()) {
							baddieData.getAnimatedTexture().animate("hitWest");
						}
					
						if (baddieData.getBody().getLinearVelocity().x > 0.05f) {
							baddieData.getAnimatedTexture().animate("walkEast");
						} else if (baddieData.getBody().getLinearVelocity().x < -0.05f) {
							baddieData.getAnimatedTexture().animate("walkWest");
						} else if (baddieData.getBody().getLinearVelocity().x == 0.0f && !baddieData.isHit()) {
							if (Math.random() < 0.5f) {
								baddieData.getBody().applyLinearImpulse(new Vector2(baddieData.getSpeed(), 0.0f), baddieData.getBody().getWorldCenter());
							} else {
								baddieData.getBody().applyLinearImpulse(new Vector2(baddieData.getSpeed() * -1.0f, 0.0f), baddieData.getBody().getWorldCenter());
							}
						}
			    	 }
			     }
			}
		}
		/* </BADKAT> */
		if (po != null) {
			if (Math.abs(po.getLinearVelocity().y) < 1e-6) {
				poIsOnGround = true;
				if (poAnimated.getCurrentAnimation() == "jumpEast" && poHeadIsBumped == false) {
					if (po.getLinearVelocity().x > 0.25f) {
						poAnimated.animate("walkEast", poFrameRate, true);
					} else {
						poAnimated.animate("standEast");
					}
				} else if (poAnimated.getCurrentAnimation() == "jumpWest" && poHeadIsBumped == false) {
					if (po.getLinearVelocity().x < -0.25f) {
						poAnimated.animate("walkWest", poFrameRate, true);
					} else {
						poAnimated.animate("standWest");
					}
				}
			} else {
				poIsOnGround = false;
				if (poAnimated.getCurrentAnimation() == "walkEast"
						|| poAnimated.getCurrentAnimation() == "standEast") {
					poAnimated.animate("jumpEast");
				} else if (poAnimated.getCurrentAnimation() == "walkWest"
						|| poAnimated.getCurrentAnimation() == "standWest") {
					poAnimated.animate("jumpWest");
				}
			}
			// Apply Movement!
			if (poVelocityX > 0.25 && poIsOnGround && poHeadIsBumped == false) {
				poAnimated.animate("walkEast", poFrameRate, true);
			} else if (poVelocityX < -0.25 && poIsOnGround && poHeadIsBumped == false) {
				poAnimated.animate("walkWest", poFrameRate, true);
			} else if (poVelocityX >= -0.25 && poVelocityX <= 0.25) {
				if (poAnimated.getCurrentAnimation() == "walkEast") {
					poAnimated.animate("standEast");
				}
				if (poAnimated.getCurrentAnimation() == "walkWest") {
					poAnimated.animate("standWest");
				}
			}
		}
		/* <BADKAT> */
		for (Iterator<Body> iter = world.getBodies(); iter.hasNext();) {
			Body body = iter.next();
			if(body!=null) {
				if (body.getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData")) {
					baddieData = (BaddieData) body.getUserData();
					if (baddieData.getBody() != null) {
						if (baddieData.isOnGround()) {
							if (!baddieData.isHit()) {
								baddieData.getBody().applyLinearImpulse(new Vector2(0.05f*baddieData.getVelocityX(), 0.0f),
									baddieData.getBody().getWorldCenter());
							}
						} else {
							if (!baddieData.isHit()) {
								baddieData.getBody().applyLinearImpulse(new Vector2(0.02f*baddieData.getVelocityX(), 0.0f),
									baddieData.getBody().getWorldCenter());
							}
						}
					}
				}
			}
		}
		/* </BADKAT> */
		if (po != null) {
			if (poIsOnGround) {
				po.applyLinearImpulse(new Vector2(0.05f*poVelocityX, 0.0f),
						po.getWorldCenter());
			} else {
				po.applyLinearImpulse(new Vector2(0.01f*poVelocityX, 0.0f),
						po.getWorldCenter());
			}
		}
		
		// Reset Max Velocity so our dude don't go hog-wild.
		/* <BADKAT> */
		for (Iterator<Body> iter = world.getBodies(); iter.hasNext();) {
			Body body = iter.next();
			if(body!=null) {
				if (body.getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData")) {
					baddieData = (BaddieData) body.getUserData();
					if (baddieData.getBody() != null) {
						if (baddieData.getBody().getLinearVelocity().x > baddieData.getSpeed() * 5.0f) {
							baddieData.getBody().setLinearVelocity(new Vector2(baddieData.getSpeed() * 5.0f, baddieData.getBody().getLinearVelocity().y));
						} else if (baddieData.getBody().getLinearVelocity().x < baddieData.getSpeed() * -5.0f) {
							baddieData.getBody().setLinearVelocity(new Vector2(baddieData.getSpeed() * -5.0f, baddieData.getBody().getLinearVelocity().y));
						}
					}
				}
			}
		}
		/* </BADKAT> */
		if (po != null) {
			if (po.getLinearVelocity().x > 4) {
				po.setLinearVelocity(new Vector2(4.0f, po.getLinearVelocity().y));
			} else if (po.getLinearVelocity().x < -4) {
				po.setLinearVelocity(new Vector2(-4.0f, po.getLinearVelocity().y));
			}
		}
		
		// Update the physics-world
		world.step(1.0f / 60.0f, 8, 3);
		for (Iterator<Body> iter = world.getBodies(); iter.hasNext();) {
		     Body body = iter.next();
		     if(body!=null) {
		    	 if (body.getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData")) {
			          BaddieData data = (BaddieData) body.getUserData();
			          if(data.isDead()) {
			        	  world.destroyBody(body);
			        	  body.setUserData(null);
			        	  body = null;
			        	  baddieCount--;
			        	  // Scoring Logic Goes Here: Gaining Points
			        	  poScore += data.getValue() + level;
			        	  poKills++;
			          } else if (body.getPosition().y * PIXELS_PER_METER < 40) {
			        	  world.destroyBody(body);
			        	  body.setUserData(null);
			        	  body = null;
			        	  baddieCount--;
			          } else if (body.getPosition().y * PIXELS_PER_METER < 120 && (body.getPosition().x * PIXELS_PER_METER < 40 || (body.getPosition().x * PIXELS_PER_METER) + 40 > 1240)) {
			        	  world.destroyBody(body);
			        	  body.setUserData(null);
			        	  body = null;
			        	  baddieCount--;
			        	  // Scoring Logic Goes Here: Losing Points
			        	  poScore -= 5;
			          }
		    	 } else if (body.getUserData().getClass().toString().matches("class java.lang.String")) {
			          String data = (String) body.getUserData();
			          if(data.matches("PlayerOne") && poIsDead) {
			        	  world.destroyBody(body);
			        	  body.setUserData(null);
			        	  body = null;
			        	  po = null;
			          }
		    	 }
		     }
		}
		//System.gc();
		// Spawn New Baddie
		if (spawnTimer.stopwatch(spawnDelay)) {
			if (baddieCount < 15) {
				genBadKat("BK", baddieIndex);
			}
		}
		
		// Pan Camera Into Position
		float camSpacingX = 1280.0f * 0.001375f;
		if (panToX < camSpacingX) {
			panToX += Gdx.graphics.getDeltaTime();
		} else if (panToX > -camSpacingX) {
			panToX -= Gdx.graphics.getDeltaTime();
		}
		if (panToY < 2.0f) {
			panToY += Gdx.graphics.getDeltaTime();
		}
		if (po != null) {
			tiledLevel.getCamera().position.x = Math.round(PIXELS_PER_METER
					* (po.getPosition().x + panToX));
			tiledLevel.getCamera().position.y = Math.round(PIXELS_PER_METER
					* (po.getPosition().y + panToY));
		} else if (poIsDead) {
			for (int l = 0; l < deadList.size(); l++) {
				IAmDead deadPlayer = deadList.get(l);
				if (deadPlayer.getName().matches("PlayerOne")) {
					tiledLevel.getCamera().position.x = Math.round(PIXELS_PER_METER
							* (panToX) + deadPlayer.getPosX());
					tiledLevel.getCamera().position.y = Math.round(PIXELS_PER_METER
							* (panToY) + deadPlayer.getPosY());
				}
			}
		}

		// Keep camera to the bounds of the map
		int screenWidth = 1280;
		if (tiledLevel.getCamera().position.x < screenWidth / 2) {
			tiledLevel.getCamera().position.x = screenWidth / 2;
		}
		if (tiledLevel.getCamera().position.x >= tiledLevel.getWidth() - screenWidth / 2) {
			tiledLevel.getCamera().position.x = tiledLevel.getWidth() - screenWidth / 2;
		}
		int screenHeight = 720;
		if (tiledLevel.getCamera().position.y < screenHeight / 2) {
			tiledLevel.getCamera().position.y = screenHeight / 2;
		}
		if (tiledLevel.getCamera().position.y >= tiledLevel.getHeight() - screenHeight / 2) {
			tiledLevel.getCamera().position.y = tiledLevel.getHeight() - screenHeight / 2;
		}
        tiledLevel.getCamera().update();
	}
	
	public void updateTheDead() {
		IAmDead deadGuy;
		for (int i = 0; i < deadList.size(); i++) {
			deadGuy = deadList.get(i);
			if (deadGuy.getType() == "BadKat" && deadGuy.getPosY() < 0 - 40) {
				deadList.remove(i);
				i--;
			} else if (deadGuy.getName() == "PlayerOne" && deadGuy.getPosY() > 1220) {
				poIsDead = false;
				poLives--;
				deadList.remove(i);
				i--;
			} else {
				deadGuy.update();
			}
			//System.out.println(deadList.size());
		}
	}
	
	public void animateSmallCrates() {
        // Animate Small Crates
        // Row 1
		for (int i = 0; i < smallCrateData1.length; i++) {
        	smallCrateData1[i].animate();
        }
		// Row 2
		for (int i = 0; i < smallCrateData2.length; i++) {
        	smallCrateData2[i].animate();
        }
		// Row 3
		for (int i = 0; i < smallCrateData3.length; i++) {
        	smallCrateData3[i].animate();
        }
		// Row 4
		for (int i = 0; i < smallCrateData4.length; i++) {
        	smallCrateData4[i].animate();
        }
		// Row 5
		for (int i = 0; i < smallCrateData5.length; i++) {
        	smallCrateData5[i].animate();
        }
		// Row 6
		for (int i = 0; i < smallCrateData6.length; i++) {
        	smallCrateData6[i].animate();
        }
		// Row 7
		for (int i = 0; i < smallCrateData7.length; i++) {
        	smallCrateData7[i].animate();
        }
		// Row 8
		for (int i = 0; i < smallCrateData8.length; i++) {
			smallCrateData8[i].animate();
        }
	}
	
	public void calcBaddieFlips() {
		for (Iterator<Body> iter = world.getBodies(); iter.hasNext();) {
			Body body = iter.next();
			if(body!=null) {
				boolean fresh = true;
				if (body.getUserData().getClass().toString().matches("class com.rabrant.bashnbump.BaddieData")) {
					BaddieData baddieData = (BaddieData) body.getUserData();
					if (baddieData.getBody() != null) {
						fresh = true;
						for (int k = 0; k < smallCrateData1.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData1[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData1[k].getX()
									&& smallCrateData1[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData1[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData1[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData1[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						fresh = true;
						for (int k = 0; k < smallCrateData2.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData2[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData2[k].getX()
									&& smallCrateData2[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData2[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData2[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData2[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						fresh = true;
						for (int k = 0; k < smallCrateData3.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData3[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData3[k].getX()
									&& smallCrateData3[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData3[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData3[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData3[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						fresh = true;
						for (int k = 0; k < smallCrateData4.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData4[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData4[k].getX()
									&& smallCrateData4[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData4[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData4[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData4[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						fresh = true;
						for (int k = 0; k < smallCrateData5.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData5[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData5[k].getX()
									&& smallCrateData5[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData5[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData5[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData5[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						fresh = true;
						for (int k = 0; k < smallCrateData6.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData6[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData6[k].getX()
									&& smallCrateData6[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData6[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData6[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData6[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						fresh = true;
						for (int k = 0; k < smallCrateData7.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData7[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData7[k].getX()
									&& smallCrateData7[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData7[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData7[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData7[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						fresh = true;
						for (int k = 0; k < smallCrateData8.length; k++) {
							if ((baddieData.getBody().getPosition().x * PIXELS_PER_METER) < smallCrateData8[k].getX() + 40 
									&& (baddieData.getBody().getPosition().x * PIXELS_PER_METER) + 40 > smallCrateData8[k].getX()
									&& smallCrateData8[k].getY() + 56 > baddieData.getBody().getPosition().y * PIXELS_PER_METER
									&& smallCrateData8[k].getY() < baddieData.getBody().getPosition().y * PIXELS_PER_METER) 
							{
								if (smallCrateData8[k].isHit() && !baddieData.isHit() && fresh)
								{
									fresh = false;
									baddieData.getSleepTimer().reset();
									baddieData.setIsHit(true);
									baddieData.setVelocityX(0.0f);
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);	
								} else if (smallCrateData8[k].isHit() && baddieData.isHit() && fresh) {
									fresh = false;
									baddieData.setIsHit(false);
									if (Math.random() < 0.5f) {
										baddieData.setVelocityX(baddieData.getSpeed());
									} else {
										baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
									}
									baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
								}
							}
						}
						if (baddieData.isHit() && baddieData.getSleepTimer().stopwatch(baddieData.getSleepTimerMs())) {
							baddieData.setIsHit(false);
							if (Math.random() < 0.5f) {
								baddieData.setVelocityX(baddieData.getSpeed());
							} else {
								baddieData.setVelocityX(baddieData.getSpeed() * -1.0f);
							}
							baddieData.getBody().setLinearVelocity(baddieData.getVelocityX(), 2.0f);
						}
					}
				}
			}
		}
	}
		
	public void drawSmallCrates() {
		// Row 1
		for (int i = 0; i < smallCrateData1.length; i++) {
			if (smallCrateData1[i] != null) {
				batch.draw(smallCrate, smallCrateData1[i].getX(), smallCrateData1[i].getY());
			}
		}
		// Row 2
		for (int i = 0; i < smallCrateData2.length; i++) {
			if (smallCrateData2[i] != null) {
				batch.draw(smallCrate, smallCrateData2[i].getX(), smallCrateData2[i].getY());
			}
		}
		// Row 3
		for (int i = 0; i < smallCrateData3.length; i++) {
			if (smallCrateData3[i] != null) {
				batch.draw(smallCrate, smallCrateData3[i].getX(), smallCrateData3[i].getY());
			}
		}
		// Row 4
		for (int i = 0; i < smallCrateData4.length; i++) {
			if (smallCrateData4[i] != null) {
				batch.draw(smallCrate, smallCrateData4[i].getX(), smallCrateData4[i].getY());
			}
		}
		// Row 5
		for (int i = 0; i < smallCrateData5.length; i++) {
			if (smallCrateData5[i] != null) {
				batch.draw(smallCrate, smallCrateData5[i].getX(), smallCrateData5[i].getY());
			}
		}
		// Row 6
		for (int i = 0; i < smallCrateData6.length; i++) {
			if (smallCrateData6[i] != null) {
				batch.draw(smallCrate, smallCrateData6[i].getX(), smallCrateData6[i].getY());
			}
		}
		// Row 7
		for (int i = 0; i < smallCrateData7.length; i++) {
			if (smallCrateData7[i] != null) {
				batch.draw(smallCrate, smallCrateData7[i].getX(), smallCrateData7[i].getY());
			}
		}
		// Row 8
		for (int i = 0; i < smallCrateData8.length; i++) {
			if (smallCrateData8[i] != null) {
				batch.draw(smallCrate, smallCrateData8[i].getX(), smallCrateData8[i].getY());
			}
		}
	}
	
	public void loadLevel(int lvl) {
		if (lvl == 1) {
			spawnDelay = 6000;
		} else if (lvl == 2) {
			spawnDelay = 5750;
		} else if (lvl == 3) {
			spawnDelay = 5500;
		} else if (lvl == 4) {
			spawnDelay = 5250;
		} else if (lvl == 5) {
			spawnDelay = 5000;
		} else if (lvl == 6) {
			spawnDelay = 4750;
		} else if (lvl == 7) {
			spawnDelay = 4500;
		} else if (lvl == 8) {
			spawnDelay = 4250;
		} else if (lvl == 9) {
			spawnDelay = 4000;
		} else if (lvl == 10) {
			spawnDelay = 3750;
		} else if (lvl == 11) {
			spawnDelay = 3500;
		} else if (lvl == 12) {
			spawnDelay = 3250;
		} else if (lvl == 13) {
			spawnDelay = 3000;
		} else if (lvl == 14) {
			spawnDelay = 2750;
		} else if (lvl == 15) {
			spawnDelay = 2500;
		} else if (lvl == 16) {
			spawnDelay = 2250;
		} else if (lvl == 17) {
			spawnDelay = 2000;
		} else if (lvl == 18) {
			spawnDelay = 1750;
		} else if (lvl == 19) {
			spawnDelay = 1500;
		} else if (lvl == 20) {
			spawnDelay = 1250;
		} else {
			spawnDelay = 1000;
		}
		poKills = 0;
		baddieCount = 0;
		baddieIndex = 0;
		
		// camera helper for smooth scrolling on the x-axis.
		panToX = 0.0f;
		panToY = 0.0f;
		
		// tiled map stuff
		world = new World(new Vector2(0.0f, -10.0f), true);
		world.setContactListener(this);		
		
		tiledLevel = new TiledMapHelper();
		tiledLevel.setPackerDirectory("bashnbump");
		tiledLevel.loadMap("bashnbump/bashnbumplevel.tmx");
		tiledLevel.prepareCamera(1280, 720);
		
		tiledCollisionMap = new TiledMapHelper();
		tiledCollisionMap.setPackerDirectory("bashnbump");
		tiledCollisionMap.loadMap("bashnbump/collision.tmx");
		tiledCollisionMap.loadCollisions("bashnbump/collision.tmc", world, PIXELS_PER_METER);
		tiledCollisionMap.dispose();
		
		// interactive crates (116 mobile crates)
		// Row 1
		smallCrateData1 = new CrateData[12];
		int i = 0;
		int y = 40*4;
		for (int x = 40*10; x < 40*10+40*12; x += 40) {
			smallCrateData1[i] = new CrateData();
			smallCrateData1[i].setIsHit(false);
			smallCrateData1[i].setX(x);
			smallCrateData1[i].setY(y);
			i++;
		}
		// Row 2
		smallCrateData2 = new CrateData[24];
		i = 0;
		y = 40*8;
		for (int x = 40; x < 40+40*12; x += 40) {
			smallCrateData2[i] = new CrateData();
			smallCrateData2[i].setIsHit(false);
			smallCrateData2[i].setX(x);
			smallCrateData2[i].setY(y);
			i++;
		}
		for (int x = 40*19; x < 40*19+40*12; x += 40) {
			smallCrateData2[i] = new CrateData();
			smallCrateData2[i].setIsHit(false);
			smallCrateData2[i].setX(x);
			smallCrateData2[i].setY(y);
			i++;
		}
		// Row 3
		smallCrateData3 = new CrateData[10];
		i = 0;
		y = 40*12;
		for (int x = 40; x < 40+40*5; x += 40) {
			smallCrateData3[i] = new CrateData();
			smallCrateData3[i].setIsHit(false);
			smallCrateData3[i].setX(x);
			smallCrateData3[i].setY(y);
			i++;
		}
		for (int x = 40*26; x < 40*26+40*5; x += 40) {
			smallCrateData3[i] = new CrateData();
			smallCrateData3[i].setIsHit(false);
			smallCrateData3[i].setX(x);
			smallCrateData3[i].setY(y);
			i++;
		}
		// Row 4
		smallCrateData4 = new CrateData[14];
		i = 0;
		y = 40*13;
		for (int x = 40*9; x < 40*9+40*14; x += 40) {
			smallCrateData4[i] = new CrateData();
			smallCrateData4[i].setIsHit(false);
			smallCrateData4[i].setX(x);
			smallCrateData4[i].setY(y);
			i++;
		}
		// Row 5
		smallCrateData5 = new CrateData[8];
		i = 0;
		y = 40*17;
		for (int x = 40*10; x < 40*10+40*4; x += 40) {
			smallCrateData5[i] = new CrateData();
			smallCrateData5[i].setIsHit(false);
			smallCrateData5[i].setX(x);
			smallCrateData5[i].setY(y);
			i++;
		}
		for (int x = 40*18; x < 40*18+40*4; x += 40) {
			smallCrateData5[i] = new CrateData();
			smallCrateData5[i].setIsHit(false);
			smallCrateData5[i].setX(x);
			smallCrateData5[i].setY(y);
			i++;
		}
		// Row 6
		smallCrateData6 = new CrateData[14];
		i = 0;
		y = 40*18;
		for (int x = 40*3; x < 40*3+40*7; x += 40) {
			smallCrateData6[i] = new CrateData();
			smallCrateData6[i].setIsHit(false);
			smallCrateData6[i].setX(x);
			smallCrateData6[i].setY(y);
			i++;
		}
		for (int x = 40*22; x < 40*22+40*7; x += 40) {
			smallCrateData6[i] = new CrateData();
			smallCrateData6[i].setIsHit(false);
			smallCrateData6[i].setX(x);
			smallCrateData6[i].setY(y);
			i++;
		}
		// Row 7
		smallCrateData7 = new CrateData[14];
		i = 0;
		y = 40*22;
		for (int x = 40*9; x < 40*9+40*14; x += 40) {
			smallCrateData7[i] = new CrateData();
			smallCrateData7[i].setIsHit(false);
			smallCrateData7[i].setX(x);
			smallCrateData7[i].setY(y);
			i++;
		}
		// Row 8
		smallCrateData8 = new CrateData[20];
		i = 0;
		y = 40*26;
		for (int x = 40*3; x < 40*3+40*10; x += 40) {
			smallCrateData8[i] = new CrateData();
			smallCrateData8[i].setIsHit(false);
			smallCrateData8[i].setX(x);
			smallCrateData8[i].setY(y);
			i++;
		}
		for (int x = 40*19; x < 40*19+40*10; x += 40) {
			smallCrateData8[i] = new CrateData();
			smallCrateData8[i].setIsHit(false);
			smallCrateData8[i].setX(x);
			smallCrateData8[i].setY(y);
			i++;
		}
	}
	
	
	
	public void genBadKat(String name, int spawnPoint) {
		// Baddie Peach Kat:
		BaddieData baddieData2 = new BaddieData();
		if (spawnPoint < 1 || spawnPoint > 4) {
			double rndSpawn = Math.random() * 4.0f;
			if (rndSpawn <= 1.0f) {
				spawnPoint = 1;
			} else if (rndSpawn <= 2.0f) {
				spawnPoint = 2;
			} else if (rndSpawn <= 3.0f) {
				spawnPoint = 3;
			} else if (spawnPoint <= 4.0f) {
				spawnPoint = 4;
			} else {
				spawnPoint = 2;
			}
		}
		baddieData2.setIsOnGround(false);
		baddieData2.setSleepTimerMs(7000);
		baddieData2.setAnimatedTexture(new AnimatedTexture(badKat, 4, 3));
		baddieData2.setType("BadKat");
		baddieData2.setName(name+baddieIndex);
		baddieData2.setSpeed(0.4f);
		baddieData2.setFrameRateMs(160);
		
		AnimationSet hitEast = new AnimationSet();
		hitEast.setName("hitEast");
		hitEast.setFrameRateMs(-1);
		Vector<Integer> framesEastH = new Vector<Integer>();
		framesEastH.add(7);
		hitEast.setFrames(framesEastH);
		baddieData2.getAnimatedTexture().addAnimationSet(hitEast);
		
		AnimationSet walkEast = new AnimationSet();
		walkEast.setName("walkEast");
		walkEast.setFrameRateMs(baddieData2.getFrameRateMs());
		Vector<Integer> framesEastW = new Vector<Integer>();
		framesEastW.add(5);
		framesEastW.add(4);
		framesEastW.add(5);
		framesEastW.add(6);
		walkEast.setFrames(framesEastW);
		baddieData2.getAnimatedTexture().addAnimationSet(walkEast);
		
		AnimationSet hitWest = new AnimationSet();
		hitWest.setName("hitWest");
		hitWest.setFrameRateMs(-1);
		Vector<Integer> framesWestH = new Vector<Integer>();
		framesWestH.add(3);
		hitWest.setFrames(framesWestH);
		baddieData2.getAnimatedTexture().addAnimationSet(hitWest);
		
		AnimationSet walkWest = new AnimationSet();
		walkWest.setName("walkWest");
		walkWest.setFrameRateMs(baddieData2.getFrameRateMs());
		Vector<Integer> framesWestW = new Vector<Integer>();
		framesWestW.add(1);
		framesWestW.add(0);
		framesWestW.add(1);
		framesWestW.add(2);
		walkWest.setFrames(framesWestW);
		baddieData2.getAnimatedTexture().addAnimationSet(walkWest);
		
		AnimationSet dead = new AnimationSet();
		dead.setName("dead");
		dead.setFrameRateMs(-1);
		Vector<Integer> framesDead = new Vector<Integer>();
		framesDead.add(8);
		dead.setFrames(framesDead);
		baddieData2.getAnimatedTexture().addAnimationSet(dead);
		
		
		BodyDef bkBodyDef = new BodyDef();
		bkBodyDef.type = BodyDef.BodyType.DynamicBody;
		
		switch(spawnPoint) {
		case 1: // Lower Left
			bkBodyDef.position.set(0.5f, 10.5f); // Bottom Left Corner of Sprite
			baddieData2.setVelocityX(baddieData2.getSpeed());
			baddieData2.getAnimatedTexture().setCurrentAnimation("walkEast");
			break;
		case 2: // Lower Right
			bkBodyDef.position.set(15.0f, 10.5f); // Bottom Left Corner of Sprite
			baddieData2.setVelocityX(baddieData2.getSpeed() * -1.0f);
			baddieData2.getAnimatedTexture().setCurrentAnimation("walkWest");
			break;
		case 3: // Top Left
			bkBodyDef.position.set(0.5f, 14.5f); // Bottom Left Corner of Sprite
			baddieData2.setVelocityX(baddieData2.getSpeed());
			baddieData2.getAnimatedTexture().setCurrentAnimation("walkEast");
			break;
		case 4: // Top Right
			bkBodyDef.position.set(15.0f, 14.5f); // Bottom Left Corner of Sprite
			baddieData2.setVelocityX(baddieData2.getSpeed() * -1.0f);
			baddieData2.getAnimatedTexture().setCurrentAnimation("walkWest");
			break;
		default: // Lower Right
			bkBodyDef.position.set(15.0f, 10.5f); // Bottom Left Corner of Sprite
			baddieData2.setVelocityX(baddieData2.getSpeed() * -1.0f);
			baddieData2.getAnimatedTexture().setCurrentAnimation("walkWest");
			break;
		}

		baddieData2.setBody(world.createBody(bkBodyDef));
		baddieData2.getBody().setFixedRotation(true);
		baddieData2.getBody().setUserData(baddieData2);
		baddieData2.getBody().setLinearVelocity(baddieData2.getVelocityX(), 0.0f);

		// Bad Kat Collision Shapes
		// Head
		PolygonShape bkHeadShape = new PolygonShape();
		Vector2[] bkHeadPoints = new Vector2[3];
		bkHeadPoints[0] = new Vector2(37.0f / PIXELS_PER_METER, 38.0f / PIXELS_PER_METER);
		bkHeadPoints[1] = new Vector2(3.0f / PIXELS_PER_METER, 38.0f / PIXELS_PER_METER);
		bkHeadPoints[2] = new Vector2(20.0f / PIXELS_PER_METER, 20.0f / PIXELS_PER_METER);
		bkHeadShape.set(bkHeadPoints);
		
		// Left Side
		PolygonShape bkLeftShape = new PolygonShape();
		Vector2[] bkLeftPoints = new Vector2[3];
		bkLeftPoints[0] = new Vector2(2.0f / PIXELS_PER_METER, 37.0f / PIXELS_PER_METER);
		bkLeftPoints[1] = new Vector2(2.0f / PIXELS_PER_METER, 1.0f / PIXELS_PER_METER);
		bkLeftPoints[2] = new Vector2(20.0f / PIXELS_PER_METER, 20.0f / PIXELS_PER_METER);
		bkLeftShape.set(bkLeftPoints);
		
		// Feet
		PolygonShape bkFeetShape = new PolygonShape();
		Vector2[] bkFeetPoints = new Vector2[3];
		bkFeetPoints[0] = new Vector2(20.0f / PIXELS_PER_METER, 20.0f / PIXELS_PER_METER);
		bkFeetPoints[1] = new Vector2(3.0f / PIXELS_PER_METER, 0.0f / PIXELS_PER_METER);
		bkFeetPoints[2] = new Vector2(37.0f / PIXELS_PER_METER, 0.0f / PIXELS_PER_METER);
		bkFeetShape.set(bkFeetPoints);
		
		// Right Side
		PolygonShape bkRightShape = new PolygonShape();
		Vector2[] bkRightPoints = new Vector2[3];
		bkRightPoints[0] = new Vector2(38.0f / PIXELS_PER_METER, 37.0f / PIXELS_PER_METER);
		bkRightPoints[1] = new Vector2(20.0f / PIXELS_PER_METER, 20.0f / PIXELS_PER_METER);
		bkRightPoints[2] = new Vector2(38.0f / PIXELS_PER_METER, 1.0f / PIXELS_PER_METER);
		bkRightShape.set(bkRightPoints);

		// Bad Kat Fixtures
		// Head
		FixtureDef bkHeadFixDef = new FixtureDef();
		bkHeadFixDef.shape = bkHeadShape;
		bkHeadFixDef.density = 0.25f;
		bkHeadFixDef.friction = 0.0f;
		baddieData2.getBody().createFixture(bkHeadFixDef);
		bkHeadShape.dispose();
		
		// Left Side
		FixtureDef bkLeftFixDef = new FixtureDef();
		bkLeftFixDef.shape = bkLeftShape;
		bkLeftFixDef.density = 0.25f;
		bkLeftFixDef.friction = 0.0f;
		baddieData2.getBody().createFixture(bkLeftFixDef);
		bkLeftShape.dispose();
		
		// Feet
		FixtureDef bkFeetFixDef = new FixtureDef();
		bkFeetFixDef.shape = bkFeetShape;
		bkFeetFixDef.density = 0.75f;
		bkFeetFixDef.friction = 5.0f;
		baddieData2.getBody().createFixture(bkFeetFixDef);
		bkFeetShape.dispose();
		
		// Right Side
		FixtureDef bkRightFixDef = new FixtureDef();
		bkRightFixDef.shape = bkRightShape;
		bkRightFixDef.density = 0.25f;
		bkRightFixDef.friction = 0.0f;
		baddieData2.getBody().createFixture(bkRightFixDef);
		bkRightShape.dispose();
		
		baddieCount++;
		baddieIndex++;
	}
	
	public void loadPlayer() {
		// Player One:
		poIsOnGround = true;
		poHeadIsBumped = false;
		poIsDead = false;
		poFrameRate = 320;
		poVelocityX = 0.0f;
		if (poAnimated != null) {
			poAnimated = null;
		}
		poAnimated = new AnimatedTexture(playerOne, 4, 3);
		
		AnimationSet standEast = new AnimationSet();
		standEast.setName("standEast");
		standEast.setFrameRateMs(-1);
		Vector<Integer> framesEastS = new Vector<Integer>();
		framesEastS.add(7);
		standEast.setFrames(framesEastS);
		poAnimated.addAnimationSet(standEast);
		
		AnimationSet walkEast = new AnimationSet();
		walkEast.setName("walkEast");
		walkEast.setFrameRateMs(poFrameRate);
		Vector<Integer> framesEastW = new Vector<Integer>();
		framesEastW.add(7);
		framesEastW.add(6);
		framesEastW.add(7);
		framesEastW.add(5);
		walkEast.setFrames(framesEastW);
		poAnimated.addAnimationSet(walkEast);
		
		AnimationSet jumpEast = new AnimationSet();
		jumpEast.setName("jumpEast");
		jumpEast.setFrameRateMs(-1);
		Vector<Integer> framesEastJ = new Vector<Integer>();
		framesEastJ.add(4);
		jumpEast.setFrames(framesEastJ);
		poAnimated.addAnimationSet(jumpEast);
		
		AnimationSet standWest = new AnimationSet();
		standWest.setName("standWest");
		standWest.setFrameRateMs(-1);
		Vector<Integer> framesWestS = new Vector<Integer>();
		framesWestS.add(0);
		standWest.setFrames(framesWestS);
		poAnimated.addAnimationSet(standWest);
		
		AnimationSet walkWest = new AnimationSet();
		walkWest.setName("walkWest");
		walkWest.setFrameRateMs(poFrameRate);
		Vector<Integer> framesWestW = new Vector<Integer>();
		framesWestW.add(0);
		framesWestW.add(1);
		framesWestW.add(0);
		framesWestW.add(2);
		walkWest.setFrames(framesWestW);
		poAnimated.addAnimationSet(walkWest);
		
		AnimationSet jumpWest = new AnimationSet();
		jumpWest.setName("jumpWest");
		jumpWest.setFrameRateMs(-1);
		Vector<Integer> framesWestJ = new Vector<Integer>();
		framesWestJ.add(3);
		jumpWest.setFrames(framesWestJ);
		poAnimated.addAnimationSet(jumpWest);
		
		AnimationSet dead = new AnimationSet();
		dead.setName("dead");
		dead.setFrameRateMs(-1);
		Vector<Integer> framesDead = new Vector<Integer>();
		framesDead.add(8);
		dead.setFrames(framesDead);
		poAnimated.addAnimationSet(dead);
		
		poAnimated.setCurrentAnimation("standEast");
		
		
		BodyDef poBodyDef = new BodyDef();
		poBodyDef.type = BodyDef.BodyType.DynamicBody;
		poBodyDef.position.set(1.0f, 2.0f); // Bottom Left Corner of Sprite

		if (po != null) {
			po = null;
		}
		po = world.createBody(poBodyDef);
		po.setFixedRotation(true);
		po.setUserData("PlayerOne");

		// Player One Collision Shapes
		// Head
		PolygonShape poHeadShape = new PolygonShape();
		Vector2[] poHeadPoints = new Vector2[3];
		poHeadPoints[0] = new Vector2(39.0f / PIXELS_PER_METER, 60.0f / PIXELS_PER_METER);
		poHeadPoints[1] = new Vector2(1.0f / PIXELS_PER_METER, 60.0f / PIXELS_PER_METER);
		poHeadPoints[2] = new Vector2(20.0f / PIXELS_PER_METER, 30.0f / PIXELS_PER_METER);
		poHeadShape.set(poHeadPoints);
		
		// Left Side
		PolygonShape poLeftShape = new PolygonShape();
		Vector2[] poLeftPoints = new Vector2[3];
		poLeftPoints[0] = new Vector2(0.0f / PIXELS_PER_METER, 59.0f / PIXELS_PER_METER);
		poLeftPoints[1] = new Vector2(0.0f / PIXELS_PER_METER, 3.0f / PIXELS_PER_METER);
		poLeftPoints[2] = new Vector2(20.0f / PIXELS_PER_METER, 30.0f / PIXELS_PER_METER);
		poLeftShape.set(poLeftPoints);
		
		// Feet
		PolygonShape poFeetShape = new PolygonShape();
		Vector2[] poFeetPoints = new Vector2[3];
		poFeetPoints[0] = new Vector2(20.0f / PIXELS_PER_METER, 30.0f / PIXELS_PER_METER);
		poFeetPoints[1] = new Vector2(1.0f / PIXELS_PER_METER, 2.0f / PIXELS_PER_METER);
		poFeetPoints[2] = new Vector2(39.0f / PIXELS_PER_METER, 2.0f / PIXELS_PER_METER);
		poFeetShape.set(poFeetPoints);
		
		// Right Side
		PolygonShape poRightShape = new PolygonShape();
		Vector2[] poRightPoints = new Vector2[3];
		poRightPoints[0] = new Vector2(40.0f / PIXELS_PER_METER, 59.0f / PIXELS_PER_METER);
		poRightPoints[1] = new Vector2(20.0f / PIXELS_PER_METER, 30.0f / PIXELS_PER_METER);
		poRightPoints[2] = new Vector2(40.0f / PIXELS_PER_METER, 3.0f / PIXELS_PER_METER);
		poRightShape.set(poRightPoints);

		// Player One Fixtures
		// Head
		FixtureDef poHeadFixDef = new FixtureDef();
		poHeadFixDef.shape = poHeadShape;
		poHeadFixDef.density = 0.25f;
		poHeadFixDef.friction = 0.0f;
		po.createFixture(poHeadFixDef);
		poHeadShape.dispose();
		
		// Left Side
		FixtureDef poLeftFixDef = new FixtureDef();
		poLeftFixDef.shape = poLeftShape;
		poLeftFixDef.density = 0.25f;
		poLeftFixDef.friction = 0.0f;
		po.createFixture(poLeftFixDef);
		poLeftShape.dispose();
		
		// Feet
		FixtureDef poFeetFixDef = new FixtureDef();
		poFeetFixDef.shape = poFeetShape;
		poFeetFixDef.density = 0.65f;
		poFeetFixDef.friction = 5.0f;
		po.createFixture(poFeetFixDef);
		poFeetShape.dispose();
		
		// Right Side
		FixtureDef poRightFixDef = new FixtureDef();
		poRightFixDef.shape = poRightShape;
		poRightFixDef.density = 0.25f;
		poRightFixDef.friction = 0.0f;
		po.createFixture(poRightFixDef);
		poRightShape.dispose();
	}
	
	public void loadInput(int whichPlatform) {
		if (whichPlatform == PLATFORM_DESKTOP) {
			controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
			if (controllers.length == 0) {
				System.out.println("Found no controllers.");
				System.exit(0);
			}
		} else if (whichPlatform == PLATFORM_ANDROID) {
			// STUB
		} else if (whichPlatform == PLATFORM_OUYA) {
			// STUB
		}
	}
	
	public void loadScoreboard() {
		scoreboard = new Texture(Gdx.files.internal("bashnbump/bnbScoreboard.png"));
	}
	
	//public void loadDebug() {
		//debugRenderer = new Box2DDebugRenderer();
	//}
}
