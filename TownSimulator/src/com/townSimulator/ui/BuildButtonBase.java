package com.townSimulator.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.townSimulator.utility.ResourceManager;

class BuildButtonBase extends UIButton
{
	//protected static 	float 					ICON_PAD 	= BuildComsUI.BUTTON_WIDTH * 0.2f;
	protected static 	TextureRegionDrawable 	mButtonUp   = new TextureRegionDrawable(ResourceManager.findTextureRegion("button_up"));
	protected static 	TextureRegionDrawable 	mButtonDown = new TextureRegionDrawable(ResourceManager.findTextureRegion("button_down"));
	protected static	TextureRegionDrawable	mLabelBackgroud = new TextureRegionDrawable(ResourceManager.findTextureRegion("label_background"));
	protected 			TextureRegionDrawable 	mButtonImg;
	protected			Label					mTextLabel	= null;
	
	public BuildButtonBase(String textureName, String labelText)
	{
		super(null, null, null);
		setSize(BuildComsUI.BUTTON_WIDTH, BuildComsUI.BUTTON_HEIGHT);
		mButtonImg = new TextureRegionDrawable( ResourceManager.findTextureRegion(textureName) );
		
		if(labelText != null)
		{
			LabelStyle labelStyle = new LabelStyle();
			labelStyle.font = ResourceManager.getFont((int)BuildComsUI.BUTTON_TOP_LABEL_PAD);
			labelStyle.fontColor = Color.WHITE;
			mTextLabel = new Label(labelText, labelStyle)
			{
//				@Override
//				public void draw(SpriteBatch batch, float parentAlpha) {
//					batch.setColor(1.0f, 1.0f, 1.0f, parentAlpha * getColor().a);
//					mLabelBackgroud.draw(batch, mTextLabel.getX(), mTextLabel.getY(), mTextLabel.getWidth(), mTextLabel.getHeight());
//					super.draw(batch, parentAlpha);
//				}

				@Override
				public void draw(Batch batch, float parentAlpha) {
					batch.setColor(1.0f, 1.0f, 1.0f, parentAlpha * getColor().a);
					mLabelBackgroud.draw(batch, mTextLabel.getX(), mTextLabel.getY(), mTextLabel.getWidth(), mTextLabel.getHeight());
					super.draw(batch, parentAlpha);
				}
				
				
			};
			mTextLabel.setAlignment(Align.center);
			mTextLabel.setPosition( (getWidth() - mTextLabel.getWidth()) * 0.5f, getHeight());
			addActor(mTextLabel);
			System.out.println(labelText + "  " +mTextLabel.getWidth());
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(1.0f, 1.0f, 1.0f, parentAlpha * getColor().a);
		if(!isPressed())
			mButtonDown.draw(batch, getX(), getY(), getWidth(), getHeight());
		mButtonUp.draw(batch, getX(), getY(), getWidth(), getHeight());
		float iconPad = getWidth() * 0.2f;
		mButtonImg.draw(batch, 	getX() + iconPad, 				getY() + iconPad,
								getWidth() - iconPad * 2.0f, 	getHeight() - iconPad * 2.0f);
		super.draw(batch, parentAlpha);
	}
	
}
