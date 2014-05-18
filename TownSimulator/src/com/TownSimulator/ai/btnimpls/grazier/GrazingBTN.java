package com.TownSimulator.ai.btnimpls.grazier;

import com.TownSimulator.ai.behaviortree.ActionNode;
import com.TownSimulator.ai.behaviortree.ExecuteResult;
import com.TownSimulator.ai.behaviortree.SequenceNode;
import com.TownSimulator.entity.Man;
import com.TownSimulator.entity.ManAnimeType;
import com.TownSimulator.entity.ManStateType;
import com.TownSimulator.utility.quadtree.QuadTreeType;

public class GrazingBTN extends SequenceNode{
	private static final long serialVersionUID = 833590892672891622L;
	private Man man;
	
	public GrazingBTN(Man man)
	{
		this.man = man;
		
		init();
	}
	
	private void init()
	{
		ActionNode moveToRanch = new ActionNode() {
			private static final long serialVersionUID = 6917962253492059743L;

			@Override
			public ExecuteResult execute(float deltaTime) {
				float destX = man.getInfo().workingBuilding.getAABBWorld(QuadTreeType.COLLISION).getCenterX();
				float destY = man.getInfo().workingBuilding.getAABBWorld(QuadTreeType.COLLISION).getCenterY();
				man.setMoveDestination(destX, destY);
				man.getInfo().manStates.add( ManStateType.Working );
				
				if( !man.move(deltaTime) )
				{
					man.getInfo().animeType = ManAnimeType.STANDING;
				}
				else
					man.getInfo().animeType = ManAnimeType.MOVE;
				
				return ExecuteResult.TRUE;
			}
		};
		
		this.addNode(moveToRanch);
	}
}
