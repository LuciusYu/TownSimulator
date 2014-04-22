package com.townSimulator.game.scene;

import com.badlogic.gdx.utils.Array;
import com.townSimulator.utility.AxisAlignedBoundingBox;

public class QuadTree {
	private AxisAlignedBoundingBox	mManagedAABB;
	private QuadTreeNode			mRoot;
	private QuadTreeType			mType;
	
	public QuadTree(QuadTreeType type, float worldMinX, float worldMinY, float worldMaxX, float worldMaxY)
	{
		mType = type;
		mManagedAABB = new AxisAlignedBoundingBox(worldMinX, worldMinY, worldMaxX, worldMaxY);
		mRoot = new QuadTreeNode(mType, mManagedAABB);
	}
	
	public boolean addManageble(QuadTreeManageble obj)
	{
		return mRoot.addIntersection(obj);
	}
	
	public boolean detectIntersection(QuadTreeManageble obj)
	{
		return mRoot.detectIntersection(obj);
	}
	
	public boolean detectIntersection(QuadTreeManageble obj, Array<QuadTreeManageble> collideObjs)
	{
		return mRoot.detectIntersection(obj, collideObjs);
	}
	
	public boolean detectIntersection(AxisAlignedBoundingBox aabb)
	{
		return mRoot.detectIntersection(aabb);
	}
	
	public boolean detectIntersection(AxisAlignedBoundingBox aabb, Array<QuadTreeManageble> collideObjs)
	{
		return mRoot.detectIntersection(aabb, collideObjs);
	}
	
	public boolean detectIntersection(float x, float y)
	{
		return mRoot.detectIntersection(x, y);
	}
	
	public boolean detectIntersection(float x, float y, Array<QuadTreeManageble> collideObjs)
	{
		return mRoot.detectIntersection(x, y, collideObjs);
	}
	
}
