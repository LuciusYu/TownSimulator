package com.TownSimulator.entity.building;

import com.TownSimulator.entity.ResourceType;

public enum CropType {
	Wheat("crop_wheat", "Crop", ResourceType.RS_WHEAT);
	
	private String textureName;
	private String viewName;
	private ResourceType rsType; //作物对应的资源
	private CropType(String textureName, String viewName, ResourceType resourceType)
	{
		this.textureName = textureName;
		this.viewName = viewName;
		this.rsType = resourceType;
	}
	
	public String getTextureName()
	{
		return textureName;
	}
	
	public ResourceType getResourceType()
	{
		return rsType;
	}

	public String getViewName() {
		return viewName;
	}
	
	public static CropType findWithViewName(String viewName)
	{
		for (CropType type : values()) {
			if(type.getViewName().equals(viewName))
				return type;
		}
		
		return null;
	}
	
	
}
