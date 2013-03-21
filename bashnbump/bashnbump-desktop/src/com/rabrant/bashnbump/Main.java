package com.rabrant.bashnbump;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "bashnbump";
		cfg.useGL20 = true;
		
		cfg.width = 1280;
		cfg.height = 720;
		
		//cfg.width = 1366;
		//cfg.height = 768;
		//cfg.fullscreen = true;
		
		// PLATFORM_DESKTOP = 0;
		// PLATFORM_ANDROID = 1;
		// PLATFORM_OUYA = 2;
		new LwjglApplication(new BashNBump(0), cfg);
	}
}
