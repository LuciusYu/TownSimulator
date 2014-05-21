package com.TownSimulator.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.TownSimulator.utility.Singleton;

public class ResourceInfoCollector extends Singleton 
	implements Serializable
{
	private static final long serialVersionUID = -3695490079998930235L;
	private List<Resource> resourceMap;
	private int foodAmount = 0;
	private int candyAmount = 0;
	
	private ResourceInfoCollector()
	{
		resourceMap = new ArrayList<Resource>();
	}
	
	public void addCandy(int amount)
	{
		int addResult = candyAmount + amount;
		if(amount < 0)
			candyAmount = Math.max(addResult, 0);
		else
		{
			if(addResult < 0)
				candyAmount = Integer.MAX_VALUE;
			else
				candyAmount = addResult;
		}
	}
	
	public int getCandyAmount()
	{
		return candyAmount;
	}
	
	public void addResourceAmount(ResourceType type, int amount)
	{
		if(!resourceMap.contains(new Resource(type)))
		{
			resourceMap.add(new Resource(type));
		}
		resourceMap.get(resourceMap.indexOf(new Resource(type))).addAmount(amount);
		if(type.isFood())
			foodAmount += amount;
	}
	
	public int getResourceAmount(ResourceType type)
	{
		if(resourceMap.contains(new Resource(type)))
			return resourceMap.get(resourceMap.indexOf(new Resource(type))).getAmount();
		else
			return -1;
	}
	
	public int getFoodAmount()
	{
		return foodAmount;
	}
}
