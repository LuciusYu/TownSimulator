package com.TownSimulator.ai.btnimpls.farmer;

import com.TownSimulator.ai.behaviortree.ConditionNode;
import com.TownSimulator.ai.behaviortree.ExecuteResult;
import com.TownSimulator.ai.behaviortree.SelectorNode;
import com.TownSimulator.ai.behaviortree.SequenceNode;
import com.TownSimulator.entity.Man;
import com.TownSimulator.entity.SeasonType;
import com.TownSimulator.entity.World;
import com.TownSimulator.entity.building.FarmHouse;
import com.TownSimulator.entity.building.FarmLand;

public class ReapBTN extends SequenceNode{
	private static final long serialVersionUID = 1L;
	private Man man;
	
	public ReapBTN(Man man)
	{
		this.man = man;
		
		init();
	}
	
	private void init()
	{
		ConditionNode isReapStart = new ConditionNode() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public ExecuteResult execute(float deltaTime) {
				FarmHouse farmHouse = ((FarmHouse)man.getInfo().workingBuilding);
				if(farmHouse.isReapStart())
					return ExecuteResult.TRUE;
				else
					return ExecuteResult.FALSE;
			}
		};
		
		ConditionNode judgeLeftCrop = new ConditionNode() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public ExecuteResult execute(float deltaTime) {
				FarmHouse farmHouse = ((FarmHouse)man.getInfo().workingBuilding);
				float remainCropAmount = 0.0f;
				for (FarmLand land : farmHouse.getFarmLands()) {
					remainCropAmount += land.getCurCropAmount();
				}
				
				if(remainCropAmount > 0.0f)
					return ExecuteResult.TRUE;
				else
					return ExecuteResult.FALSE;
			}
		};
		
		ConditionNode judgeTime = new ConditionNode() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public ExecuteResult execute(float deltaTime) {
				if( World.getInstance(World.class).getCurSeason() == SeasonType.Winter )
					return ExecuteResult.TRUE;
				else
					return ExecuteResult.FALSE;
			}
		};
		
		ConditionNode isCropFull = new ConditionNode() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public ExecuteResult execute(float deltaTime) {
				FarmHouse farmHouse = ((FarmHouse)man.getInfo().workingBuilding);
				for (FarmLand land : farmHouse.getFarmLands()) {
					if(land.getCurCropAmount() < FarmLand.MAX_CROP_AMOUNT)
						return ExecuteResult.FALSE;
				}
				
				return ExecuteResult.TRUE;
			}
		};
		
		this.addNode( new SelectorNode().addNode(isReapStart)
										.addNode( new SequenceNode().addNode(judgeLeftCrop)
																	.addNode( new SelectorNode().addNode(judgeTime)
																								.addNode(isCropFull)
																			)
												)
					)
			.addNode(new ReapExexcuteBTN(man));
	}
	
}
