package com.rabrant.bashnbump;

import com.badlogic.gdx.utils.TimeUtils;

public class SimpleTimer {
	private long stopwatchStart;


	public SimpleTimer() {
		reset();
	}

	public long getTimer() {
		return TimeUtils.millis();
	}
	
	public long getStopwatchStart() {
		return stopwatchStart;
	}

	public boolean stopwatch(int ms) {
        if (getTimer() > stopwatchStart + ms) {
                reset();
                return true;
        }
        else return false;
	}

	public void reset() {
		stopwatchStart = getTimer();
	}
}
