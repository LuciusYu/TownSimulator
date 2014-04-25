package com.TownSimulator.broker;

import com.TownSimulator.entity.Building;
import com.TownSimulator.entity.Entity;
import com.TownSimulator.entity.EntityListener;
import com.TownSimulator.scene.CameraController;
import com.TownSimulator.scene.QuadTreeManageble;
import com.TownSimulator.scene.QuadTreeType;
import com.TownSimulator.scene.SceneManager;
import com.TownSimulator.scene.CameraController.CameraListener;
import com.TownSimulator.ui.BuildAdjustUI;
import com.TownSimulator.ui.BuildAdjustUI.BuildAjustUIListener;
import com.TownSimulator.utility.AxisAlignedBoundingBox;
import com.TownSimulator.utility.Settings;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BuildBroker implements EntityListener, CameraListener{
	private static 	BuildBroker mInstance;
	private 		Building	mCurBuilding;
	private 		boolean		mbBuildingMovable = false;
	private			float		mMoveDeltaX = 0.0f;
	private			float		mMoveDeltaY = 0.0f;
	private			BuildAdjustUI mBuildUI;
	
	public static synchronized BuildBroker getInstance()
	{
		if(mInstance == null)
			mInstance = new BuildBroker();
		return mInstance;
	}
	
	public boolean isIdle()
	{
		return mCurBuilding == null;
	}
	
	public void setBuilding(Building building)
	{
		mCurBuilding = building;
		
		mCurBuilding.setListener(this);
	}
	
	public void setBuildAjustUI(BuildAdjustUI ui)
	{
		if(ui == null)
			return;
		
		mBuildUI = ui;
		mBuildUI.setVisible(true);
		mBuildUI.setListener(new BuildAjustUIListener() {
			
			@Override
			public void confirm() {
				SceneManager.getInstance().setDrawGrid(false);
				mCurBuilding.setListener(null);
				mCurBuilding = null;
				mBuildUI.setListener(null);
				mBuildUI.setVisible(false);
				mBuildUI = null;
				CameraController.getInstance().removeListner(BuildBroker.this);
			}
			
			@Override
			public void cancel() {
				SceneManager.getInstance().setDrawGrid(false);
				SceneManager.getInstance().removeBuilding(mCurBuilding);
				mCurBuilding.setListener(null);
				mCurBuilding = null;
				mBuildUI.setListener(null);
				mBuildUI.setVisible(false);
				mBuildUI = null;
				CameraController.getInstance().removeListner(BuildBroker.this);
			}
		});
		updateUIPos();
		
		CameraController.getInstance().addListner(this);
	}
	
	private void updateUIPos()
	{
		if(mBuildUI == null)
			return;
		
		AxisAlignedBoundingBox drawAABB = mCurBuilding.getBoundingBox(QuadTreeType.DRAW);
		Vector3 pos = new Vector3(drawAABB.maxX, drawAABB.maxY, 0.0f);
		CameraController.getInstance().worldToScreen(pos);
		mBuildUI.setPosition(pos.x, pos.y - mBuildUI.getHeight());
	}

	@Override
	public void objBeTouchDown(Entity obj) {
		//System.out.println("Touch Down");
		CameraController.getInstance().setMovable(false);
		mbBuildingMovable = true;
		mMoveDeltaX = 0.0f;
		mMoveDeltaY = 0.0f;
	}

	@Override
	public void objBeTouchUp(Entity obj) {
		//System.out.println("Touch Up");
		CameraController.getInstance().setMovable(true);
		mbBuildingMovable = false;
		mMoveDeltaX = 0.0f;
		mMoveDeltaY = 0.0f;
		//SceneManager.getInstance().setDrawGrid(false);
	}

	@Override
	public void objBeTouchDragged(Entity obj, float x, float y, float deltaX, float deltaY) {
		//System.out.println("Touch Dragged " + x + " " + y + " " + deltaX + " " + deltaY);
		if(mbBuildingMovable)
		{
			mMoveDeltaX += deltaX;
			mMoveDeltaY += deltaY;
			
			int gridDeltaX = (int) (mMoveDeltaX / Settings.UNIT);
			int gridDeltaY = (int) (mMoveDeltaY / Settings.UNIT);
			
			if(Math.abs(gridDeltaX) != 0 || Math.abs(gridDeltaY) != 0)
			{
				//System.out.println("Move " + mMoveDeltaX + " " + mMoveDeltaY );
				AxisAlignedBoundingBox destAABB = new AxisAlignedBoundingBox();
				AxisAlignedBoundingBox collsionAABB = mCurBuilding.getBoundingBox(QuadTreeType.COLLISION);
				float moveDeltaX = gridDeltaX * Settings.UNIT;
				float moveDeltaY = gridDeltaY * Settings.UNIT;
				destAABB.minX = collsionAABB.minX + moveDeltaX;
				destAABB.minY = collsionAABB.minY + moveDeltaY;
				destAABB.maxX = collsionAABB.maxX + moveDeltaX;
				destAABB.maxY = collsionAABB.maxY + moveDeltaY;
				
				Array<QuadTreeManageble> excluded = new Array<QuadTreeManageble>();
				excluded.add(mCurBuilding);
				if( !SceneManager.getInstance().getCollisionDetector().detectIntersection(destAABB, null, excluded) )
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