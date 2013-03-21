package com.rabrant.bashnbump;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
		// PLATFORM_DESKTOP = 0;
		// PLATFORM_ANDROID = 1;
		// PLATFORM_OUYA = 2;
        initialize(new BashNBump(1), cfg);
    }
}