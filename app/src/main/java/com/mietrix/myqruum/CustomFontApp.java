package com.mietrix.myqruum;

import android.app.Application;

public class CustomFontApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/sofiapromedium.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/sofiapromedium.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/sofiapromedium.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/sofiapromedium.ttf");
    }
}