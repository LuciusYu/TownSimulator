package com.TownSimulator.entity.building;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.TownSimulator.ai.behaviortree.BehaviorTreeNode;
import com.TownSimulator.ai.btnimpls.factoryworker.FactoryWorkerBTN;
import com.TownSimulator.driver.Driver;
import com.TownSimulator.driver.DriverListener;
import com.TownSimulator.driver.DriverListenerBaseImpl;
import com.TownSimulator.entity.EntityInfoCollector;
import com.TownSimulator.entity.JobType;
import com.TownSimulator.entity.Man;
import com.TownSimulator.entity.ResourceInfoCollector;
import com.TownSimulator.entity.ResourceType;
import com.TownSimulator.utility.ResourceManager;
import com.TownSimulator.utility.Settings;
import com.TownSimulator.utility.TipsBillborad;
import com.TownSimulator.utility.quadtree.QuadTreeType;
import com.badlogic.gdx.graphics.Color;

public class CoatFactory extends WorkableBuilding{
	private static final long serialVersionUID = 5636509488927087926L;
	private static final int 	MAX_JOB_CNT = 4;
	private static final float 	PRODUCE_INTERVAL_TIME = 20.0f;
	private static final int 	PRODUCE_FUR_PER_COAT = 4;
	private static final int 	PRODUCE_COAT_AMOUNT = 20;
	private float produceAccum = 0.0f;
//	private transient WorkableWithTipsWindow	workTipsWindow;
	private DriverListener driverListener;
	
	public CoatFactory() {
		super("building_coat_factory", BuildingType.CoatFactory, JobType.FactoryWorker);
		
		driverListener = new DriverListenerBaseImpl()
		{
			private static final long serialVersionUID = -8628748620963794602L;

			@Override
			public void update(float deltaTime) {
//				if(ResourceInfoCollector.getInstance(ResourceInfoCollector.class).getResourceAmount(ResourceType.RS_FUR) <= 0)
//				{
//					workTipsWindow.setWarningMsg("No 'Fur' Resource ( Ranch )");
//					return;
//				}
//				else if(EntityInfoCollector.getInstance(EntityInfoCollector.class)
//						.getBuildings(BuildingType.POWER_STATION).size() <= 0)
//				{
//					workTipsWindow.setWarningMsg("Need 'Power Station'");
//					return;
//				}
//				else
//					workTipsWindow.setWarningMsg("");
				if(isWorking())
					produce(deltaTime);
			}
		};
	}
	
	

	@Override
	public boolean isWorking() {
		return super.isWorking() && ResourceInfoCollector.getInstance(ResourceInfoCollector.class).getResourceAmount(ResourceType.Fur) > 0;
	}

	@Override
	protected String getWarningMessage() {
		if(ResourceInfoCollector.getInstance(ResourceInfoCollector.class).getResourceAmount(ResourceType.Fur) <= 0)
		{
			return ResourceManager.stringMap.get("building_warning_noFur");
		}
		else
			return super.getWarningMessage();
	}

	@Override
	protected int getMaxJobCnt() {
		return MAX_JOB_CNT;
	}

	@Override
	protected BehaviorTreeNode createBehavior(Man man) {
		return new FactoryWorkerBTN(man);
	}
	
	private void decreFurResource(int amount)
	{
		int remainAmount = amount;
		for (Building building : EntityInfoCollector.getInstance(EntityInfoCollector.class).getBuildings(BuildingType.Warehouse)) {
//			if(building.getType() == BuildingType.WAREHOUSE)
//			{
				Warehouse warehouse = (Warehouse)building;
				int stored = warehouse.getStoredResourceAmount(ResourceType.Fur);
				if(stored > 0)
				{
					int decre = Math.min(remainAmount, stored);
					warehouse.addStoredResource(ResourceType.Fur, -decre, false);
					float originX = this.getAABBWorld(QuadTreeType.DRAW).getCenterX();
					float originY = this.getAABBWorld(QuadTreeType.DRAW).maxY + Settings.UNIT * 0.6f + TipsBillborad.getTipsHeight();
					Color color = Color.RED;
					TipsBillborad.showTips(
							ResourceType.Fur + " - " + decre, originX, originY, color);
					remainAmount -= decre;
					
					if(remainAmount <= 0)
						return;
//				}
			}
		}
	}
	
	private void produce(float deltaTime)
	{
		int furAmount = ResourceInfoCollector.getInstance(ResourceInfoCollector.class)
							.getResourceAmount(ResourceType.Fur);
		if( furAmount <= PRODUCE_FUR_PER_COAT )
			return;
		
		produceAccum += deltaTime;
		while(produceAccum >= PRODUCE_INTERVAL_TIME)
		{
			produceAccum -= PRODUCE_INTERVAL_TIME;
			Warehouse warehouse = EntityInfoCollector.getInstance(EntityInfoCollector.class)
									.findNearestWareHouse(mPosXWorld, mPosYWorld);
			
			int amount = 0;
			for (Man man : workers) {
				amount += man.getInfo().workEfficency * PRODUCE_COAT_AMOUNT;
			}
			int produceAmount = Math.min(furAmount / PRODUCE_FUR_PER_COAT, amount);
			
			if(produceAmount <= 0 )
				continue;
			
			decreFurResource(produceAmount * PRODUCE_FUR_PER_COAT);
			warehouse.addStoredResource(ResourceType.Coat, produceAmount, false);
			float originX = this.getAABBWorld(QuadTreeType.DRAW).getCenterX();
			float originY = this.getAABBWorld(QuadTreeType.DRAW).maxY + Settings.UNIT * 0.4f;
			Color color = Color.WHITE;
			TipsBillborad.showTips(
					ResourceType.Coat + " + " + produceAmount,
					originX,
					originY, color);
		}
	}
	
	
	
	@Override
	public void destroy() {
		super.destroy();
		Driver.getInstance(Driver.class).removeListener(driverListener);
	}

	@Override
	public void setState(State state) {
		super.setState(state);
		if( state == Building.State.Constructed )
		{
			Driver.getInstance(Driver.class).addListener(driverListener);
		}
	}

//	@Override
//	protected WorkableViewWindow createWorkableWindow() {
//		workTipsWindow = UIManager.getInstance(UIManager.class).getGameUI()
//							.createWorkableWithTipsWindow(buildingType, getMaxJobCnt());
//		return workTipsWindow;
//	}
	
	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();
		setState(buildingState);
	}
}
