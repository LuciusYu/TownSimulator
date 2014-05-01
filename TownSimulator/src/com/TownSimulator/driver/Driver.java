package com.TownSimulator.driver;


import java.util.Random;

import com.TownSimulator.ai.btnimpls.FarmerBTN;
import com.TownSimulator.ai.btnimpls.farm.FarmBTN;
import com.TownSimulator.ai.btnimpls.farm.ReapBTN;
import com.TownSimulator.ai.btnimpls.farm.SowBTN;
import com.TownSimulator.camera.CameraController;
import com.TownSimulator.collision.CollisionDetector;
import com.TownSimulator.entity.EntityFactory;
import com.TownSimulator.entity.EntityInfoCollector;
import com.TownSimulator.entity.Man;
import com.TownSimulator.entity.ResourceInfoCollector;
import com.TownSimulator.entity.ResourceType;
import com.TownSimulator.entity.building.Building;
import com.TownSimulator.entity.building.Building.State;
import com.TownSimulator.entity.building.BuildingType;
import com.TownSimulator.entity.building.CropType;
import com.TownSimulator.entity.building.FarmHouse;
import com.TownSimulator.entity.building.FarmLand;
import com.TownSimulator.entity.building.Warehouse;
import com.TownSimulator.entity.building.WorkingBuilding;
import com.TownSimulator.io.InputMgr;
import com.TownSimulator.render.Renderer;
import com.TownSimulator.ui.UIManager;
import com.TownSimulator.utility.ResourceManager;
import com.TownSimulator.utility.Settings;
import com.TownSimulator.utility.SingletonPublisher;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Driver extends SingletonPublisher<DriverListener> implements ApplicationListener{
	private Driver()
	{
		
	}
	
	public void init()
	{
		Random rand = new Random(System.currentTimeMillis());
		int initPepleCnt = 1;
		float originPosX = CameraController.getInstance(CameraController.class).getX();
		float originPoxY = CameraController.getInstance(CameraController.class).getY();
		
		ResourceInfoCollector.getInstance(ResourceInfoCollector.class).addResourceAmount(ResourceType.RS_WOOD, 100);
		ResourceInfoCollector.getInstance(ResourceInfoCollector.class).addResourceAmount(ResourceType.RS_STONE, 50);
		
		Warehouse wareHouse = (Warehouse) EntityFactory.createBuilding(BuildingType.WAREHOUSE);
		wareHouse.addStoredResource(ResourceType.RS_WOOD, 100);
		wareHouse.addStoredResource(ResourceType.RS_STONE, 50);
		wareHouse.setState(State.Constructed);
		wareHouse.setPositionWorld(originPosX - 2 * Settings.UNIT, originPoxY);
		EntityInfoCollector.getInstance(EntityInfoCollector.class).addBuilding(wareHouse);
		CollisionDetector.getInstance(CollisionDetector.class).attachCollisionDetection(wareHouse);
		Renderer.getInstance(Renderer.class).attachDrawScissor(wareHouse);
		
		Building lowCostHouse = EntityFactory.createBuilding(BuildingType.LOW_COST_HOUSE);
		lowCostHouse.setState(Building.State.Constructed);
		lowCostHouse.setPositionWorld(originPosX + 2 * Settings.UNIT, originPoxY);
		EntityInfoCollector.getInstance(EntityInfoCollector.class).addBuilding(lowCostHouse);
		CollisionDetector.getInstance(CollisionDetector.class).attachCollisionDetection(lowCostHouse);
		Renderer.getInstance(Renderer.class).attachDrawScissor(lowCostHouse);
		
		FarmHouse farmHouse = (FarmHouse)EntityFactory.createBuilding(BuildingType.FARM_HOUSE);
		farmHouse.setState(Building.State.Constructed);
		farmHouse.setPositionWorld(originPosX, originPoxY - 2 * Settings.UNIT);
		EntityInfoCollector.getInstance(EntityInfoCollector.class).addBuilding(farmHouse);
		CollisionDetector.getInstance(CollisionDetector.class).attachCollisionDetection(farmHouse);
		Renderer.getInstance(Renderer.class).attachDrawScissor(farmHouse);
		
		farmHouse.setCurCropType(CropType.WHEAT);
		farmHouse.setSowed(false);
		
		for (FarmLand land : ((FarmHouse)farmHouse).getFarmLands()) {
			//land.addCropAmount(1990.0f);
			Renderer.getInstance(Renderer.class).attachDrawScissor(land);
		}
		
		for (int i = 0; i < initPepleCnt; i++) {
			float randX = (rand.nextFloat() - 0.5f) * Settings.UNIT * 6;
			float ranxY = (rand.nextFloat() - 0.5f) * Settings.UNIT * 6;
			Man man = new Man();
			man.setPositionWorld(originPosX + randX, originPoxY + ranxY);
			man.setBehavior(new FarmerBTN(man));
			((WorkingBuilding)farmHouse).addWorker(man);
			EntityInfoCollector.getInstance(EntityInfoCollector.class).addMan(man);
			
			Renderer.getInstance(Renderer.class).attachDrawScissor(man);
		}
	}
	
	@Override
	public void create() {
		Settings.refreshUnit();
		ResourceManager.getInstance(ResourceManager.class);
		InputMgr.getInstance(InputMgr.class);
		UIManager.getInstance(UIManager.class);
		CameraController.getInstance(CameraController.class);
		Renderer.getInstance(Renderer.class);
		CollisionDetector.getInstance(CollisionDetector.class);
	}

	@Override
	public void dispose() {
		for (int i = 0; i < mListeners.size; i++) {
			mListeners.get(i).dispose();
		}
	}

	@Override
	public void render() {	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Renderer.getInstance(Renderer.class).render();
		UIManager.getInstance(UIManager.class).render();
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		for (int i = 0; i < mListeners.size; i++) {
			mListeners.get(i).update(deltaTime);
		}
	}

	@Override
	public void resize(int width, int height) {
		for (int i = 0; i < mListeners.size; i++) {
			mListeners.get(i).resize(width, height);
		}
	}

	@Override
	public void pause() {
		for (int i = 0; i < mListeners.size; i++) {
			mListeners.get(i).pause();
		}
	}

	@Override
	public void resume() {
		for (int i = 0; i < mListeners.size; i++) {
			mListeners.get(i).resume();
		}
	}

}
