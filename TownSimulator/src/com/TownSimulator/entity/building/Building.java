package com.TownSimulator.entity.building;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.TownSimulator.ai.btnimpls.construct.ConstructionProject;
import com.TownSimulator.entity.Entity;
import com.TownSimulator.entity.Resource;
import com.TownSimulator.entity.ResourceType;
import com.TownSimulator.ui.UIManager;
import com.TownSimulator.ui.building.construction.ConstructionWindow;
import com.TownSimulator.ui.building.construction.ConstructionWindowListener;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Building extends Entity implements ConstructionWindowListener{
	protected List<Resource>			resources;
	protected int						constructionWork;
	protected int						finishedConstructionWork;
	private	  ConstructionProject		constructionProject;
	protected State						state;
	protected BuildingType				type;
	private   int 						numAllowedBuilder = 3;
	private   ConstructionWindow		constructionWindow;
	
	public enum State
	{
		BUILDING_UNCONFIRMED, BUILDING_PROCESS, BUILDING_FINISHED
	}
	
	public Building(String textureName, BuildingType type)
	{
		super(textureName);
		this.type = type;
		
		init();
	}
	
	public Building(Sprite sp, BuildingType type) {
		super(sp);
		this.type = type;

		init();
	}
	
	private void init()
	{
		state = State.BUILDING_UNCONFIRMED;
		resources = new LinkedList<Resource>();
		constructionWindow = UIManager.getInstance(UIManager.class).getGameUI().createConstructionWindow(resources, numAllowedBuilder);
		constructionWindow.setListener(this);
	}
	
	@Override
	public void setPositionWorld(float x, float y) {
		super.setPositionWorld(x, y);
		constructionWindow.setBuildingPosWorld(getPositionXWorld(), getPositionYWorld());
	}

	public int getMaxAllowdBuilderCnt()
	{
		return numAllowedBuilder;
	}
	
	public BuildingType getType()
	{
		return type;
	}
	
	public void setNeededConstructionResource(ResourceType type, int need)
	{
		if(resources.contains(new Resource(type))) 
			resources.get(resources.indexOf(new Resource(type))).setNeededAmount(need);
		else
			resources.add(new Resource(type, 0, need));
	}	
	
	public Set<ResourceType> getConstructionResourceTypes()
	{
		Set<ResourceType> types = new HashSet<ResourceType>();
		for(Resource res : resources) 
			types.add(res.getType());
		return types;
	}
	
	public boolean addConstructionResource(ResourceType type, int amount)
	{
		if(resources.contains(new Resource(type))) {
			resources.get(resources.indexOf(new Resource(type))).addAmount(amount);;
			return true;
		}
		return false;
	}
	
	public int getNeededConstructionResouceAmount(ResourceType type)
	{
		if(resources.contains(new Resource(type)))
			return resources.get(resources.indexOf(new Resource(type))).getAmount();
		return -1;
	}
	
	public void setNeededConstructionWork(int amount)
	{
		constructionWork = amount;
	}
	
	public int getUnfinishedConstructionWork()
	{
		return constructionWork - finishedConstructionWork;
	}

	public int getFinishedConstructionWork() 
	{
		return finishedConstructionWork;
	}

	//this method will notify constructionWindow
	public void doConstructionWork(int amount)
	{
		finishedConstructionWork = Math.min(constructionWork, finishedConstructionWork + amount);
		if(constructionWindow != null)
			constructionWindow.setProcess(getProcess());
	}
	
	public boolean isConstructionResourceSufficient()
	{
		for(Resource res : resources) {
			if(!res.isSufficient())
				return false;
		}
		return true;
	}
	
	public void setConstructionProject(ConstructionProject project)
	{
		constructionProject = project;
	}
	
	public ConstructionProject getConstructionProject()
	{
		return constructionProject;
	}
	
	public boolean isConstructionFinished()
	{
		return finishedConstructionWork >= constructionWork;
	}
	
	public float getProcess() {
		return finishedConstructionWork / (float)constructionWork;
	}
	
	
	@Override
	public boolean detectTouchDown() {
		return super.detectTouchDown() || state == State.BUILDING_PROCESS || state == State.BUILDING_FINISHED;
	}

	@Override
	public void detectTouchUp()
	{
		super.detectTouchUp();
		UIManager.getInstance(UIManager.class).getGameUI().hideAllWindow();
		if(state == State.BUILDING_PROCESS) {
			constructionWindow.setVisible(true);
		}
		//for test
//		UIManager.getInstance(UIManager.class).getGameUI().testScrollPane();		
	}
	
	public void setState(State state)
	{
		this.state = state;
	}
	
	public State getState()
	{
		return state;
	}

	//this method will notify constructionWindow
	public void addBuilder() {
		constructionWindow.addBuilder();
	}

	@Override
	public void constructionCancelled() {
		
	}

	@Override
	public void builderLimitSelected(int limit) {
		System.out.println(limit);
		constructionProject.setOpenWorkJobCnt(limit);
	}
}
