package com.me.mypenguins.test;

import com.badlogic.gdx.math.Vector2;
import com.me.mypenguins.MainActivity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

public class BasicContextTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Activity activity;
	Vector2 slopeBodyPos;
	Vector2 slopeBodyDim;
	
	@SuppressWarnings("deprecation")
	public BasicContextTest() {
		super("com.me.mypenguins", MainActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.activity = this.getActivity();
	}
	
	public void testStart() throws Exception {
		assertNotNull(this.activity);
		assertNotNull(this.activity.getApplication());
	}
	
	public void testOpenEpicFish() throws Exception {
		assertNotNull(this.activity.getAssets().open("epicFish.json"));
		assertNotNull(this.activity.getAssets().open("epicfish.png"));
	}
	
	public void testOpenPenguin() throws Exception {
		assertNotNull(this.activity.getAssets().open("penFig.json"));
		assertNotNull(this.activity.getAssets().open("penguin_side.png"));
	}
	
	public void testOpenRamp() throws Exception {
		assertNotNull(this.activity.getAssets().open("ramp.json"));
		assertNotNull(this.activity.getAssets().open("ramp.png"));
		assertNotNull(this.activity.getAssets().open("slope.png"));
	}
	
	public void testOpenworld() throws Exception {
		assertNotNull(this.activity.getAssets().open("data/world/level1/level.tmx"));
		assertNotNull(this.activity.getAssets().open("data/collisions.txt"));
	}
	
	public void testOpenpacker() throws Exception {
		assertNotNull(this.activity.getAssets().open("data/packer/level.png"));
		assertNotNull(this.activity.getAssets().open("data/packer/level packfile"));
	}
}
