package com.TownSimulator.ai.btnimpls.farm;

import com.TownSimulator.ai.behaviortree.ConditionNode;
import com.TownSimulator.ai.behaviortree.ExcuteResult;
import com.TownSimulator.ai.behaviortree.SelectorNode;
import com.TownSimulator.ai.behaviortree.SequenceNode;
import com.TownSimulator.entity.Man;
import com.TownSimulator.entity.World;
import com.TownSimulator.entity.World.SeasonType;
import com.TownSimulator.entity.building.FarmHouse;

public class SowBTN extends SequenceNode{
	private Man man;
	
	public SowBTN(Man man){
		this.man=man;
		init();
	}
	
	private void init(){
		
		ConditionNode isSowStart=new ConditionNode() {
			
			@Override
			public ExcuteResult execute(float deltaTime) {
				// TODO Auto-generated method stub
				FarmHouse farmHouse=(FarmHouse)man.getInfo().workingBuilding;
				if(!farmHouse.isSowStart())//未播种
					return ExcuteResult.TRUE;
				return ExcuteResult.FALSE;
			}
		};
		
		ConditionNode judgeTimeSuitable=new ConditionNode() {
			
			@Override
			public ExcuteResult execute(float deltaTime) {
				// TODO Auto-generated method stub
				if(World.getInstance(World.class).getCurSeason()==SeasonType.Winter)
					return ExcuteResult.FALSE;
				return ExcuteResult.TRUE;
			}
		};
		
		ConditionNode judgeRoomEnough=new ConditionNode() {
			
			@Override
			public ExcuteResult execute(float deltaTime) {
				// TODO Auto-generated method stub
				FarmHouse farmHouse = ((FarmHouse)man.getInfo().workingBuilding);
				if(farmHouse.isSowed()){
					return ExcuteResult.FALSE;
				}
				return ExcuteResult.TRUE;
			}
		};
		
		this.addNode(new SelectorNode().addNode(isSowStart)
				                       .addNode(new SequenceNode().addNode(judgeTimeSuitable)
				                                                  .addNode(judgeRoomEnough)))
		    .addNode(new SowExecuteBTN(man));
		
	}
}
