package com.rabrant.pickinsticks;

import java.util.Vector;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.rabrant.pickinsticks.AnimationSet;
import com.rabrant.pickinsticks.TextureSheet;

public class AnimatedTexture {
	private TextureSheet myTextureSheet;
	private Vector<AnimationSet> myAnimationSetList;
	private String myCurrentAnimation;
	private int myCurrentAnimationIndex;
	
	
	public AnimatedTexture(Texture texture, int cols, int rows) {
		setTexture(texture, cols, rows);
		myCurrentAnimationIndex = 0;
	}
	
	public int getTextureIndex()
	{
	    return myTextureSheet.getIndex();
	}
	public void setTextureIndex(int index)
	{
	    myTextureSheet.setIndex(index);
	}
	
	public Texture getTexture() {
		return myTextureSheet.getTexture();
	}
	public void setTexture(Texture texture, int cols, int rows) {
		myAnimationSetList = new Vector<AnimationSet>();
		AnimationSet allAnimationSet = new AnimationSet();
		Vector<Integer> allFrames = new Vector<Integer>();
		for (int i = 0; i < cols * rows; i++) {
			allFrames.add(i);
		}
		allAnimationSet.setName("all");
		allAnimationSet.setFrames(allFrames);
		myAnimationSetList.add(allAnimationSet);
		setCurrentAnimation("all");
		
		myTextureSheet = new TextureSheet(texture, cols, rows);
	}
	public TextureRegion getRegion() {
		return myTextureSheet.getRegion();
	}
	
	public String getCurrentAnimation() {
		return myCurrentAnimation;
	}
	public boolean setCurrentAnimation(String name) {
		boolean gotSet = false;
		
		for (int i = 0; i < myAnimationSetList.size(); i++) {
			if (myAnimationSetList.get(i).getName() == name) {
				myCurrentAnimation = name;
				myCurrentAnimationIndex = i;
				if (myTextureSheet != null) myTextureSheet.setIndex(myAnimationSetList.get(i).getFrameIndex());
				gotSet = true;
			}
		}
		return gotSet;
	}

	public void addAnimationSet(AnimationSet animationSet) {
		myAnimationSetList.add(animationSet);
	}
	public void removeAnimationSet(String name) {
		for (int i = 0; i < myAnimationSetList.size(); i++) {
			if (myAnimationSetList.get(i).getName() == name) {
				myAnimationSetList.remove(i);
			}
		}
	}
	
	public boolean animate(String name) {
		boolean gotSet = false;
		if (myCurrentAnimation != name) {
			for (int i = 0; i < myAnimationSetList.size(); i++) {
				if (myAnimationSetList.get(i).getName() == name) {
					myCurrentAnimation = name;
					myCurrentAnimationIndex = i;
					gotSet = true;
				}
			}
		} else {
			gotSet = true;
		}
		myAnimationSetList.get(myCurrentAnimationIndex).animate();
		myTextureSheet.setIndex(myAnimationSetList.get(myCurrentAnimationIndex).getFrameIndex());
		return gotSet;
	}
	public boolean animate(String name, int frame) {
		boolean gotSet = false;
		if (myCurrentAnimation != name) {
			for (int i = 0; i < myAnimationSetList.size(); i++) {
				if (myAnimationSetList.get(i).getName() == name) {
					myCurrentAnimation = name;
					myCurrentAnimationIndex = i;
					gotSet = true;
				}
			}
		} else {
			gotSet = true;
		}
		myAnimationSetList.get(myCurrentAnimationIndex).setCurFrame(frame);
		myTextureSheet.setIndex(myAnimationSetList.get(myCurrentAnimationIndex).getFrameIndex());
		return gotSet;
	}
}
