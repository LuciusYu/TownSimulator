package com.TownSimulator.render;

import java.util.ArrayList;
import java.util.List;

import com.TownSimulator.camera.CameraController;
import com.TownSimulator.collision.CollisionDetector;
import com.TownSimulator.driver.Driver;
import com.TownSimulator.driver.DriverListenerBaseImpl;
import com.TownSimulator.entity.Entity;
import com.TownSimulator.io.InputMgr;
import com.TownSimulator.io.InputMgrListenerBaseImpl;
import com.TownSimulator.map.Map;
import com.TownSimulator.utility.AxisAlignedBoundingBox;
import com.TownSimulator.utility.ResourceManager;
import com.TownSimulator.utility.Settings;
import com.TownSimulator.utility.Singleton;
import com.TownSimulator.utility.SingletonPublisher;
import com.TownSimulator.utility.quadtree.QuadTree;
import com.TownSimulator.utility.quadtree.QuadTreeManageble;
import com.TownSimulator.utility.quadtree.QuadTreeType;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Renderer extends SingletonPublisher<RendererListener>{
	private RenderBatch		   		mRenderBatch;
	private	QuadTree				mDrawScissor;
	private	List<Grid> 				mGridIdleList;
	private boolean 				mbDrawGrid = false;
	private	int						allocIndex = 0;
	private boolean					mbRenderScene = false;
	private	List<Entity> 			mEventsListeningObjs;
	private float 					touchDownXWorld;
	private float 					touchDownYWorld;
	
	private Renderer()
	{
		mRenderBatch = new RenderBatch();
		mDrawScissor = new QuadTree(QuadTreeType.DRAW, 0.0f, 0.0f, Map.MAP_WIDTH * Settings.UNIT, Map.MAP_HEIGHT * Settings.UNIT);
		mGridIdleList = new ArrayList<Grid>();
		mEventsListeningObjs = new ArrayList<Entity>();
		
		Driver.getInstance(Driver.class).addListener(new DriverListenerBaseImpl()
		{
			private static final long serialVersionUID = -2163781980586299032L;

			@Override
			public void dispose() {
				mRenderBatch.dispose();
				Singleton.clearInstanceMap();
			}
		});
		
		InputMgr.getInstance(InputMgr.class).addListener(new InputMgrListenerBaseImpl()
		{

			@Override
			public boolean touchDown(float screenX, float screenY, int pointer,
					int button) {
				Vector3 pos = new Vector3(screenX, screenY, 0.0f);
				CameraController.getInstance(CameraController.class).screenToWorld(pos);
				touchDownWorldSpace(pos.x, pos.y);
				return true;
			}

			@Override
			public boolean touchUp(float screenX, float screenY, int pointer,
					int button) {
				Vector3 pos = new Vector3(screenX, screenY, 0.0f);
				CameraController.getInstance(CameraController.class).screenToWorld(pos);
				touchUpWorldSpace(pos.x, pos.y);
				return true;
			}

			@Override
			public void touchDragged(float screenX, float screenY, float deltaX, float deltaY, int pointer) {
				Vector3 pos = new Vector3(screenX, screenY, 0.0f);
				CameraController.getInstance(CameraController.class).screenToWorld(pos);
				touchDraggedWorldSpace(	pos.x, pos.y,
										CameraController.getInstance(CameraController.class).screenToWorldDeltaX(deltaX),
										CameraController.getInstance(CameraController.class).screenToWorldDeltaX(-deltaY));
			}

			
			
		});
	}
	
	private void touchDownWorldSpace(float x, float y)
	{
		mEventsListeningObjs.clear();
		
		List<QuadTreeManageble> objs = new ArrayList<QuadTreeManageble>();
		if(mDrawScissor.detectIntersection(x, y, objs))
		{
			for (int i = 0; i < objs.size(); i++) {
				if(objs.get(i) instanceof Entity)
				{
					Entity obj = (Entity)objs.get(i);
					if(obj.detectTouchDown())
						mEventsListeningObjs.add(obj);
				}
			}
		}
		
		touchDownXWorld = x;
		touchDownYWorld = y;
	}
	
	private void touchUpWorldSpace(float x, float y)
	{
		for (int i = 0; i < mEventsListeningObjs.size(); i++) {
			mEventsListeningObjs.get(i).detectTouchUp();
		}
		
		if(x == touchDownXWorld && y == touchDownYWorld)
		{
			if(mEventsListeningObjs.size() == 0)
			{
				for (int i = 0; i < mListeners.size(); i++) {
					mListeners.get(i).emptyTapped();
				}
			}
			else
			{
				for (int i = 0; i < mEventsListeningObjs.size(); i++) {
					mEventsListeningObjs.get(i).detectTapped();
				}
			}
			
		}
			
	}
	
	private void touchDraggedWorldSpace(float x, float y, float deltaX, float deltaY)
	{
		for (int i = 0; i < mEventsListeningObjs.size(); i++) {
			mEventsListeningObjs.get(i).detectTouchDragged(x, y, deltaX, deltaY);
		}
	}
	
	public boolean attachDrawScissor(QuadTreeManageble obj)
	{
		return mDrawScissor.addManageble(obj);
	}
	
	public void dettachDrawScissor(QuadTreeManageble obj)
	{
		obj.dettachQuadTree(QuadTreeType.DRAW);
	}
	
	public List<QuadTreeManageble> getVisibleEntities(AxisAlignedBoundingBox scissor)
	{
		List<QuadTreeManageble> entities = new ArrayList<QuadTreeManageble>();
		mDrawScissor.detectIntersection(scissor, entities);
		return entities;
	}
	
	public void updateDrawScissor(QuadTreeManageble obj)
	{
		dettachDrawScissor(obj);
		attachDrawScissor(obj);
	}
	
	public void setRenderScene(boolean value)
	{
		mbRenderScene = value;
	}
	
	private void renderBegin()
	{
		CameraController.getInstance(CameraController.class).updateCamera();
		
		for (int i = 0; i < mListeners.size(); i++) {
			mListeners.get(i).renderBegined();
		}
	}
	
	private void renderEnd()
	{
		
		mRenderBatch.setProjectionMatrix(CameraController.getInstance(CameraController.class).getCameraCombined());
		mRenderBatch.doRender();
		
		allocIndex = 0;
//		Iterator<String> itr = mGroundDrawMap.keySet().iterator();
//		while(itr.hasNext())
//		{
//			String key = itr.next();
//			mGroundDrawMap.get(key).reset();
//		}
		
		for (int i = 0; i < mListeners.size(); i++) {
			mListeners.get(i).renderEnded();
		}
	}
	
	public void render()
	{
		renderBegin();
		if(mbRenderScene)
			renderScene();
		renderEnd();
	}
	
	private void renderScene()
	{
		//renderGround();
		
		List<QuadTreeManageble> renderList = new ArrayList<QuadTreeManageble>();
		mDrawScissor.detectIntersection(CameraController.getInstance(CameraController.class).getCameraViewAABB(), renderList);
//		System.err.println("render size: " + renderList.size());
		for (int i = 0; i < renderList.size(); i++) {
			draw((Drawable) renderList.get(i));
		}
		
		if(mbDrawGrid)
			renderGrid();
	}
	
	public void draw(Drawable draw)
	{
		mRenderBatch.addDrawable(draw);
	}
	
	public void setDrawGrid(boolean bDrawGrid)
	{
		mbDrawGrid = bDrawGrid;
	}
	
	public Grid allocGrid()
	{
		if(allocIndex >= mGridIdleList.size())
		{
			Grid grid = new Grid();
			mGridIdleList.add( grid );
		}
		
		return mGridIdleList.get(allocIndex++);
	}
	
	private void renderGrid()
	{
		AxisAlignedBoundingBox scissor = CameraController.getInstance(CameraController.class).getCameraViewAABB();
		AxisAlignedBoundingBox gridAABB = new AxisAlignedBoundingBox();
		int l = (int)(scissor.minX / Settings.UNIT);
		int r = (int)(scissor.maxX / Settings.UNIT);
		int b = (int)(scissor.minY / Settings.UNIT);
		int u = (int)(scissor.maxY / Settings.UNIT);
		for (int x = l; x <= (r+1); x ++) {
			for (int y = b; y <= (u+1); y ++) {
				Grid grid = allocGrid();
				grid.setGridPos(x, y);
				gridAABB.minX = x * Settings.UNIT + Grid.PAD;
				gridAABB.minY = y * Settings.UNIT + Grid.PAD;
				gridAABB.maxX = gridAABB.minX + Settings.UNIT - Grid.PAD * 2.0f;
				gridAABB.maxY = gridAABB.minY + Settings.UNIT - Grid.PAD * 2.0f;
				if(CollisionDetector.getInstance(CollisionDetector.class).detect(gridAABB))
					grid.setColor(1.0f, 0.0f, 0.0f, 0.3f);
				else
					grid.setColor(0.0f, 0.0f, 1.0f, 0.3f);
				draw(grid);
			}
		}
		
	}
	
//	private GroundDraw allocGroundDraw(String textureName)
//	{
//		if( !mGroundDrawMap.containsKey(textureName) )
//			mGroundDrawMap.put(textureName, new GroundDrawContainer(textureName));
//		
//		return mGroundDrawMap.get(textureName).alloc();	
//	}
	
//	private void renderGround()
//	{
//		
//		
//		AxisAlignedBoundingBox scissor = CameraController.getInstance(CameraController.class).getCameraViewAABB();
//		int l = Math.max( 0, (int)(scissor.minX / Settings.UNIT) );
//		int r = Math.min( Map.MAP_WIDTH - 1, (int)(scissor.maxX / Settings.UNIT) );
//		int b = Math.max( 0, (int)(scissor.minY / Settings.UNIT) );
//		int u = Math.min( Map.MAP_HEIGHT - 1, (int)(scissor.maxY / Settings.UNIT) );
//		for (int x = l; x <= (r); x ++) {
//			for (int y = b; y <= (u); y ++) {
//				String textureName = Map.getInstance(Map.class).getGroundMap()[x][y];
//				GroundDraw draw = allocGroundDraw(textureName);
//				draw.setGridPos(x, y);
//				draw(draw);
//			}
//		}
//		
//		
//	}
	
	class GroundDrawContainer
	{
		private List<GroundDraw> draws;
		private int allocIndex = 0;
		private String textureName;
		
		public GroundDrawContainer(String textureName)
		{
			draws = new ArrayList<GroundDraw>();
			this.textureName = textureName;
		}
		
		public GroundDraw alloc()
		{
			if(allocIndex >= draws.size())
				draws.add(new GroundDraw(textureName));
			
			return draws.get(allocIndex++);
		}
		
		public void reset()
		{
			allocIndex = 0;
		}
	}
	
	class GroundDraw implements Drawable
	{
		private Sprite sp;
		public GroundDraw(String textureName)
		{
			sp = ResourceManager.getInstance(ResourceManager.class).createSprite(textureName);
			sp.setSize(Settings.UNIT, Settings.UNIT);
		}
		
		public void setGridPos(int x, int y)
		{
			sp.setPosition(x * Settings.UNIT, y * Settings.UNIT);
		}
		
		@Override
		public void drawSelf(SpriteBatch batch) {
			sp.draw(batch);
		}

		@Override
		public float getDepth() {
			return Float.MAX_VALUE;
		}
		
	}
}
