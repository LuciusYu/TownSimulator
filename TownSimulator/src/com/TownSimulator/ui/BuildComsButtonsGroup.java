package com.TownSimulator.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class BuildComsButtonsGroup extends Group{
	private Array<BuildComsButton> mButtonsList;
	
	public BuildComsButtonsGroup()
	{
		mButtonsList = new Array<BuildComsButton>();
	}
	
	public void addBuild(String textureName, String labelText)
	{
		BuildComsButton button = new BuildComsButton(textureName, labelText);
		addActor(button);
		mButtonsList.add(button);
	}
	
	public void updateLayout()
	{
		setSize(mButtonsList.size * BuildComsUI.BUTTON_WIDTH + (mButtonsList.size-1) * BuildComsUI.BUTTONS_H_PAD,
				BuildComsUI.BUTTON_HEIGHT);
		Vector2 categoryPos = new Vector2(0.0f, 0.0f);
		getParent().localToStageCoordinates(categoryPos);
		float x = 	(BuildComsUI.BUTTON_WIDTH - getWidth()) * 0.5f;
		float offsetX = (Gdx.graphics.getWidth() - categoryPos.x - BuildComsUI.BUTTON_WIDTH * 0.5f) - getWidth() * 0.5f;
		offsetX = Math.min(0.0f, offsetX);
		setPosition( x + offsetX, BuildComsUI.BUTTON_HEIGHT + BuildComsUI.BUTTON_TOP_LABEL_PAD);
		
		for (int i = 0; i < mButtonsList.size; i++) {
			mButtonsList.get(i).setPosition( i * (BuildComsUI.BUTTON_WIDTH + BuildComsUI.BUTTONS_H_PAD), 0.0f);
		}
	}
	
}