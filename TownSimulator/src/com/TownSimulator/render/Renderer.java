package com.TownSimulator.render;

import com.TownSimulator.entity.Drawable;
import com.TownSimulator.utility.AxisAlignedBoundingBox;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * 
 * Annotate me!!!
 *
 */
public class Renderer {
	private OrthographicCamera 		mCamera;
	private RenderBatch		   		mRenderBatch;
	private AxisAlignedBoundingBox	mScissorAABB;
	private static	Renderer		mInstance;
	
	public Renderer()
	{
		mRenderBatch = new RenderBatch();
		mScissorAABB = new AxisAlignedBoundingBox();
		System.out.println("Renderer Create >>>>>>>>>>>");
	}
	
	public void setCamera(OrthographicCamera camera)
	{
		mCamera = camera;
	}
	
//	public Renderer(OrthographicCamera camera)
//	{
//		mCamera = camera;
//		mRenderBatch = new RenderBatch();
//		mScissorAABB = new AxisAlignedBoundingBox();
//	}
	
	public static synchronized Renderer getInstance()
	{
		if(mInstance == null)
			mInstance = new Renderer();
		return mInstance;
	}
	
	public void renderBegin()
	{
		mCamera.update();
		updateScissor();
	}
	
	private void updateScissor()
	{
		mScissorAABB.minX = mCamera.position.x - mCamera.viewportWidth 	* 0.5f;
		mScissorAABB.minY = mCamera.position.y - mCamera.viewportHeight * 0.5f;
		mScissorAABB.maxX = mCamera.position.x + mCamera.viewportWidth 	* 0.5f;
		mScissorAABB.maxY = mCamera.position.y + mCamera.viewportHeight * 0.5f;
	}
	
	public AxisAlignedBoundingBox getScissor()
	{
		return mScissorAABB;
	}
	
	public void dispose()
	{
		mRenderBatch.dispose();
		mInstance = null;
	}
	
	public void renderEnd()
	{
		mRenderBatch.setProjectionMatrix(mCamera.combined);
		mRenderBatch.doRender();
	}
	
	public void draw(Drawable draw)
	{
		mRenderBatch.addDrawable(draw);
	}
	
//	public void addDrawContainer(GameDrawableContainer drawContainer)
//	{
//		Rectangle scissorRect = new Rectangle(
//				mCamera.position.x - mCamera.viewportWidth*0.5f,
//				mCamera.position.y - mCamera.viewportHeight*0.5f,
//				mCamera.viewportWidth, mCamera.viewportHeight);
//		drawContainer.draw(mRenderBatch, scissorRect);
//	}
}