package com.rabrant.pickinsticks;

//import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Pickin' Sticks";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;
		
		//cfg.width = 1152;
		//cfg.height = 864;
		//cfg.fullscreen = true;
		//cfg.width = 1920;
		//cfg.height = 1080;
		/*DisplayMode[] modes = LwjglApplicationConfiguration.getDisplayModes(); 
		for (int i=0;i<modes.length;i++) {
			DisplayMode current = modes[i];
			System.out.println(current.width + "x" + current.height + " Bits: " + current.bitsPerPixel + " Refresh: " + current.refreshRate);
		}*/
		
		new LwjglApplication(new PickinSticks(), cfg);
	}
}
