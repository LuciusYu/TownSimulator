package com.townSimulator.game.scene;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.townSimulator.game.objs.BaseObject;
import com.townSimulator.game.objs.Building;
import com.townSimulator.game.objs.Drawable;
import com.townSimulator.game.objs.DrawableObject;
import com.townSimulator.game.objs.MapObject;
import com.townSimulator.game.render.Renderer;
import com.townSimulator.utility.AxisAlignedBoundingBox;
import com.townSimulator.utility.ResourceManager;
import com.townSimulator.utility.Settings;

/**
 * 
 * 	维持所有DrawObject的列表
 *
 */
public class SceneManager {
	public 	static final 	int							MAP_WIDTH_UNIT  = 512;
	public 	static final 	int							MAP_HEIGHT_UNIT = 512;
	private 				Map 						mMap;
	private 				QuadTree					mCollsionDetector;
	private					QuadTree					mDrawScissor;
	private static			boolean 					mbDrawGrid = false;
	private					Array<DrawableObject> 		mGridIdleList;
	private					int							mMallocIndex = 0;
	private	static			SceneManager				instance = null;
	private					Array<BaseObject> 			mEventsListeningObjs;
	
	public SceneManager()
	{
		instance = this;
		mCollsionDetector = new QuadTree(QuadTreeType.COLLISION, 0.0f, 0.0f, MAP_WIDTH_UNIT * Settings.UNIT, MAP_HEIGHT_UNIT * Settings.UNIT);
		mDrawScissor = new QuadTree(QuadTreeType.DRAW, 0.0f, 0.0f, MAP_WIDTH_UNIT * Settings.UNIT, MAP_HEIGHT_UNIT * Settings.UNIT);
		mGridIdleList = new Array<DrawableObject>();
		mEventsListeningObjs = new Array<BaseObject>();
	}
	
	public synchronized static SceneManager getInstance() {
		if(instance == null)
			instance = new SceneManager();
		return instance;
	}
	
	
	public void initMap()
	{
		mMap = new Map(MAP_WIDTH_UNIT, MAP_HEIGHT_UNIT, 100, this);
	}
	
	public void touchDownWorldSpace(float x, float y)
	{
		mEventsListeningObjs.clear();
		
		Array<QuadTreeManageble> objs = new Array<QuadTreeManageble>();
		if(mCollsionDetector.detectIntersection(x, y, objs))
		{
			for (int i = 0; i < objs.size; i++) {
				if(objs.get(i) instanceof BaseObject)
				{
					BaseObject obj = (BaseObject)objs.get(i);
					if(obj.detectTouchDown())
						mEventsListeningObjs.add(obj);
				}
			}
		}
	}
	
	public void touchUpWorldSpace(float x, float y)
	{
		for (int i = 0; i < mEventsListeningObjs.size; i++) {
			mEventsListeningObjs.get(i).detectTouchUp();
		}
	}
	
	public void touchDraggedWorldSpace(float x, float y, float deltaX, float deltaY)
	{
		for (int i = 0; i < mEventsListeningObjs.size; i++) {
			mEventsListeningObjs.get(i).detectTouchDragged(x, y, deltaX, deltaY);
		}
	}
	
	public void addMapObjs(MapObject obj)
	{
//		mCollsionDetector.addManageble(obj);
//		mDrawScissor.addManageble(obj);
		attachCollisionDetection(obj);
		attachDrawScissor(obj);
	}
	
	public void removeMapObjs(MapObject obj)
	{
		obj.dettachQuadTree(QuadTreeType.COLLISION);
		obj.dettachQuadTree(QuadTreeType.DRAW);
	}
	
	public void addBuilding(Building building)
	{
		attachCollisionDetection(building);
		attachDrawScissor(building);
	}
	
	public void removeBuilding(Building building)
	{
		building.dettachQuadTree(QuadTreeType.COLLISION);
		building.dettachQuadTree(QuadTreeType.DRAW);
	}
	
	public boolean attachCollisionDetection(QuadTreeManageble obj)
	{
		return mCollsionDetector.addManageble(obj);
	}
	
	public boolean attachDrawScissor(QuadTreeManageble obj)
	{
		return mDrawScissor.addManageble(obj);
	}
	
	public void updateCollisionDetector(QuadTreeManageble obj)
	{
		obj.dettachQuadTree(QuadTreeType.COLLISION);
		attachCollisionDetection(obj);
	}
	
	public void updateDrawScissor(QuadTreeManageble obj)
	{
		obj.dettachQuadTree(QuadTreeType.DRAW);
		attachDrawScissor(obj);
	}
	
//	private void dettachCollisionDetection(QuadTreeManageble obj)
//	{
//		Array<QuadTreeNode> nodes = obj.get(QuadTreeType.COLLISION);
//		for (int i = 0; i < nodes.size; i++) {
//			obj
//		}
//	}
	
	public Map getMap()
	{
		return mMap;
	}
	
	public QuadTree getCollisionDetector()
	{
		return mCollsionDetector;
	}
	
	public void renderScene(Renderer renderMgr)
	{
		Array<QuadTreeManageble> renderList = new Array<QuadTreeManageble>();
		AxisAlignedBoundingBox scissor = renderMgr.getScissor();
		mDrawScissor.detectIntersection(scissor, renderList);
		for (int i = 0; i < renderList.size; i++) {
			renderMgr.draw((Drawable) renderList.get(i));
		}
		
		if(mbDrawGrid)
			renderGrid(renderMgr, scissor);
		
//		Building b = BuildHelper.getInstance().mCurBuilding;
//		if(b != null)
//		{
//			Array<QuadTreeNode> node = b.getCollisionQuadNode();
//			for (int i = 0; i < node.size; i++) {
//				Sprite sp = ResourceManager.createSprite("button_up");
//				AxisAlignedBoundingBox aabb = node.get(i).getAABB();
//				sp.setBounds(aabb.minX, aabb.minY, aabb.maxX - aabb.minX, aabb.maxY - aabb.minY);
//				Renderer.getInstance().draw(new DrawableObject(sp, -2.0f));
//			}
//		}
	}
	
	public void setDrawGrid(boolean bDrawGrid)
	{
		mbDrawGrid = bDrawGrid;
	}
	
	public DrawableObject mallocGrid()
	{
		if(mMallocIndex >= mGridIdleList.size)
		{
			Sprite gridSp = ResourceManager.createSprite("grid");
			gridSp.setSize(Settings.UNIT, Settings.UNIT);
			mGridIdleList.add( new DrawableObject(gridSp, -1.0f) );
		}
		
		return mGridIdleList.get(mMallocIndex++);
	}
	
	private void renderGrid(Renderer renderMgr, AxisAlignedBoundingBox scissor)
	{
		int l = (int)(scissor.minX / Settings.UNIT);
		int r = (int)(scissor.maxX / Settings.UNIT);
		int b = (int)(scissor.minY / Settings.UNIT);
		int u = (int)(scissor.maxY / Settings.UNIT);
		mMallocIndex = 0;
		AxisAlignedBoundingBox gridAABB = new AxisAlignedBoundingBox();
		for (float x = l * Settings.UNIT; x <= (r+1) * Settings.UNIT; x += Settings.UNIT) {
			for (float y = b * Settings.UNIT; y <= (u+1) * Settings.UNIT; y += Settings.UNIT) {
				DrawableObject obj = mallocGrid();
				obj.setDrawPosition(x, y);
				gridAABB.minX = x;
				gridAABB.minY = y;
				gridAABB.maxX = x + Settings.UNIT;
				gridAABB.maxY = y + Settings.UNIT;
				obj.setDepth(Float.MAX_VALUE);
				if(mCollsionDetector.detectIntersection(gridAABB))
					obj.getSprite().setColor(1.0f, 0.0f, 0.0f, 0.3f);
				else
					obj.getSprite().setColor(0.0f, 0.0f, 1.0f, 0.3f);
				renderMgr.draw(obj);
			}
		}
	}
}
