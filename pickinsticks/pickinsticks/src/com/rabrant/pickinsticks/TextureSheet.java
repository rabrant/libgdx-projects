package com.rabrant.pickinsticks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class TextureSheet {
	private Texture myTexture;
	private int myCols;
	private int myRows;
	
	private int myFrameWidth;
	private int myFrameHeight;
	private int myIndex;
	private int myRegionLeft;
	private int myRegionTop;
	private TextureRegion myRegion;
	
	
	public TextureSheet(Texture texture, int cols, int rows) {
		myTexture = texture;
		myCols = cols;
		myRows = rows;
		
		myFrameWidth = texture.getWidth() / myCols;
		myFrameHeight = texture.getHeight() / myRows;
		myIndex = 0;
		myRegionLeft = 0;
		myRegionTop = 0;
		myRegion = new TextureRegion(myTexture, myRegionLeft, myRegionTop, myFrameWidth, myFrameHeight);
	}
	
	public Texture getTexture() {
		return myTexture;
	}
	public void setTexture(Texture texture) {
		myTexture = texture;
	}
	
	public int getCols() {
		return myCols;
	}
	public void setCols(int cols) {
		myCols = cols;
	}
	
	public int getRows() {
		return myRows;
	}
	public void setRows(int rows) {
		myRows = rows;
	}
	
	public int getIndex()
	{
	    return myIndex;
	}
	public void setIndex(int index)
	{
	    myIndex = index;

	    int frameCol = myIndex % myCols;
	    int frameRow = myIndex / myCols;

	    myRegionLeft = frameCol * myFrameWidth;
	    myRegionTop = frameRow * myFrameHeight;
	    myRegion.setRegion(myRegionLeft, myRegionTop, myFrameWidth, myFrameHeight);
	}
	
	public TextureRegion getRegion() {
		return myRegion;
	}
	public int getFrameWidth() {
		return myFrameWidth;
	}
	public int getFrameHeight() {
		return myFrameHeight;
	}
}
