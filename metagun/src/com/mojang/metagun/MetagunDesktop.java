
package com.mojang.metagun;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class MetagunDesktop {
	public static void main (String[] argv) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.height = 240;
		cfg.width = 320;
		cfg.title = "Metagun";
		//new LwjglApplication(new Metagun(), "Metagun", 320, 240);
		new LwjglApplication(new Metagun(), cfg);
	}
}
