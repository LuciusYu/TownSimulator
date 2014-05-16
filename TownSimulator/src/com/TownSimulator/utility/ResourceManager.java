package com.TownSimulator.utility;


import java.util.HashMap;
import java.util.Iterator;

import com.TownSimulator.driver.Driver;
import com.TownSimulator.driver.DriverListenerBaseImpl;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;


public class ResourceManager extends Singleton{
	private HashMap<String, Texture>		mTexturesMap;
	private HashMap<Integer, BitmapFont> 	mFontsMap;
	private HashMap<String, Sound>          mSoundsMap;
	private FreeTypeFontGenerator			mFontGenerator;
	private AssetManager					mAssetsManager;
	
	public ResourceManager()
	{
		mTexturesMap = new HashMap<String, Texture>();
		mSoundsMap=new HashMap<String, Sound>();
		mAssetsManager = new AssetManager();
		mFontsMap = new HashMap<Integer, BitmapFont>();
		mFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/visitor1.ttf"));
		preLoadSounds();
		
		Driver.getInstance(Driver.class).addListener(new DriverListenerBaseImpl()
		{

			@Override
			public void dispose() {
				mAssetsManager.clear();
				mTexturesMap.clear();
				
				Iterator<Integer> it_font = mFontsMap.keySet().iterator();
				while(it_font.hasNext())
				{
					mFontsMap.get(it_font.next()).dispose();
				}
				
				
				Iterator<String> it_sound = mSoundsMap.keySet().iterator();
				while(it_sound.hasNext())
				{
					mSoundsMap.get(it_sound.next()).dispose();
				}
				mSoundsMap.clear();
				mFontGenerator.dispose();
				
				mInstaceMap.clear();
			}

			@Override
			public void resume() {
				while( !mAssetsManager.update() );
			}
			
		});
		
	}
	
//	public void loadResource()
//	{
//		mTexturesMap = new HashMap<String, Texture>();
//		mSoundsMap=new HashMap<String, Sound>();
//		mAssetsManager = new AssetManager();
//		mFontsMap = new HashMap<Integer, BitmapFont>();
//		mFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/visitor1.ttf"));
//	}
	
	public void reloadResources()
	{
		while( !mAssetsManager.update() );

	}
	
	public Sprite createSprite(String textureName)
	{
		if( textureName == null)
			return null;
		
		if(!mTexturesMap.containsKey(textureName))
			loadTexture(textureName);
			
		return new Sprite(mTexturesMap.get(textureName));
	}
	
	private void preLoadSounds(){
		//loadSound("voice/sound/cave3.wav");
		//loadSound("voice/sound/game.mp3");
		//loadSound("voice/sound/rain.mp3");
		loadSound("voice/sound/loop01.wav");
	}
	
	private void loadSound(String soundName){
		mAssetsManager.load(soundName, Sound.class);
		mAssetsManager.finishLoading();		
		mSoundsMap.put(soundName, mAssetsManager.get(soundName, Sound.class));
	}
	
	public Sound getSound(String soundName){
		if(!mSoundsMap.containsKey(soundName)){
			loadSound(soundName);
		}
		return mSoundsMap.get(soundName);
	}
	
	
	private void loadTexture(String textureName)
	{
		System.out.println("Load " + textureName);
		mAssetsManager.load("data/" + textureName + ".png", Texture.class);
		mAssetsManager.finishLoading();
		mTexturesMap.put(textureName, mAssetsManager.get("data/" + textureName + ".png", Texture.class));
	}
	
	public TextureRegion createTextureRegion(String textureName)
	{
		if(!mTexturesMap.containsKey(textureName))
			loadTexture(textureName);
			
		return new TextureRegion(mTexturesMap.get(textureName));
	}
	
	@SuppressWarnings("deprecation")
	public BitmapFont getFont(int size)
	{
		if(mFontsMap.containsKey(size))
			return mFontsMap.get(size);
		else
		{
			//mAssetsManager.setLoader(type, loader);
			mFontsMap.put(size, mFontGenerator.generateFont(size));
			return mFontsMap.get(size);
		}
	}
	
//	@SuppressWarnings("deprecation")
//	public BitmapFont getFontNoManaged(int size)
//	{
//		return mFontGenerator.generateFont(size);
//	}
	
}
