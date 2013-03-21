package com.rabrant.bashnbump;

import com.rabrant.bashnbump.AnimatedTexture;
import com.rabrant.bashnbump.SimpleTimer;

public class IAmDead {
	private AnimatedTexture iadAnimated;
	private SimpleTimer iadTimer;
	private long iadRateMs;
	private long iadTimerStart;
	private float iadPosX;
	private float iadPosY;
	private String iadType;
	private String iadName;
	
	public IAmDead() {
		iadAnimated = null;
		iadTimer = new SimpleTimer();
		iadRateMs = 8;
		iadTimerStart = iadTimer.getTimer();
		iadPosX = 0.0f;
		iadPosY = 0.0f;
		iadName = "";
	}
	
	public void setAnimatedTexture(AnimatedTexture animated) {
		iadAnimated = animated;
	}
	public AnimatedTexture getAnimatedTexture() {
		return iadAnimated;
	}
	
	public void setPosX(float posX) {
		iadPosX = posX;
	}
	public float getPosX() {
		return iadPosX;
	}
	
	public void setPosY(float posY) {
		iadPosY = posY;
	}
	public float getPosY() {
		return iadPosY;
	}
	
	public void setType(String type) {
		iadType = type;
	}
	public String getType() {
		return iadType;
	}
	
	public void setName(String name) {
		iadName = name;
	}
	public String getName() {
		return iadName;
	}
	
	public void update() {
		iadAnimated.animate("dead");
		if (iadTimer.getTimer() >  iadTimerStart + iadRateMs) {
        	// reset the timer
        	iadTimerStart = iadTimer.getTimer();
        	if (iadType == "BadKat") {
        		iadPosY -= 6;
        	} else if (iadName == "PlayerOne") {
        		iadPosY += 2;
        	}
        }
	}
}
