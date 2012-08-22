package com.rabrant.pickinsticks;

import java.util.Vector;
import com.rabrant.pickinsticks.SimpleTimer;

public class AnimationSet {
	private Vector<Integer> myFrames;
	private String myName;
	
	private SimpleTimer frameTimer;
	private int myCurFrame;
	private long myFrameRateMs;
	private long myFrameStart;
	
	
	public AnimationSet() {
		frameTimer = new SimpleTimer();
		myCurFrame = 0;
		myFrameRateMs = 0;
		myFrameStart = frameTimer.getTimer();
	}
	
	public void setName(String name) {
		myName = name;
	}
	public String getName() {
		return myName;
	}
	
	public void setFrames(Vector<Integer> frames) {
		myFrames = frames;
	}
	public Vector<Integer> getFrames() {
		return myFrames;
	}
	
	public void setCurFrame(int curFrame) {
		myCurFrame = curFrame;
	    if (myCurFrame >= myFrames.size())
	    {
	        myCurFrame = 0; // Loop Back To Begining.
	    }
	}
	public int getFrameIndex() {
		return myFrames.get(myCurFrame);
	}
	
	public void setFrameRateMs(long frameRateMs) {
		myFrameRateMs = frameRateMs;
	}
	public long getFrameRateMs() {
		return myFrameRateMs;
	}
	
	public int getNumFrames() {
		return myFrames.size();
	}
	
	public boolean nextFrame() {
	    boolean hasRestarted = false;
	    ++myCurFrame;
	    if (myCurFrame >= myFrames.size())
	    {
	        myCurFrame = 0; // Loop Back To Begining.
	        hasRestarted = true;
	    }

	    return hasRestarted;
	}
	
	public boolean animate()
	{
        // update frame based on time
        if (frameTimer.getTimer() >  myFrameStart + myFrameRateMs) {
        	// reset animation timer
        	myFrameStart = frameTimer.getTimer();
            return nextFrame();
        }
        return false;
	}
}
