package com.TownSimulator.ui;


import com.TownSimulator.GameManager;
import com.TownSimulator.ui.StartScreenUI.StartUIListener;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

public class UIManager implements Screen, InputProcessor, StartUIListener{
	private StartScreenUI 	mStartUI;
	private GameScreenUI	mGameUI;
	private ScreenUIBase	mCurScreenUI;
	private InputProcessor 	mCurInputProcessor;
	private GameManager		mGameMgr = null;
	
	public UIManager()
	{
		mStartUI = new StartScreenUI();
		mGameUI = new GameScreenUI();
		mCurScreenUI = mStartUI;
		mCurInputProcessor = mStartUI.getInputProcessor();
		mStartUI.setListner(this);
	}
	
	public GameScreenUI getGameUI()
	{
		return mGameUI;
	}
	
	public InputProcessor getInputProcessor()
	{
		return this;
	}
	
	public void setGameMgr(GameManager mgr)
	{
		mGameMgr = mgr;
	}
	
	@Override
	public void startGame() {
		if( mGameMgr != null )
		{
			mGameMgr.startGame();
			mCurScreenUI = mGameUI;
			mCurInputProcessor = mGameUI.getInputProcessor();
		}
	}

	@Override
	public void render(float delta) {
//		Label fpsLabel = (Label) stage.getRoot().findActor("fps_label");
//		fpsLabel.setText("FPS " + Gdx.graphics.getFramesPerSecond());
//		//fpsLabel.setX(Gdx.graphics.getWidth() - fpsLabel.getTextBounds().width);
//		
//		stage.act(delta);
//		stage.draw();
//		if (!mGameMgr.isGameStart()) {
//			mStartUI.update(delta);
//			mStartUI.draw();
//		}
		if( mCurScreenUI != null)
		{
			mCurScreenUI.update(delta);
			mCurScreenUI.draw();
		}
	}

	@Override
	public void resize(int width, int height) {
		//stage.setViewport(width, height, true);
	}

	@Override
	public void show() {
		mCurScreenUI.show();
	}

	@Override
	public void hide() {
		mCurScreenUI.hide();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		mStartUI.resume();
	}

	@Override
	public void dispose() {
		mStartUI.dispose();
		mGameUI.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return mCurInputProcessor.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return mCurInputProcessor.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return mCurInputProcessor.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}


}