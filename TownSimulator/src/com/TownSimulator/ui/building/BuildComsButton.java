package com.TownSimulator.ui.building;

import com.TownSimulator.broker.BuildBroker;
import com.TownSimulator.entity.building.BuildingType;
import com.TownSimulator.ui.base.IconLabelButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class BuildComsButton extends IconLabelButton{
	
	public BuildComsButton(String textureName, String labelText) {
		super(textureName, labelText, (int)BuildComsUI.BUTTON_TOP_LABEL_PAD);
		setSize(BuildComsUI.BUTTON_WIDTH, BuildComsUI.BUTTON_WIDTH);
		addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				event.cancel();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				BuildBroker.getInstance(BuildBroker.class).startNewBuilding(BuildingType.LOW_COST_HOUSE);
			}
		});
	}
}
