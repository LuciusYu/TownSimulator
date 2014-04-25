package com.TownSimulator;


import com.TownSimulator.ui.UIManager;
import com.TownSimulator.utility.ResourceManager;
import com.TownSimulator.utility.Settings;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;

public class Driver implements ApplicationListener{
	private GameManager mGameMgr;
	private UIManager   mUiMgr;
	
	@Override
	public void create() {
		Settings.refreshUnit();
		ResourceManager.loadResource();
		
		mUiMgr   = new UIManager();
		mGameMgr = GameManager.getInstance();
		mUiMgr.setGameMgr(mGameMgr);
		mGameMgr.setUIMgr(mUiMgr);
		InputMultiplexer inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(mUiMgr.getInputProcessor());
		inputMulti.addProcessor(mGameMgr.getInputProcessor());
		Gdx.input.setInputProcessor(inputMulti);
		System.out.println("Create");
		
	}

	@Override
	public void dispose() {
		mGameMgr.dispose();
		mUiMgr.dispose();
		System.out.println("Dispose");
	}

	@Override
	public void render() {	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float timeDelta = Gdx.graphics.getDeltaTime();
		mGameMgr.render( timeDelta );
		mUiMgr.render( timeDelta );
	}

	@Override
	public void resize(int width, int height) {
		mUiMgr.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
		mGameMgr.resume();
		mUiMgr.resume();
	}

}