package com.TownSimulator.utility.quadtree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.TownSimulator.utility.AxisAlignedBoundingBox;
import com.TownSimulator.utility.GameMath;

public class QuadTreeNode {
//	private static final 	long serialVersionUID = -5005395474154683597L;
	private static final 	int 						LEAF_OBJ_CNT = 100;
	private 				AxisAlignedBoundingBox 		mAABB;
	private 				QuadTreeType 				mType;
	private					List<QuadTreeManageble>		mObjs;
	private	transient		QuadTreeNode				mParent;
	private transient 		List<QuadTreeNode>			mChildren;
	
	public QuadTreeNode(QuadTreeType type, AxisAlignedBoundingBox aabb, QuadTreeNode parent)
	{
		mType = type;
		mAABB = aabb;
		mParent = parent;
		mChildren = new ArrayList<QuadTreeNode>();
		mObjs = new ArrayList<QuadTreeManageble>();
	}
	
	public AxisAlignedBoundingBox getAABB()
	{
		return mAABB;
	}
	
	public QuadTreeNode getParent()
	{
		return mParent;
	}
	
	public boolean removeManageble(QuadTreeManageble obj)
	{
		if(mChildren.isEmpty() || mChildren.get(0) == null)
		{
			return mObjs.remove(obj);
		}
		else
		{
			boolean r = false;
			for (int i = 0; i < mChildren.size(); i++) {
				boolean v = mChildren.get(i).removeManageble(obj);
				r = r || v;
			}
			
			return r;
		}
	}
	
	public boolean addManageble( QuadTreeManageble obj )
	{
		if(obj == null)
			return false;
		
		if( !GameMath.intersect(mAABB, obj.getAABBWorld(mType)) )
			return false;
		
		boolean result = false;
		//No slice, it is a leaf
		if(mChildren.isEmpty() || mChildren.get(0) == null)
		{
			if(!mObjs.contains(obj))
			{
				obj.addContainedQuadTreeNode(mType, this);
				mObjs.add(obj);
				result = true;
			}
			if(mObjs.size() > LEAF_OBJ_CNT)
			{
				doQuadSlice();
			}
		}
		//Not a leaf
		else 
		{
			for (int i = 0; i < mChildren.size(); i++) {
				boolean bAdded = mChildren.get(i).addManageble(obj);
				result = result || bAdded;
			}
		}
		
		return result;
	}
	
	public boolean detectIntersection(QuadTreeManageble obj)
	{
		return detectIntersection(obj, null);
	}
	
	public boolean detectIntersection(QuadTreeManageble obj, List<QuadTreeManageble> collideObjs)
	{
		return detectIntersection(obj, collideObjs, null);
	}
	
	public boolean detectIntersection(QuadTreeManageble obj, List<QuadTreeManageble> collideObjs, List<QuadTreeManageble> excludedObjs)
	{
		return detectIntersection(obj.getAABBWorld(mType), collideObjs, excludedObjs);
	}
	
	
	public boolean detectIntersection(AxisAlignedBoundingBox aabb)
	{
		return detectIntersection(aabb, null, null);
	}
	
	public boolean detectIntersection(AxisAlignedBoundingBox aabb, List<QuadTreeManageble> collideObjs)
	{
		return detectIntersection(aabb, collideObjs, null);
	}
	
	public boolean detectIntersection(AxisAlignedBoundingBox aabb, List<QuadTreeManageble> collideObjs, List<QuadTreeManageble> excludedObjs)
	{
		if( !GameMath.intersect(mAABB, aabb) )
			return false;
		
		boolean bCollide = false;
		//A leaf
		if(mChildren.isEmpty() ||mChildren.get(0) == null)
		{
			for (int i = 0; i < mObjs.size(); i++) {
				if(excludedObjs != null && excludedObjs.contains(mObjs.get(i)))
					continue;
				
				if( GameMath.intersect(mObjs.get(i).getAABBWorld(mType), aabb) )
				{
					if(collideObjs != null && !collideObjs.contains(mObjs.get(i)))
						collideObjs.add(mObjs.get(i));
					bCollide = true;
				}
			}
		}
		//Not a leaf
		else
		{
			for (int i = 0; i < mChildren.size(); i++) {
				if( mChildren.get(i).detectIntersection(aabb, collideObjs, excludedObjs) )
					bCollide = true;
			}
		}
		
		return bCollide;
	}
	
	public boolean detectIntersection(float x, float y)
	{
		return detectIntersection(x, y, null, null);
	}
	
	public boolean detectIntersection(float x, float y, List<QuadTreeManageble> collideObjs)
	{
		return detectIntersection(x, y, collideObjs, null);
	}
	
	public boolean detectIntersection(float x, float y, List<QuadTreeManageble> collideObjs, List<QuadTreeManageble> excludedObjs)
	{
		if( !GameMath.intersect(mAABB, x, y) )
			return false;
		
		boolean bCollide = false;
		//A leaf
		if(mChildren.isEmpty() || mChildren.get(0) == null)
		{
			for (int i = 0; i < mObjs.size(); i++) {
				if(excludedObjs != null && excludedObjs.contains(mObjs.get(i)))
					continue;
				
				if( GameMath.intersect(mObjs.get(i).getAABBWorld(mType), x, y) )
				{
					if(collideObjs != null && !collideObjs.contains(mObjs.get(i)))
						collideObjs.add(mObjs.get(i));
					bCollide = true;
				}
			}
		}
		//Not a leaf
		else
		{
			for (int i = 0; i < mChildren.size(); i++) {
				if( mChildren.get(i).detectIntersection(x, y, collideObjs, excludedObjs) )
					bCollide = true;
			}
		}
		return bCollide;
	}
	
	private void doQuadSlice()
	{
		float centerX = (mAABB.minX + mAABB.maxX) * 0.5f;
		float centerY = (mAABB.minY + mAABB.maxY) * 0.5f;
		mChildren.add(0, new QuadTreeNode( mType, new AxisAlignedBoundingBox(mAABB.minX, centerY, 	 centerX, 	 mAABB.maxY), 	this ));
		mChildren.add(1, new QuadTreeNode( mType, new AxisAlignedBoundingBox(centerX, 	 centerY, 	 mAABB.maxX, mAABB.maxY), 	this ));
		mChildren.add(2, new QuadTreeNode( mType, new AxisAlignedBoundingBox(mAABB.minX, mAABB.minY, centerX, 	 centerY), 		this ));
		mChildren.add(3, new QuadTreeNode( mType, new AxisAlignedBoundingBox(centerX, 	 mAABB.minY, mAABB.maxX, centerY),		this ));
		
		for (int i = 0; i < mObjs.size(); i++) {
			for (int j = 0; j < mChildren.size(); j++) {
				mChildren.get(j).addManageble( mObjs.get(i) );
			}
		}
		
		mObjs.clear();
		mObjs = null;
	}
	
	public void setParent(QuadTreeNode parent) {
		mParent = parent;
	}
	
	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();
		for(QuadTreeNode child : mChildren) {
			child.setParent(this);
		}
	}
}
