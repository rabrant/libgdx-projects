package com.rabrant.bashnbump;

import com.badlogic.gdx.physics.box2d.Body;
import com.rabrant.bashnbump.AnimatedTexture;
import com.rabrant.bashnbump.SimpleTimer;

public class BaddieData {
	private Body bd;
	private boolean bdIsOnGround;
	private boolean bdIsHit;
	private boolean bdIsDead;
	private SimpleTimer bdSleepTimer;
	private int bdSleepTimerMs;
	private int bdFrameRateMs;
	private float bdVelocityX;
	private float bdSpeed;
	private AnimatedTexture bdAnimated;
	private String bdName;
	private String bdType;
	private int bdValue;
	
	public BaddieData() {
		bd = null;
		bdIsOnGround = false;
		bdIsHit = false;
		bdIsDead = false;
		bdSleepTimer = new SimpleTimer();
		bdSleepTimerMs = 7000;
		bdFrameRateMs = 160;
		bdVelocityX = 0.0f;
		bdSpeed = 0.0f;
		bdAnimated = null;
		bdName = "";
		bdType = "";
		bdValue = 15;
	}
	
	public void setBody(Body body) {
		bd = body;
	}
	public Body getBody() {
		return bd;
	}
	
	public void setIsOnGround(boolean isOnGround) {
		bdIsOnGround = isOnGround;
	}
	public boolean isOnGround() {
		return bdIsOnGround;
	}
	
	public void setIsHit(boolean isHit) {
		bdIsHit = isHit;
	}
	public boolean isHit() {
		return bdIsHit;
	}
	
	public void setIsDead(boolean isDead) {
		bdIsDead = isDead;
	}
	public boolean isDead() {
		return bdIsDead;
	}
	
	public SimpleTimer getSleepTimer() {
		return bdSleepTimer;
	}
	
	public void setSleepTimerMs(int sleepTimerMs) {
		bdSleepTimerMs = sleepTimerMs;
	}
	public int getSleepTimerMs() {
		return bdSleepTimerMs;
	}
	
	public void setFrameRateMs(int frameRateMs) {
		bdFrameRateMs = frameRateMs;
	}
	public int getFrameRateMs() {
		return bdFrameRateMs;
	}
	
	public void setVelocityX(float velocityX) {
		bdVelocityX = velocityX;
	}
	public float getVelocityX() {
		return bdVelocityX;
	}
	
	public void setSpeed(float speed) {
		bdSpeed = speed;
	}
	public float getSpeed() {
		return bdSpeed;
	}
	
	public void setAnimatedTexture(AnimatedTexture animated) {
		bdAnimated = animated;
	}
	public AnimatedTexture getAnimatedTexture() {
		return bdAnimated;
	}
	
	public void setName(String name) {
		bdName = name;
	}
	public String getName() {
		return bdName;
	}
	
	public void setType(String type) {
		bdType = type;
	}
	public String getType() {
		return bdType;
	}
	
	public void setValue(int value) {
		bdValue = value;
	}
	public int getValue() {
		return bdValue;
	}
}
