package com.TownSimulator.ui.building.view;

import com.TownSimulator.entity.building.BuildingType;
import com.TownSimulator.entity.building.School;
import com.TownSimulator.utility.ResourceManager;
import com.TownSimulator.utility.Settings;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class SchoolViewWindow extends WorkableViewWindow {
	private int currentStudentNum;
	private int maxStudentNum;
	private Label studentNumLabel;
	private float width;
	private float height;

	public SchoolViewWindow(int numAllowedWorker,int currentStudentNum) {
		super(BuildingType.SCHOOL, numAllowedWorker);
		width = UndockedWindow.LABEL_WIDTH*2 + UndockedWindow.MARGIN * 2;
		height = UndockedWindow.LABEL_HEIGHT * 2+ WorkerGroup.HEIGHT + MARGIN * 2;
		setSize(width, height);
		maxStudentNum=School.SingleSchoolStudentNum;
		this.currentStudentNum=currentStudentNum;
		
		addLabel();
		updateLayout();
	}
	
	private void addLabel(){
		LabelStyle labelStyle=new LabelStyle();
		labelStyle.font=ResourceManager.getInstance(ResourceManager.class).getFont((int) (Settings.UNIT * 0.3f));
		labelStyle.fontColor=Color.WHITE;
		studentNumLabel=new Label(getStudentLabelString(), labelStyle);
		studentNumLabel.setSize(LABEL_WIDTH, LABEL_HEIGHT);
		studentNumLabel.setPosition(MARGIN, MARGIN + WorkerGroup.HEIGHT);
		studentNumLabel.setAlignment(Align.left);
		addActor(studentNumLabel);
	}
	
	public void updateStudentNum(int changeStudentNum){
		currentStudentNum=changeStudentNum;
		currentStudentNum=Math.min(currentStudentNum, maxStudentNum);
		studentNumLabel.setText(getStudentLabelString());
	}
	
	private String getStudentLabelString(){
		return "Student:  "+currentStudentNum+"/"+maxStudentNum;
	}
}
