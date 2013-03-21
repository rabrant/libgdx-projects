package com.rabrant.bashnbump;

import com.rabrant.bashnbump.SimpleTimer;

public class CrateData {
	private SimpleTimer frameTimer;
	private long frameRateMs;
	private long frameStart;
	private int myIndex;
	private int myX;
	private int myY;
	private boolean myIsHit;
	private boolean myIsAnimated;
	private boolean myIsMoving;
	private int myMoveValue;

	public CrateData() {
		frameTimer = new SimpleTimer();
		frameRateMs = 8;
		frameStart = frameTimer.getTimer();
		myIndex = -1;
		myX = -1;
		myY = -1;
		myIsHit = false;
		myIsMoving = false;
		myMoveValue = 0;
	}
	
	public void setIndex(int index) {
		myIndex = index;
	}
	public int getIndex() {
		return myIndex;
	}
	
	public void setX(int x) {
		myX = x;
	}
	public int getX() {
		return myX;
	}
	
	public void setY(int y) {
		myY = y;
	}
	public int getY() {
		return myY;
	}
	
	public void setIsHit(boolean isHit) {
		myIsHit = isHit;
	}
	public boolean isHit() {
		return myIsHit;
	}
	
	public void setIsAnimated(boolean isAnimated) {
		myIsAnimated = isAnimated;
	}
	public boolean isAnimated() {
		return myIsAnimated;
	}
	
	public void setIsMoving(boolean isMoving) {
		myIsMoving = isMoving;
	}
	public boolean isMoving() {
		return myIsMoving;
	}
	
	public void setMoveValue(int moveValue) {
		myMoveValue = moveValue;
	}
	public int getMoveValue() {
		return myMoveValue;
	}
	
	public boolean animate()
	{
        // update frame based on time
        if (frameTimer.getTimer() > frameStart + frameRateMs) {
        	// reset animation timer
        	frameStart = frameTimer.getTimer();
        	if (this.isMoving()) {
                if (this.isAnimated()) {
                	if (this.getMoveValue() > 0) {
                		this.setIsHit(false);
                	}
                    if (this.getMoveValue() > 14)
                    {
                        this.setIsAnimated(false);
                    }
                    this.setMoveValue(this.getMoveValue()+2);
                    this.setY(this.getY()+2);
                }
                else
                {
                    if (this.getMoveValue() == 0)
                    {
                        this.setIsMoving(false);
                    }
                    else
                    {
                        this.setMoveValue(this.getMoveValue()-2);
                        this.setY(this.getY()-2);
                    }
                }
            }
        }
        return false;
	}
}
