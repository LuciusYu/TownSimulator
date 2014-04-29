package com.TownSimulator.broker;

import com.TownSimulator.ai.btnimpls.construct.ConstructionProject;
import com.TownSimulator.camera.CameraController;
import com.TownSimulator.camera.CameraListener;
import com.TownSimulator.collision.CollisionDetector;
import com.TownSimulator.entity.Entity;
import com.TownSimulator.entity.EntityFactory;
import com.TownSimulator.entity.EntityListener;
import com.TownSimulator.entity.building.Building;
import com.TownSimulator.entity.building.Building.State;
import com.TownSimulator.entity.building.BuildingType;
import com.TownSimulator.render.Renderer;
import com.TownSimulator.ui.UIManager;
import com.TownSimulator.ui.building.BuildingAdjustGroup;
import com.TownSimulator.ui.building.BuildingAdjustGroup.BuildAjustUIListener;
import com.TownSimulator.utility.AxisAlignedBoundingBox;
import com.TownSimulator.utility.Settings;
import com.TownSimulator.utility.Singleton;
import com.TownSimulator.utility.quadtree.QuadTreeManageble;
import com.TownSimulator.utility.quadtree.QuadTreeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BuildBroker extends Singleton implements EntityListener, CameraListener{
	private 		Building		mCurBuilding;
	private 		boolean			mbBuildingMovable = false;
	private			float			mMoveDeltaX = 0.0f;
	private			float			mMoveDeltaY = 0.0f;
	private			BuildingAdjustGroup 	mBuildUI;
	
	private BuildBroker()
	{
		
	}
	
	public void startNewBuilding(BuildingType type)
	{
		if(isIdle() == false)
			return;
		
		Renderer.getInstance(Renderer.class).setDrawGrid(true);
		
		Building newBuildingObject = EntityFactory.createBuilding(BuildingType.LOW_COST_HOUSE);
		int gridX = (int) (CameraController.getInstance(CameraController.class).getX() / Settings.UNIT);
		int gridY = (int) (CameraController.getInstance(CameraController.class).getY() / Settings.UNIT);
		int gridSerchSize = 0;
		AxisAlignedBoundingBox buildingCollisionAABB = newBuildingObject.getAABBWorld(QuadTreeType.COLLISION);
		float buildingWidth = buildingCollisionAABB.maxX - buildingCollisionAABB.minX;
		float buildingHeight = buildingCollisionAABB.maxY - buildingCollisionAABB.minY;
		boolean bPosFind = false;
		float posX = 0.0f;
		float posY = 0.0f;
		AxisAlignedBoundingBox aabb = new AxisAlignedBoundingBox();
		while(!bPosFind)
		{
			for (int x = gridX - gridSerchSize; x <= gridX + gridSerchSize && !bPosFind; x++) {
				for (int y = gridY - gridSerchSize; y <= gridY + gridSerchSize && !bPosFind; y++) {
					posX = x * Settings.UNIT;
					posY = y * Settings.UNIT;
					aabb.minX = posX;
					aabb.minY = posY;
					aabb.maxX = aabb.minX + buildingWidth;
					aabb.maxY = aabb.minY + buildingHeight;
					if( !CollisionDetector.getInstance(CollisionDetector.class).detect(aabb) )
						bPosFind = true;
				}
			}
			gridSerchSize ++;
			
		}
		newBuildingObject.setPositionWorld(posX, posY);
		Renderer.getInstance(Renderer.class).attachDrawScissor(newBuildingObject);
		CollisionDetector.getInstance(CollisionDetector.class).attachCollisionDetection(newBuildingObject);
		
		setBuilding(newBuildingObject);
		setBuildAjustUI(UIManager.getInstance(UIManager.class).getGameUI().getBuildAjustUI());
	}
	
	public boolean isIdle()
	{
		return mCurBuilding == null;
	}
	
	private void setBuilding(Building building)
	{
		mCurBuilding = building;
		
		mCurBuilding.setListener(this);
	}
	
	private void setBuildAjustUI(BuildingAdjustGroup ui)
	{
		if(ui == null)
			return;
		
		mBuildUI = ui;
		mBuildUI.setVisible(true);
		mBuildUI.setListener(new BuildAjustUIListener() {
			
			@Override
			public void confirm() {
				new ConstructionProject(mCurBuilding);
				mCurBuilding.setState(State.BUILDING_PROCESS);
//				int cnt = proj.getAvailableBuildJobCnt();
//				for (Man man : EntityInfoCollector.getInstance(EntityInfoCollector.class).getAllPeople()) {
//					if( man.getInfo().bIdle )
//					{
//						if(cnt-- > 0)
//							proj.addWorker(man);
//						else
//							break;
//					}
//				}
				
				Renderer.getInstance(Renderer.class).setDrawGrid(false);
				mCurBuilding.setListener(null);
				mCurBuilding = null;
				mBuildUI.setListener(null);
				mBuildUI.setVisible(false);
				mBuildUI = null;
				CameraController.getInstance(CameraController.class).removeListener(BuildBroker.this);
			}
			
			@Override
			public void cancel() {
				Renderer.getInstance(Renderer.class).setDrawGrid(false);
				CollisionDetector.getInstance(CollisionDetector.class).dettachCollisionDetection(mCurBuilding);
				Renderer.getInstance(Renderer.class).dettachDrawScissor(mCurBuilding);
				mCurBuilding.setListener(null);
				mCurBuilding = null;
				mBuildUI.setListener(null);
				mBuildUI.setVisible(false);
				mBuildUI = null;
				CameraController.getInstance(CameraController.class).removeListener(BuildBroker.this);
			}
		});
		updateUIPos();
		
		CameraController.getInstance(CameraController.class).addListener(this);
	}
	
	private void updateUIPos()
	{
		if(mBuildUI == null)
			return;
		
		AxisAlignedBoundingBox drawAABB = mCurBuilding.getAABBWorld(QuadTreeType.DRAW);
		Vector3 pos = new Vector3(drawAABB.maxX, drawAABB.maxY, 0.0f);
		CameraController.getInstance(CameraController.class).worldToScreen(pos);
		mBuildUI.setPosition(pos.x, pos.y - mBuildUI.getHeight());
	}

	@Override
	public void objBeTouchDown(Entity obj) {
		CameraController.getInstance(CameraController.class).setMovable(false);
		mbBuildingMovable = true;
		mMoveDeltaX = 0.0f;
		mMoveDeltaY = 0.0f;
	}

	@Override
	public void objBeTouchUp(Entity obj) {
		CameraController.getInstance(CameraController.class).setMovable(true);
		mbBuildingMovable = false;
		mMoveDeltaX = 0.0f;
		mMoveDeltaY = 0.0f;
	}

	@Override
	public void objBeTouchDragged(Entity obj, float x, float y, float deltaX, float deltaY) {
		if(mbBuildingMovable)
		{
			mMoveDeltaX += deltaX;
			mMoveDeltaY += deltaY;
			
			int gridDeltaX = (int) (mMoveDeltaX / Settings.UNIT);
			int gridDeltaY = (int) (mMoveDeltaY / Settings.UNIT);
			
			if(Math.abs(gridDeltaX) != 0 || Math.abs(gridDeltaY) != 0)
			{
				AxisAlignedBoundingBox destAABB = new AxisAlignedBoundingBox();
				AxisAlignedBoundingBox collsionAABB = mCurBuilding.getAABBWorld(QuadTreeType.COLLISION);
				float moveDeltaX = gridDeltaX * Settings.UNIT;
				float moveDeltaY = gridDeltaY * Settings.UNIT;
				destAABB.minX = collsionAABB.minX + moveDeltaX;
				destAABB.minY = collsionAABB.minY + moveDeltaY;
				destAABB.maxX = collsionAABB.maxX + moveDeltaX;
				destAABB.maxY = collsionAABB.maxY + moveDeltaY;
				
				Array<QuadTreeManageble> excluded = new Array<QuadTreeManageble>();
				excluded.add(mCurBuilding);
				if( !CollisionDetector.getInstance(CollisionDetector.class).detect(destAABB, null, excluded) )
				{
					mCurBuilding.translate(moveDeltaX, moveDeltaY);
					mMoveDeltaX -= gridDeltaX * Settings.UNIT; 
					mMoveDeltaY -= gridDeltaY * Settings.UNIT; 
					updateUIPos();
				}
			}
		}
	}

	@Override
	public void cameraMoved(float deltaX, float deltaY) {
		updateUIPos();
	}

	@Override
	public void cameraZoomed(float prevWidth, float prevHeight, float curWidth,
			float curHeight) {
		updateUIPos();
	}
	
}
