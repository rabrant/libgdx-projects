package com.rabrant.pickinsticks;

import java.util.Vector;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import com.rabrant.pickinsticks.AnimatedTexture;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;


public class PickinSticks implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Texture grassnstick;
	private TextureRegion grass;
	private TextureRegion stick;
	
	private boolean isStickDrawn;
	private Rectangle stickRect;

	private AnimatedTexture baurnAnimated;
	private Rectangle baurnRect;
	
	private int score;
	private BitmapFontCache titleCache;
	private BitmapFontCache flavCache;
	private BitmapFontCache scoreCache;
	private BitmapFont font;
	private CharSequence str;
	
	private Sound pickSound;
	private Music bgMusic;
	
	private Controller[] controllers;
	private float deltaX;
	private float deltaY;
	private boolean enableController;


	@Override
	public void create() {
		controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		if (controllers.length == 0) {
			enableController = false;
		} else {
			enableController = true;
		}
		deltaX = 0;
		deltaY = 0;
		
		// load the drop sound effect and the rain background "music"
		pickSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		
		// start the play back of the background music immediately
		bgMusic.setLooping(true);
		bgMusic.play();
		
		score = 0;
		font = new BitmapFont(Gdx.files.internal("arial.fnt"),
		         Gdx.files.internal("arial.png"), false);
		font.setScale(0.5f);
		titleCache = new BitmapFontCache(font, true);
		str = "Pickin' Sticks";
		titleCache.setText(str, 0, 720);
		titleCache.setColor(0.6f, 0.3f, 0.1f, 1.0f);
		flavCache = new BitmapFontCache(font, true);
		str = "Nose Picker";
		flavCache.setText(str, 0, 0);
		flavCache.setText(str, 1280 / 2 - flavCache.getBounds().width / 2, 720);
		flavCache.setColor(0.6f, 0.3f, 0.1f, 1.0f);
		scoreCache = new BitmapFontCache(font, true);
		str = "Score: " + score;
		scoreCache.setText(str, 0, 0);
		scoreCache.setText(str, 1280 - scoreCache.getBounds().width, 720);
		scoreCache.setColor(0.6f, 0.3f, 0.1f, 1.0f);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1280, 720);
		batch = new SpriteBatch();
		
		grassnstick = new Texture(Gdx.files.internal("pickinsticks.png"));
		grass = new TextureRegion(grassnstick, 0, 0, 32, 32);
		stick = new TextureRegion(grassnstick, 32, 0, 32, 32);
		stickRect = new Rectangle();
		isStickDrawn = false;
		
		baurnAnimated = new AnimatedTexture(new Texture(Gdx.files.internal("baurn.png")), 3, 4);
		baurnRect = new Rectangle();
		baurnRect.set(1280 / 2 - 16, 720 / 2 - 24, 32, 48);
		
		AnimationSet moveLeft = new AnimationSet();
		moveLeft.setName("west");
		Vector<Integer> leftFrames =  new Vector<Integer>();
		leftFrames.add(7);
		leftFrames.add(6);
		leftFrames.add(7);
		leftFrames.add(8);
		moveLeft.setFrames(leftFrames);
		moveLeft.setFrameRateMs(200);
		baurnAnimated.addAnimationSet(moveLeft);
		
		AnimationSet moveRight = new AnimationSet();
		moveRight.setName("east");
		Vector<Integer> rightFrames =  new Vector<Integer>();
		rightFrames.add(10);
		rightFrames.add(9);
		rightFrames.add(10);
		rightFrames.add(11);
		moveRight.setFrames(rightFrames);
		moveRight.setFrameRateMs(200);
		baurnAnimated.addAnimationSet(moveRight);
		
		AnimationSet moveUp = new AnimationSet();
		moveUp.setName("north");
		Vector<Integer> upFrames =  new Vector<Integer>();
		upFrames.add(4);
		upFrames.add(3);
		upFrames.add(4);
		upFrames.add(5);
		moveUp.setFrames(upFrames);
		moveUp.setFrameRateMs(200);
		baurnAnimated.addAnimationSet(moveUp);
		
		AnimationSet moveDown = new AnimationSet();
		moveDown.setName("south");
		Vector<Integer> downFrames =  new Vector<Integer>();
		downFrames.add(1);
		downFrames.add(0);
		downFrames.add(1);
		downFrames.add(2);
		moveDown.setFrames(downFrames);
		moveDown.setFrameRateMs(200);
		baurnAnimated.addAnimationSet(moveDown);
		baurnAnimated.setCurrentAnimation("south");
	}

	@Override
	public void dispose() {
		batch.dispose();
		grassnstick.dispose();
	}

	@Override
	public void render() {
		// Input:
		int delta = Math.round(160 * Gdx.graphics.getDeltaTime());
		// Gamepad:
		if (enableController) {
			/* Remember to poll each one */
			controllers[0].poll();
			/* Get the controllers event queue */
			EventQueue queue = controllers[0].getEventQueue();
			/* Create an event object for the underlying plugin to populate */
			Event event = new Event();
			/* For each object in the queue */
			while (queue.getNextEvent(event)) {
				/*
				 * Create a strug buffer and put in it, the controller name,
				 * the time stamp of the event, the name of the component
				 * that changed and the new value.
				 * 
				 * Note that the timestamp is a relative thing, not
				 * absolute, we can tell what order events happened in
				 * across controllers this way. We can not use it to tell
				 * exactly *when* an event happened just the order.
				 */
				StringBuffer buffer = new StringBuffer(
						controllers[0].getName());
				buffer.append(" at ");
				buffer.append(event.getNanos()).append(", ");
				Component comp = event.getComponent();
				buffer.append(comp.getName()).append(" changed to ");
				float value = event.getValue();
				
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
	
				// Moving!
				if (comp.getName() == "x") {
					deltaX = event.getValue();
				}
				if (comp.getName() == "y") {
					deltaY = event.getValue();
				}
				// Controller Debug output:
				//System.out.println(buffer.toString());
			}
			// Apply Movement!
			if (deltaX < -0.025) {
				baurnRect.x += deltaX * Gdx.graphics.getDeltaTime() * 160;
				if (baurnRect.x < 0) baurnRect.x = 0;
			}
			if (deltaX > 0.025) {
				baurnRect.x += deltaX * Gdx.graphics.getDeltaTime() * 160;
				if (baurnRect.x > 1248) baurnRect.x = 1248;
			}
			if (deltaY < -0.025) {
				baurnRect.y -= deltaY * Gdx.graphics.getDeltaTime() * 160;
				if (baurnRect.y > 656) baurnRect.y = 656;
			}
			if (deltaY > 0.025) {
				baurnRect.y -= deltaY * Gdx.graphics.getDeltaTime() * 160;
				if (baurnRect.y < 0) baurnRect.y = 0;
			}
			// Animation!
			if (Math.abs(deltaX) > Math.abs(deltaY)) {
				if (deltaX > -0.025 && deltaX < 0.025) {
					// Do nothing
				} else {
					if (deltaX < 0) {
						baurnAnimated.animate("west");
					} else {
						baurnAnimated.animate("east");
					}
				}
			} else {
				if (deltaY > -0.025 && deltaY < 0.025) {
					// Do nothing
				} else {
					if (deltaY < 0) {
						baurnAnimated.animate("north");
					} else {
						baurnAnimated.animate("south");
					}
				}
			}
		}
		
		// Keyboard:
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			baurnAnimated.animate("west");
			baurnRect.x -= delta;
			if (baurnRect.x < 0) baurnRect.x = 0;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			baurnAnimated.animate("east");
			baurnRect.x += delta;
			if (baurnRect.x > 1248) baurnRect.x = 1248;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			baurnAnimated.animate("south");
			baurnRect.y -= delta;
			if (baurnRect.y < 0) baurnRect.y = 0;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			baurnAnimated.animate("north");
			baurnRect.y += delta;
			if (baurnRect.y > 656) baurnRect.y = 656;
		}
		
		// Logic:
		baurnRect.set(baurnRect.x, baurnRect.y, 32, 48);
		if (baurnRect.contains(stickRect.x + stickRect.width / 2, stickRect.y + stickRect.height / 2)) {
			pickSound.play();
			isStickDrawn = false;
			score++;
			
			str = "Score: " + score;
			scoreCache.setText(str, 0, 0);
			scoreCache.setText(str, 1280 - scoreCache.getBounds().width, 720);
			
			if (score == 10) {
				str = "Bundle Snatcher";
				flavCache.setText(str, 0, 0);
				flavCache.setText(str, 1280 / 2 - flavCache.getBounds().width / 2, 720);
			}
			if (score == 25) {
				str = "Half-Bricked Goon";
				flavCache.setText(str, 0, 0);
				flavCache.setText(str, 1280 / 2 - flavCache.getBounds().width / 2, 720);
			}
			if (score == 50) {
				str = "Stick Addict";
				flavCache.setText(str, 0, 0);
				flavCache.setText(str, 1280 / 2 - flavCache.getBounds().width / 2, 720);
			}
			if (score >= 75) {
				str = "You Should Go To Rehab";
				flavCache.setText(str, 0, 0);
				flavCache.setText(str, 1280 / 2 - flavCache.getBounds().width / 2, 720);
			}
			//System.out.println("Colliding");
		}
		
		// Render:
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			// Draw Grass:
			for (int x = 0; x <= 40; x++)
			{
				for (int y = 0; y <= 21; y++)
				{
					batch.draw(grass, x*32, y*32);
				}
			}
			drawStick();
			batch.draw(baurnAnimated.getRegion(), baurnRect.x, baurnRect.y);
			titleCache.draw(batch);
			flavCache.draw(batch);
			scoreCache.draw(batch);
			str = "fps: " + Gdx.graphics.getFramesPerSecond();
			font.draw(batch, str, 0, 16);
		batch.end();
		//System.out.println("Test: " + titleCache.getBounds().width);
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

	private void drawStick() {
		if (!isStickDrawn) {
			stickRect.set((int)(Math.random()*1248), (int)(Math.random()*664), 32, 32);
			isStickDrawn = true;
		}
		batch.draw(stick, stickRect.x, stickRect.y);
	}
}
