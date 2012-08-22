package com.rabrant.pickinsticks;

import java.util.Vector;
import com.rabrant.pickinsticks.TextureSheet;
import com.rabrant.pickinsticks.AnimationSet;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	private int stickX;
	private int stickY;
	private Rectangle stickRect;

	
	private Texture baurn;
	private TextureSheet baurnSheet;
	private AnimationSet moveLeft;
	private AnimationSet moveRight;
	private AnimationSet moveUp;
	private AnimationSet moveDown;
	private int baurnX;
	private int baurnY;
	private Rectangle baurnRect;
	
	private int score;
	private BitmapFontCache titleCache;
	private BitmapFontCache flavCache;
	private BitmapFontCache scoreCache;
	private BitmapFont font;
	private CharSequence str;
	
	private Sound pickSound;
	private Music bgMusic;


	@Override
	public void create() {
		// load the drop sound effect and the rain background "music"
		pickSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		
		// start the playback of the background music immediately
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
		
		
		baurn = new Texture(Gdx.files.internal("baurn.png"));
		baurnSheet = new TextureSheet(baurn, 3, 4);
		baurnX = 1280 / 2 - 16;
		baurnY = 720 / 2 - 24;
		baurnRect = new Rectangle();
		baurnRect.set(baurnX, baurnY, 32, 48);
		
		moveLeft = new AnimationSet();
		moveLeft.setName("left");
		Vector<Integer> leftFrames =  new Vector<Integer>();
		leftFrames.add(7);
		leftFrames.add(6);
		leftFrames.add(7);
		leftFrames.add(8);
		moveLeft.setFrames(leftFrames);
		moveLeft.setFrameRateMs(160);
		
		moveRight = new AnimationSet();
		moveRight.setName("right");
		Vector<Integer> rightFrames =  new Vector<Integer>();
		rightFrames.add(10);
		rightFrames.add(9);
		rightFrames.add(10);
		rightFrames.add(11);
		moveRight.setFrames(rightFrames);
		moveRight.setFrameRateMs(160);
		
		moveUp = new AnimationSet();
		moveUp.setName("up");
		Vector<Integer> upFrames =  new Vector<Integer>();
		upFrames.add(4);
		upFrames.add(3);
		upFrames.add(4);
		upFrames.add(5);
		moveUp.setFrames(upFrames);
		moveUp.setFrameRateMs(160);
		
		moveDown = new AnimationSet();
		moveDown.setName("down");
		Vector<Integer> downFrames =  new Vector<Integer>();
		downFrames.add(1);
		downFrames.add(0);
		downFrames.add(1);
		downFrames.add(2);
		moveDown.setFrames(downFrames);
		moveDown.setFrameRateMs(160);
	}

	@Override
	public void dispose() {
		batch.dispose();
		grassnstick.dispose();
		baurn.dispose();
	}

	@Override
	public void render() {
		// Input:
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		int delta = Math.round(160 * Gdx.graphics.getDeltaTime());
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			moveLeft.animate();
			baurnSheet.setIndex(moveLeft.getFrameIndex());
			baurnX -= delta;
			if (baurnX < 0) baurnX = 0;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			moveRight.animate();
			baurnSheet.setIndex(moveRight.getFrameIndex());
			baurnX += delta;
			if (baurnX > 1248) baurnX = 1248;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			moveDown.animate();
			baurnSheet.setIndex(moveDown.getFrameIndex());
			baurnY -= delta;
			if (baurnY < 0) baurnY = 0;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			moveUp.animate();
			baurnSheet.setIndex(moveUp.getFrameIndex());
			baurnY += delta;
			if (baurnY > 656) baurnY = 656;
		}
		
		// Logic:
		baurnRect.set(baurnX, baurnY, 32, 48);
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
			batch.draw(baurnSheet.getRegion(), baurnX,  baurnY);
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
			stickX = (int)(Math.random()*1248);
			stickY = (int)(Math.random()*664);
			stickRect.set(stickX, stickY, 32, 32);
			isStickDrawn = true;
		}
		batch.draw(stick, stickX, stickY);
	}
}
