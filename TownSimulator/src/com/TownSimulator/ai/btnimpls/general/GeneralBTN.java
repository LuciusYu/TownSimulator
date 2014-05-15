package com.TownSimulator.ai.btnimpls.general;

import com.TownSimulator.ai.behaviortree.SelectorNode;
import com.TownSimulator.entity.Man;

public class GeneralBTN extends SelectorNode{
	public Man man;
	
	public GeneralBTN(Man man)
	{
		this.man = man;
		
		init();
	}
	
	private void init()
	{

		this.addNode( new FindHomeBTN(man) )
			.addNode( new FindFoodBTN(man) )
			.addNode(new GotoSchoolBTN(man))
			.addNode(new SeeADoctorBTN(man));

	}
	
}